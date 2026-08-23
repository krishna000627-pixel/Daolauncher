package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DaoLauncherDatabase
import com.example.data.model.AlchemyPill
import com.example.data.model.AppInfo
import com.example.data.model.AppRestriction
import com.example.data.model.CultivationProfile
import com.example.data.model.CultivationRealm
import com.example.data.model.CultivationTask
import com.example.data.model.FocusSession
import com.example.data.model.PinnedApp
import com.example.data.repository.AppLauncherRepository
import com.example.data.repository.BreakthroughResult
import com.example.data.repository.CultivationRepository
import com.example.ui.components.AppRoleOption
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LauncherTab {
    HOME,
    QUESTS,
    MEDITATION,
    PAVILION,
    CALCULATOR,
    DRAWER
}

enum class BreathingPhase(val label: String, val seconds: Int) {
    INHALE("Inhale Celestial Qi", 4),
    HOLD("Compress Qi in Dantian", 4),
    EXHALE("Exhale Worldly Impurities", 4),
    STILLNESS("Primordial Void Stillness", 4)
}

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DaoLauncherDatabase.getDatabase(application)
    private val cultivationRepo = CultivationRepository(db.cultivationDao())
    val launcherRepo = AppLauncherRepository(application)

    val profile: StateFlow<CultivationProfile?> = cultivationRepo.profileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allTasks: StateFlow<List<CultivationTask>> = cultivationRepo.allTasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPills: StateFlow<List<AlchemyPill>> = cultivationRepo.allPillsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentFocusSessions: StateFlow<List<FocusSession>> = cultivationRepo.recentFocusSessionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pinnedApps: StateFlow<List<PinnedApp>> = cultivationRepo.pinnedAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val restrictions: StateFlow<List<AppRestriction>> = cultivationRepo.restrictionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Installed apps
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    // App Drawer Search query & Category filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedAppCategory = MutableStateFlow("All")
    val selectedAppCategory: StateFlow<String> = _selectedAppCategory.asStateFlow()

    // Filtered apps (Conceals hidden apps from standard drawer & search)
    val filteredApps: StateFlow<List<AppInfo>> = combine(
        _installedApps,
        _searchQuery,
        _selectedAppCategory
    ) { apps, query, category ->
        apps.filter { app ->
            // Hidden apps are exclusively in the secret calculator vault
            if (app.isHidden) return@filter false

            val matchesQuery = query.isBlank() || app.label.contains(query, ignoreCase = true) || app.packageName.contains(query, ignoreCase = true)
            val matchesCategory = when (category) {
                "All" -> true
                "Favorites" -> app.isPinned
                "Study" -> app.isStudy
                "Distraction" -> app.isDistraction
                else -> app.category.equals(category, ignoreCase = true)
            }
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dock apps (pinned or fallback, excluding hidden apps)
    val dockApps: StateFlow<List<AppInfo>> = combine(_installedApps, pinnedApps) { apps, pinned ->
        val visibleApps = apps.filter { !it.isHidden }
        val pinnedMap = pinned.associateBy { it.packageName }
        val explicitlyPinned = visibleApps.filter { pinnedMap.containsKey(it.packageName) }
            .sortedBy { pinnedMap[it.packageName]?.orderIndex ?: 0 }

        if (explicitlyPinned.isNotEmpty()) {
            explicitlyPinned.take(5)
        } else {
            val defaults = mutableListOf<AppInfo>()
            visibleApps.find { it.category == "Communication" }?.let { defaults.add(it) }
            visibleApps.find { it.category == "Browser" }?.let { defaults.add(it) }
            visibleApps.find { it.category == "Media" }?.let { defaults.add(it) }
            visibleApps.find { it.category == "Productivity" && !defaults.contains(it) }?.let { defaults.add(it) }
            if (defaults.isEmpty()) visibleApps.take(4) else defaults.take(5)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Navigation Tab
    private val _currentTab = MutableStateFlow(LauncherTab.HOME)
    val currentTab: StateFlow<LauncherTab> = _currentTab.asStateFlow()

    // Default Launcher Status
    private val _isDefaultLauncher = MutableStateFlow(false)
    val isDefaultLauncher: StateFlow<Boolean> = _isDefaultLauncher.asStateFlow()

    // Dialog & Interaction states
    private val _showBreakthroughDialog = MutableStateFlow(false)
    val showBreakthroughDialog: StateFlow<Boolean> = _showBreakthroughDialog.asStateFlow()

    private val _showInscribeQuestDialog = MutableStateFlow(false)
    val showInscribeQuestDialog: StateFlow<Boolean> = _showInscribeQuestDialog.asStateFlow()

    private val _showAlchemyDialog = MutableStateFlow(false)
    val showAlchemyDialog: StateFlow<Boolean> = _showAlchemyDialog.asStateFlow()

    private val _distractionBlockerTargetApp = MutableStateFlow<AppInfo?>(null)
    val distractionBlockerTargetApp: StateFlow<AppInfo?> = _distractionBlockerTargetApp.asStateFlow()

    private val _categorizeTargetApp = MutableStateFlow<AppInfo?>(null)
    val categorizeTargetApp: StateFlow<AppInfo?> = _categorizeTargetApp.asStateFlow()

    private val _floatingToastMessage = MutableStateFlow<String?>(null)
    val floatingToastMessage: StateFlow<String?> = _floatingToastMessage.asStateFlow()

    private val _isCultivatingBreakthrough = MutableStateFlow(false)
    val isCultivatingBreakthrough: StateFlow<Boolean> = _isCultivatingBreakthrough.asStateFlow()

    private val _lastBreakthroughResult = MutableStateFlow<BreakthroughResult?>(null)
    val lastBreakthroughResult: StateFlow<BreakthroughResult?> = _lastBreakthroughResult.asStateFlow()

    private val _daoWisdomQuote = MutableStateFlow(
        "A journey of a thousand li begins beneath one's feet. Maintain your Dantian focus."
    )
    val daoWisdomQuote: StateFlow<String> = _daoWisdomQuote.asStateFlow()

    // Calculator Passcode
    val calculatorPasscode: StateFlow<String> = combine(profile) { prof ->
        prof[0]?.calculatorPasscode ?: "8888"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "8888")

    // Meditation Timer State
    private val _meditationDurationMin = MutableStateFlow(25)
    val meditationDurationMin: StateFlow<Int> = _meditationDurationMin.asStateFlow()

    private val _meditationRemainingSeconds = MutableStateFlow(25 * 60)
    val meditationRemainingSeconds: StateFlow<Int> = _meditationRemainingSeconds.asStateFlow()

    private val _isMeditating = MutableStateFlow(false)
    val isMeditating: StateFlow<Boolean> = _isMeditating.asStateFlow()

    private val _breathingPhase = MutableStateFlow(BreathingPhase.INHALE)
    val breathingPhase: StateFlow<BreathingPhase> = _breathingPhase.asStateFlow()

    private val _breathingPhaseSecondsLeft = MutableStateFlow(4)
    val breathingPhaseSecondsLeft: StateFlow<Int> = _breathingPhaseSecondsLeft.asStateFlow()

    private val _meditationCompletedReward = MutableStateFlow<Pair<Int, Int>?>(null)
    val meditationCompletedReward: StateFlow<Pair<Int, Int>?> = _meditationCompletedReward.asStateFlow()

    private var meditationTimerJob: Job? = null

    private val daoQuotes = listOf(
        "To the mind that is still, the entire universe surrenders.",
        "A tree that can fill the span of a man's arms grows from a tiny sprout.",
        "Knowing others is intelligence; knowing yourself is true wisdom.",
        "Mastering others is strength; mastering yourself is true power.",
        "He who conquers procrastination refines his golden core.",
        "Flow like water, penetrate mountains with persistent devotion.",
        "The Heavenly Tribulation tests only those whose ambition touches the stars.",
        "Simplicity and single-pointed focus are the true foundation of Daoist immortality."
    )

    init {
        viewModelScope.launch {
            cultivationRepo.checkDailyStreak()
            refreshInstalledApps()
            checkLauncherStatus()
        }

        viewModelScope.launch {
            combine(pinnedApps, restrictions) { _, _ -> }.collect {
                refreshInstalledApps()
            }
        }
    }

    fun setTab(tab: LauncherTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedAppCategory(category: String) {
        _selectedAppCategory.value = category
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            val pinnedList = pinnedApps.value.map { it.packageName }.toSet()
            val restrMap = restrictions.value.associateBy { it.packageName }
            val apps = launcherRepo.getInstalledApps(pinnedList, restrMap)
            _installedApps.value = apps
        }
    }

    fun checkLauncherStatus() {
        _isDefaultLauncher.value = launcherRepo.isDefaultLauncher()
    }

    // App Launch with Blocker & Study Coin Rewards
    fun launchApp(app: AppInfo) {
        // If app is a Distraction and not actively unlocked -> show Distraction Blocker
        if (app.isDistraction && !app.isCurrentlyUnlocked) {
            _distractionBlockerTargetApp.value = app
            return
        }

        // If app is a Study App -> grant coins and Qi quest bounty!
        if (app.isStudy) {
            viewModelScope.launch {
                val rewards = cultivationRepo.rewardStudyAppLaunch(app.packageName)
                showToast("📜 Sacred Dao Scripture Studied! +${rewards.second} Coins (Spirit Stones) & +${rewards.first} Qi!")
            }
        }

        launcherRepo.launchApp(app)
    }

    fun unlockDistractionAndLaunch(app: AppInfo, minutes: Int, cost: Int) {
        viewModelScope.launch {
            val success = cultivationRepo.unlockDistractionApp(app.packageName, minutes, cost)
            if (success) {
                showToast("🔓 Unlocked ${app.label} for $minutes mins (-$cost Spirit Stones)")
                launcherRepo.launchApp(app)
                _distractionBlockerTargetApp.value = null
                refreshInstalledApps()
            } else {
                showToast("⚠️ Insufficient Spirit Stones! Complete Sect Quests or Study Sacred Apps.")
            }
        }
    }

    fun resistDistraction(app: AppInfo) {
        viewModelScope.launch {
            val qiGained = cultivationRepo.rewardWillpowerResistance()
            showToast("⚡ Dao Heart Fortified! +$qiGained Qi for conquering mortal temptation!")
            _distractionBlockerTargetApp.value = null
        }
    }

    fun closeDistractionBlockerDialog() {
        _distractionBlockerTargetApp.value = null
    }

    fun openAppCategorizationDialog(app: AppInfo) {
        _categorizeTargetApp.value = app
    }

    fun closeAppCategorizationDialog() {
        _categorizeTargetApp.value = null
    }

    fun setAppClassification(app: AppInfo, option: AppRoleOption) {
        viewModelScope.launch {
            when (option) {
                AppRoleOption.NORMAL -> {
                    cultivationRepo.setAppDistractionStatus(app.packageName, false)
                    cultivationRepo.setAppStudyStatus(app.packageName, false)
                    cultivationRepo.setAppHiddenStatus(app.packageName, false)
                }
                AppRoleOption.DISTRACTION -> {
                    cultivationRepo.setAppDistractionStatus(app.packageName, true)
                    cultivationRepo.setAppStudyStatus(app.packageName, false)
                    cultivationRepo.setAppHiddenStatus(app.packageName, false)
                }
                AppRoleOption.STUDY -> {
                    cultivationRepo.setAppStudyStatus(app.packageName, true)
                    cultivationRepo.setAppDistractionStatus(app.packageName, false)
                    cultivationRepo.setAppHiddenStatus(app.packageName, false)
                }
                AppRoleOption.HIDDEN -> {
                    cultivationRepo.setAppHiddenStatus(app.packageName, true)
                }
            }
            showToast("Updated ${app.label} to ${option.title}")
            refreshInstalledApps()
        }
    }

    fun setAppHiddenStatus(packageName: String, isHidden: Boolean) {
        viewModelScope.launch {
            cultivationRepo.setAppHiddenStatus(packageName, isHidden)
            refreshInstalledApps()
        }
    }

    fun updateCalculatorPasscode(newPin: String) {
        viewModelScope.launch {
            cultivationRepo.updateCalculatorPasscode(newPin)
            showToast("🔑 Secret Vault PIN updated successfully!")
        }
    }

    fun showToast(msg: String) {
        viewModelScope.launch {
            _floatingToastMessage.value = msg
            delay(3500)
            if (_floatingToastMessage.value == msg) {
                _floatingToastMessage.value = null
            }
        }
    }

    fun dismissToast() {
        _floatingToastMessage.value = null
    }

    fun openAppDetails(packageName: String) {
        launcherRepo.openAppDetails(packageName)
    }

    fun openDefaultLauncherSettings() {
        launcherRepo.openDefaultLauncherSettings()
    }

    fun togglePinApp(app: AppInfo) {
        viewModelScope.launch {
            if (app.isPinned) {
                cultivationRepo.unpinApp(app.packageName)
            } else {
                val nextOrder = pinnedApps.value.size
                cultivationRepo.pinApp(app.packageName, nextOrder)
            }
            refreshInstalledApps()
        }
    }

    // Quest Actions
    fun completeTask(task: CultivationTask) {
        viewModelScope.launch {
            cultivationRepo.completeTask(task)
        }
    }

    fun uncompleteTask(task: CultivationTask) {
        viewModelScope.launch {
            cultivationRepo.uncompleteTask(task)
        }
    }

    fun insertTask(task: CultivationTask) {
        viewModelScope.launch {
            cultivationRepo.insertTask(task)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            cultivationRepo.deleteTask(taskId)
        }
    }

    // Dialog Controls
    fun openBreakthroughDialog() {
        _lastBreakthroughResult.value = null
        _showBreakthroughDialog.value = true
    }

    fun closeBreakthroughDialog() {
        _showBreakthroughDialog.value = false
        _lastBreakthroughResult.value = null
    }

    fun openInscribeQuestDialog() {
        _showInscribeQuestDialog.value = true
    }

    fun closeInscribeQuestDialog() {
        _showInscribeQuestDialog.value = false
    }

    fun openAlchemyDialog() {
        _showAlchemyDialog.value = true
    }

    fun closeAlchemyDialog() {
        _showAlchemyDialog.value = false
    }

    fun cycleWisdomQuote() {
        val nextQuote = daoQuotes.random()
        _daoWisdomQuote.value = nextQuote
    }

    // Breakthrough Logic
    fun attemptBreakthrough(useBreakthroughPill: Boolean) {
        viewModelScope.launch {
            _isCultivatingBreakthrough.value = true
            delay(1800)
            val result = cultivationRepo.attemptBreakthrough(useBreakthroughPill)
            _isCultivatingBreakthrough.value = false
            _lastBreakthroughResult.value = result
        }
    }

    // Alchemy Actions
    fun craftPill(pill: AlchemyPill) {
        viewModelScope.launch {
            cultivationRepo.craftPill(pill)
        }
    }

    fun consumePill(pill: AlchemyPill) {
        viewModelScope.launch {
            cultivationRepo.consumePill(pill)
        }
    }

    // Meditation / Focus Timer Logic
    fun setMeditationDuration(minutes: Int) {
        if (!_isMeditating.value) {
            _meditationDurationMin.value = minutes
            _meditationRemainingSeconds.value = minutes * 60
        }
    }

    fun startMeditation() {
        if (_isMeditating.value) return
        _isMeditating.value = true
        _meditationCompletedReward.value = null

        meditationTimerJob?.cancel()
        meditationTimerJob = viewModelScope.launch {
            var phaseIndex = 0
            val phases = BreathingPhase.entries
            var phaseTimeLeft = phases[0].seconds
            _breathingPhase.value = phases[0]
            _breathingPhaseSecondsLeft.value = phaseTimeLeft

            while (_isMeditating.value && _meditationRemainingSeconds.value > 0) {
                delay(1000)
                _meditationRemainingSeconds.value -= 1

                phaseTimeLeft -= 1
                if (phaseTimeLeft <= 0) {
                    phaseIndex = (phaseIndex + 1) % phases.size
                    _breathingPhase.value = phases[phaseIndex]
                    phaseTimeLeft = phases[phaseIndex].seconds
                }
                _breathingPhaseSecondsLeft.value = phaseTimeLeft
            }

            if (_meditationRemainingSeconds.value <= 0) {
                _isMeditating.value = false
                val reward = cultivationRepo.recordFocusSession(
                    _meditationDurationMin.value,
                    "MIND_BREATHING"
                )
                _meditationCompletedReward.value = reward
                _meditationRemainingSeconds.value = _meditationDurationMin.value * 60
            }
        }
    }

    fun pauseOrCancelMeditation() {
        _isMeditating.value = false
        meditationTimerJob?.cancel()
        _meditationRemainingSeconds.value = _meditationDurationMin.value * 60
    }

    fun dismissMeditationReward() {
        _meditationCompletedReward.value = null
    }

    fun toggleZenMode() {
        viewModelScope.launch {
            val p = profile.value ?: return@launch
            cultivationRepo.setZenMode(!p.zenModeEnabled)
        }
    }
}
