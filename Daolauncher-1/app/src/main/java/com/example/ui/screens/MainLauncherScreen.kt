package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppInfo
import com.example.data.model.CultivationProfile
import com.example.data.model.CultivationRealm
import com.example.ui.components.AppCategorizationDialog
import com.example.ui.components.BreakthroughDialog
import com.example.ui.components.DistractionBlockerDialog
import com.example.ui.components.InscribeQuestDialog
import com.example.ui.components.PillCraftingDialog
import com.example.ui.components.QiWispsBackground
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.JadeQiGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark
import com.example.ui.viewmodel.LauncherTab
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun MainLauncherScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val profileState by viewModel.profile.collectAsStateWithLifecycle()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val allPills by viewModel.allPills.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val filteredApps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val dockApps by viewModel.dockApps.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedAppCategory.collectAsStateWithLifecycle()

    val showBreakthrough by viewModel.showBreakthroughDialog.collectAsStateWithLifecycle()
    val showInscribeQuest by viewModel.showInscribeQuestDialog.collectAsStateWithLifecycle()
    val showAlchemy by viewModel.showAlchemyDialog.collectAsStateWithLifecycle()
    val distractionTargetApp by viewModel.distractionBlockerTargetApp.collectAsStateWithLifecycle()
    val categorizeTargetApp by viewModel.categorizeTargetApp.collectAsStateWithLifecycle()
    val floatingToastMessage by viewModel.floatingToastMessage.collectAsStateWithLifecycle()

    val isCultivatingBreakthrough by viewModel.isCultivatingBreakthrough.collectAsStateWithLifecycle()
    val lastBreakthroughResult by viewModel.lastBreakthroughResult.collectAsStateWithLifecycle()

    val profile = profileState ?: CultivationProfile(
        id = 1,
        realmLevel = 1,
        realmSubStage = 1,
        currentQi = 80L,
        maxQi = 300L,
        spiritStones = 120L,
        daoTitle = "Dao Apprentice"
    )

    val realm = profile.currentRealm
    val breakthroughPill = allPills.find { it.id == "breakthrough_pill" }
    val breakthroughPillCount = breakthroughPill?.count ?: 0

    Box(modifier = modifier.fillMaxSize()) {
        // Celestial Qi Background
        QiWispsBackground(realm = realm)

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.statusBars,
            bottomBar = {
                LauncherBottomBar(
                    currentTab = currentTab,
                    realm = realm,
                    dockApps = dockApps,
                    onTabSelected = { viewModel.setTab(it) },
                    onLaunchApp = { viewModel.launchApp(it) },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        LauncherTab.HOME -> {
                            ImmortalHomeScreen(
                                viewModel = viewModel,
                                profile = profile,
                                tasks = allTasks,
                                dockApps = dockApps
                            )
                        }
                        LauncherTab.QUESTS -> {
                            QuestBoardScreen(
                                viewModel = viewModel,
                                profile = profile,
                                tasks = allTasks
                            )
                        }
                        LauncherTab.MEDITATION -> {
                            MeditationScreen(
                                viewModel = viewModel,
                                profile = profile
                            )
                        }
                        LauncherTab.PAVILION -> {
                            PavilionScreen(
                                viewModel = viewModel,
                                profile = profile,
                                pills = allPills
                            )
                        }
                        LauncherTab.CALCULATOR -> {
                            DaoCalculatorVaultScreen(
                                viewModel = viewModel
                            )
                        }
                        LauncherTab.DRAWER -> {
                            AppDrawerScreen(
                                viewModel = viewModel,
                                apps = filteredApps,
                                searchQuery = searchQuery,
                                selectedCategory = selectedCategory
                            )
                        }
                    }
                }
            }
        }

        // Floating Daoist Notification / Toast
        AnimatedVisibility(
            visible = floatingToastMessage != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 20.dp, end = 20.dp)
        ) {
            floatingToastMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(VoidDark)
                        .border(1.2.dp, CelestialGold, RoundedCornerShape(16.dp))
                        .clickable { viewModel.dismissToast() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CelestialGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = msg,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Distraction Blocker Dialog
        distractionTargetApp?.let { app ->
            DistractionBlockerDialog(
                app = app,
                currentSpiritStones = profile.spiritStones,
                realm = realm,
                onUnlockAndLaunch = { minutes, cost ->
                    viewModel.unlockDistractionAndLaunch(app, minutes, cost)
                },
                onResistTemptation = {
                    viewModel.resistDistraction(app)
                },
                onDismiss = {
                    viewModel.closeDistractionBlockerDialog()
                }
            )
        }

        // App Role & Classification Dialog
        categorizeTargetApp?.let { app ->
            AppCategorizationDialog(
                app = app,
                onSelectRole = { role ->
                    viewModel.setAppClassification(app, role)
                },
                onDismiss = {
                    viewModel.closeAppCategorizationDialog()
                }
            )
        }

        // Breakthrough Dialog
        if (showBreakthrough) {
            BreakthroughDialog(
                profile = profile,
                breakthroughPillCount = breakthroughPillCount,
                isCultivatingBreakthrough = isCultivatingBreakthrough,
                lastBreakthroughResult = lastBreakthroughResult,
                onDismiss = { viewModel.closeBreakthroughDialog() },
                onAttemptBreakthrough = { usePill ->
                    viewModel.attemptBreakthrough(usePill)
                }
            )
        }

        // Inscribe Quest Dialog
        if (showInscribeQuest) {
            InscribeQuestDialog(
                onDismiss = { viewModel.closeInscribeQuestDialog() },
                onSaveTask = { task ->
                    viewModel.insertTask(task)
                }
            )
        }

        // Alchemy Pill Crafting Dialog
        if (showAlchemy) {
            PillCraftingDialog(
                profile = profile,
                pills = allPills,
                onDismiss = { viewModel.closeAlchemyDialog() },
                onCraftPill = { pill -> viewModel.craftPill(pill) },
                onConsumePill = { pill -> viewModel.consumePill(pill) }
            )
        }
    }
}

@Composable
fun LauncherBottomBar(
    currentTab: LauncherTab,
    realm: CultivationRealm,
    dockApps: List<AppInfo>,
    onTabSelected: (LauncherTab) -> Unit,
    onLaunchApp: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(VoidDark.copy(alpha = 0.95f))
            .border(0.8.dp, VoidBorder.copy(alpha = 0.5f))
    ) {
        // If on Home Screen, show Quick Dock row above navigation
        if (currentTab == LauncherTab.HOME && dockApps.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                dockApps.take(4).forEach { app ->
                    val iconBitmap = remember(app.icon) {
                        app.icon?.let { drawable ->
                            try {
                                drawable.toBitmap(width = 84, height = 84, config = Bitmap.Config.ARGB_8888).asImageBitmap()
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(VoidCard)
                            .border(
                                0.8.dp,
                                when {
                                    app.isStudy -> JadeQiGreen.copy(alpha = 0.8f)
                                    app.isDistraction && !app.isCurrentlyUnlocked -> Color(0xFFEF4444).copy(alpha = 0.8f)
                                    else -> VoidBorder
                                },
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onLaunchApp(app) }
                            .padding(4.dp)
                            .testTag("dock_app_${app.packageName}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (iconBitmap != null) {
                            Image(
                                bitmap = iconBitmap,
                                contentDescription = app.label,
                                modifier = Modifier.size(34.dp)
                            )
                        } else {
                            Text(
                                text = app.label.take(1).uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialGold
                            )
                        }

                        if (app.isStudy) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(JadeQiGreen)
                            )
                        } else if (app.isDistraction && !app.isCurrentlyUnlocked) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                        }
                    }
                }

                // Quick Calculator / Vault Shortcut in Dock
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VoidCard)
                        .border(1.dp, CelestialGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(LauncherTab.CALCULATOR) }
                        .testTag("dock_calculator_shortcut"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Calculator",
                        tint = CelestialGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Quick Drawer Icon in Dock
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(realm.primaryColor.copy(alpha = 0.25f))
                        .border(1.dp, realm.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(LauncherTab.DRAWER) }
                        .testTag("dock_open_drawer_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = "All Apps",
                        tint = realm.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Standard Navigation Bar
        NavigationBar(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            tonalElevation = 0.dp,
            modifier = Modifier.height(64.dp)
        ) {
            NavigationBarItem(
                selected = currentTab == LauncherTab.HOME,
                onClick = { onTabSelected(LauncherTab.HOME) },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Sanctum", fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = CelestialGold,
                    indicatorColor = CelestialGold,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_tab_home")
            )

            NavigationBarItem(
                selected = currentTab == LauncherTab.QUESTS,
                onClick = { onTabSelected(LauncherTab.QUESTS) },
                icon = { Icon(Icons.Default.Assignment, contentDescription = "Quests") },
                label = { Text("Quests", fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = CelestialGold,
                    indicatorColor = CelestialGold,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_tab_quests")
            )

            NavigationBarItem(
                selected = currentTab == LauncherTab.MEDITATION,
                onClick = { onTabSelected(LauncherTab.MEDITATION) },
                icon = { Icon(Icons.Default.SelfImprovement, contentDescription = "Focus") },
                label = { Text("Meditation", fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = CelestialGold,
                    indicatorColor = CelestialGold,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_tab_meditation")
            )

            NavigationBarItem(
                selected = currentTab == LauncherTab.CALCULATOR,
                onClick = { onTabSelected(LauncherTab.CALCULATOR) },
                icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculator") },
                label = { Text("Calculator", fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = CelestialGold,
                    indicatorColor = CelestialGold,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_tab_calculator")
            )

            NavigationBarItem(
                selected = currentTab == LauncherTab.PAVILION,
                onClick = { onTabSelected(LauncherTab.PAVILION) },
                icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = "Pavilion") },
                label = { Text("Pavilion", fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = CelestialGold,
                    indicatorColor = CelestialGold,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_tab_pavilion")
            )

            NavigationBarItem(
                selected = currentTab == LauncherTab.DRAWER,
                onClick = { onTabSelected(LauncherTab.DRAWER) },
                icon = { Icon(Icons.Default.Apps, contentDescription = "Apps") },
                label = { Text("Apps", fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = CelestialGold,
                    indicatorColor = CelestialGold,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_tab_drawer")
            )
        }
    }
}
