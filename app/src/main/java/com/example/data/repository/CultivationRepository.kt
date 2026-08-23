package com.example.data.repository

import com.example.data.local.CultivationDao
import com.example.data.model.AlchemyPill
import com.example.data.model.AppRestriction
import com.example.data.model.CultivationProfile
import com.example.data.model.CultivationRealm
import com.example.data.model.CultivationTask
import com.example.data.model.FocusSession
import com.example.data.model.PinnedApp
import com.example.data.model.TaskGrade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CultivationRepository(private val dao: CultivationDao) {

    val profileFlow: Flow<CultivationProfile?> = dao.getProfile()
    val allTasksFlow: Flow<List<CultivationTask>> = dao.getAllTasks()
    val activeTasksFlow: Flow<List<CultivationTask>> = dao.getActiveTasks()
    val completedTasksFlow: Flow<List<CultivationTask>> = dao.getCompletedTasks()
    val allPillsFlow: Flow<List<AlchemyPill>> = dao.getAllPills()
    val recentFocusSessionsFlow: Flow<List<FocusSession>> = dao.getRecentFocusSessions()
    val pinnedAppsFlow: Flow<List<PinnedApp>> = dao.getPinnedApps()
    val restrictionsFlow: Flow<List<AppRestriction>> = dao.getAllRestrictions()

    suspend fun getProfile(): CultivationProfile {
        val existing = dao.getProfileDirect()
        if (existing != null) return existing

        val initial = CultivationProfile(
            id = 1,
            realmLevel = 1,
            realmSubStage = 1,
            currentQi = 80L,
            maxQi = 300L,
            spiritStones = 120L,
            totalTasksCompleted = 0,
            totalFocusMinutes = 0L,
            consecutiveDaoDays = 1,
            lastActiveDate = getTodayDateString(),
            daoTitle = "Dao Apprentice"
        )
        dao.insertOrUpdateProfile(initial)
        return initial
    }

    suspend fun checkDailyStreak() {
        val profile = getProfile()
        val today = getTodayDateString()
        if (profile.lastActiveDate.isEmpty()) {
            dao.insertOrUpdateProfile(profile.copy(lastActiveDate = today, consecutiveDaoDays = 1))
        } else if (profile.lastActiveDate != today) {
            val isNextDay = isConsecutiveDay(profile.lastActiveDate, today)
            val newStreak = if (isNextDay) profile.consecutiveDaoDays + 1 else 1
            val bonusStones = if (isNextDay) 30L else 10L
            dao.insertOrUpdateProfile(
                profile.copy(
                    lastActiveDate = today,
                    consecutiveDaoDays = newStreak,
                    spiritStones = profile.spiritStones + bonusStones
                )
            )
        }
    }

    suspend fun completeTask(task: CultivationTask): Pair<Long, Long> {
        val profile = getProfile()
        val now = System.currentTimeMillis()

        // Check if active buff is active
        val hasQiBuff = profile.activeBuffPill == "qi_gathering_pill" && profile.buffExpiresAt > now
        val qiMultiplier = if (hasQiBuff) 2.0 else 1.0

        val qiEarned = (task.grade.qiReward * qiMultiplier).toLong()
        val stonesEarned = task.grade.stoneReward.toLong()

        dao.setTaskCompleted(task.id, true, now)

        val updatedQi = profile.currentQi + qiEarned
        val updatedStones = profile.spiritStones + stonesEarned
        val updatedTasksCount = profile.totalTasksCompleted + 1

        dao.insertOrUpdateProfile(
            profile.copy(
                currentQi = updatedQi,
                spiritStones = updatedStones,
                totalTasksCompleted = updatedTasksCount
            )
        )

        return Pair(qiEarned, stonesEarned)
    }

    suspend fun uncompleteTask(task: CultivationTask) {
        val profile = getProfile()
        dao.setTaskCompleted(task.id, false, null)
        val reducedTasksCount = (profile.totalTasksCompleted - 1).coerceAtLeast(0)
        dao.insertOrUpdateProfile(profile.copy(totalTasksCompleted = reducedTasksCount))
    }

    suspend fun insertTask(task: CultivationTask): Long {
        return dao.insertTask(task)
    }

    suspend fun deleteTask(taskId: Long) {
        dao.deleteTask(taskId)
    }

    suspend fun attemptBreakthrough(useBreakthroughPill: Boolean): BreakthroughResult {
        val profile = getProfile()
        val currentRealm = profile.currentRealm

        if (profile.currentQi < profile.maxQi) {
            return BreakthroughResult.NotEnoughQi(profile.currentQi, profile.maxQi)
        }

        val baseSuccessChance = when (profile.realmSubStage) {
            1 -> 0.95 // Early -> Mid
            2 -> 0.85 // Mid -> Late
            3 -> 0.75 // Late -> Peak
            else -> 0.65 // Peak -> Next Major Realm
        }

        val actualSuccessChance = if (useBreakthroughPill) 1.0 else baseSuccessChance
        val isSuccessful = Math.random() <= actualSuccessChance

        if (useBreakthroughPill) {
            consumePillItem("breakthrough_pill")
        }

        if (isSuccessful) {
            var newSubStage = profile.realmSubStage + 1
            var newRealmLevel = profile.realmLevel
            var newTitle = profile.daoTitle

            if (newSubStage > 4) {
                // Advance to next major realm!
                newSubStage = 1
                newRealmLevel = (profile.realmLevel + 1).coerceAtMost(9)
                newTitle = getDaoTitleForRealm(newRealmLevel)
            }

            val nextRealm = CultivationRealm.fromLevel(newRealmLevel)
            val subStageMultiplier = when (newSubStage) {
                1 -> 1.0
                2 -> 1.25
                3 -> 1.6
                else -> 2.0
            }
            val newMaxQi = (nextRealm.maxQi * subStageMultiplier / 2.0).toLong().coerceAtLeast(100L)
            val remainingQi = (profile.currentQi - profile.maxQi).coerceAtLeast(0L)

            val updatedProfile = profile.copy(
                realmLevel = newRealmLevel,
                realmSubStage = newSubStage,
                currentQi = remainingQi,
                maxQi = newMaxQi,
                tribulationBreakthroughCount = profile.tribulationBreakthroughCount + 1,
                daoTitle = newTitle,
                spiritStones = profile.spiritStones + 100L // Breakthrough celestial gift
            )
            dao.insertOrUpdateProfile(updatedProfile)

            val isMajorRealmAdvance = profile.realmSubStage == 4
            return BreakthroughResult.Success(
                newRealm = nextRealm,
                newSubStage = newSubStage,
                isMajorAscension = isMajorRealmAdvance
            )
        } else {
            // Minor setback (Qi fluctuation, lose 15% Qi)
            val lostQi = (profile.maxQi * 0.15).toLong()
            val remainingQi = (profile.currentQi - lostQi).coerceAtLeast(0L)
            dao.insertOrUpdateProfile(profile.copy(currentQi = remainingQi))
            return BreakthroughResult.Failed(lostQi = lostQi)
        }
    }

    suspend fun craftPill(pill: AlchemyPill): Boolean {
        val profile = getProfile()
        if (profile.spiritStones < pill.stoneCost || profile.currentQi < pill.qiCost) {
            return false
        }

        val updatedProfile = profile.copy(
            spiritStones = profile.spiritStones - pill.stoneCost,
            currentQi = profile.currentQi - pill.qiCost
        )
        dao.insertOrUpdateProfile(updatedProfile)

        val updatedPill = pill.copy(count = pill.count + 1)
        dao.updatePill(updatedPill)
        return true
    }

    suspend fun consumePill(pill: AlchemyPill): Boolean {
        if (pill.count <= 0) return false
        val profile = getProfile()
        val now = System.currentTimeMillis()

        dao.updatePill(pill.copy(count = pill.count - 1))

        when (pill.id) {
            "qi_gathering_pill" -> {
                val expiresAt = now + (pill.durationMinutes * 60 * 1000L)
                dao.insertOrUpdateProfile(
                    profile.copy(
                        activeBuffPill = pill.id,
                        buffExpiresAt = expiresAt
                    )
                )
            }
            "mind_clearing_elixir" -> {
                val expiresAt = now + (pill.durationMinutes * 60 * 1000L)
                dao.insertOrUpdateProfile(
                    profile.copy(
                        activeBuffPill = pill.id,
                        buffExpiresAt = expiresAt
                    )
                )
            }
            "heavenly_tea" -> {
                dao.insertOrUpdateProfile(
                    profile.copy(
                        spiritStones = profile.spiritStones + 120L,
                        currentQi = profile.currentQi + 80L
                    )
                )
            }
            else -> {}
        }
        return true
    }

    private suspend fun consumePillItem(pillId: String) {
        val pill = dao.getPill(pillId)
        if (pill != null && pill.count > 0) {
            dao.updatePill(pill.copy(count = pill.count - 1))
        }
    }

    suspend fun recordFocusSession(minutes: Int, mode: String): Pair<Int, Int> {
        val profile = getProfile()
        val now = System.currentTimeMillis()

        val hasMindBuff = profile.activeBuffPill == "mind_clearing_elixir" && profile.buffExpiresAt > now
        val multiplier = if (hasMindBuff) 1.5 else 1.0

        val baseQiPerMin = 4
        val baseStonesPer5Min = 2

        val qiEarned = (minutes * baseQiPerMin * multiplier).toInt()
        val stonesEarned = ((minutes / 5) * baseStonesPer5Min * multiplier).toInt().coerceAtLeast(3)

        val session = FocusSession(
            durationMinutes = minutes,
            completedAt = now,
            qiEarned = qiEarned,
            spiritStonesEarned = stonesEarned,
            mode = mode
        )
        dao.insertFocusSession(session)

        dao.insertOrUpdateProfile(
            profile.copy(
                currentQi = profile.currentQi + qiEarned,
                spiritStones = profile.spiritStones + stonesEarned,
                totalFocusMinutes = profile.totalFocusMinutes + minutes
            )
        )

        return Pair(qiEarned, stonesEarned)
    }

    suspend fun setZenMode(enabled: Boolean) {
        val profile = getProfile()
        dao.insertOrUpdateProfile(profile.copy(zenModeEnabled = enabled))
    }

    suspend fun pinApp(packageName: String, orderIndex: Int) {
        dao.pinApp(PinnedApp(packageName = packageName, orderIndex = orderIndex))
    }

    suspend fun unpinApp(packageName: String) {
        dao.unpinApp(packageName)
    }

    // --- Distraction Blocker & App Restrictions ---
    suspend fun unlockDistractionApp(packageName: String, minutes: Int, stoneCost: Int): Boolean {
        val profile = getProfile()
        if (profile.spiritStones < stoneCost) {
            return false
        }

        // Deduct spirit stones
        dao.insertOrUpdateProfile(profile.copy(spiritStones = profile.spiritStones - stoneCost))

        val now = System.currentTimeMillis()
        val expiresAt = now + (minutes * 60 * 1000L)
        val existing = dao.getRestriction(packageName)
        val updated = existing?.copy(unlockExpiresAt = expiresAt)
            ?: AppRestriction(packageName = packageName, isDistraction = true, unlockExpiresAt = expiresAt)
        dao.insertOrUpdateRestriction(updated)
        return true
    }

    suspend fun rewardStudyAppLaunch(packageName: String): Pair<Int, Int> {
        val profile = getProfile()
        val now = System.currentTimeMillis()
        val existing = dao.getRestriction(packageName)

        // Award +10 Spirit Stones, +25 Qi per study session launch
        val qiBonus = 25
        val stonesBonus = 10

        dao.insertOrUpdateProfile(
            profile.copy(
                currentQi = profile.currentQi + qiBonus,
                spiritStones = profile.spiritStones + stonesBonus
            )
        )

        val updated = existing?.copy(lastStudyRewardAt = now, isStudy = true)
            ?: AppRestriction(packageName = packageName, isStudy = true, lastStudyRewardAt = now)
        dao.insertOrUpdateRestriction(updated)

        return Pair(qiBonus, stonesBonus)
    }

    suspend fun rewardWillpowerResistance(): Int {
        val profile = getProfile()
        val qiBonus = 10
        dao.insertOrUpdateProfile(
            profile.copy(currentQi = profile.currentQi + qiBonus)
        )
        return qiBonus
    }

    suspend fun setAppDistractionStatus(packageName: String, isDistraction: Boolean) {
        val existing = dao.getRestriction(packageName)
        val updated = existing?.copy(isDistraction = isDistraction)
            ?: AppRestriction(packageName = packageName, isDistraction = isDistraction)
        dao.insertOrUpdateRestriction(updated)
    }

    suspend fun setAppStudyStatus(packageName: String, isStudy: Boolean) {
        val existing = dao.getRestriction(packageName)
        val updated = existing?.copy(isStudy = isStudy)
            ?: AppRestriction(packageName = packageName, isStudy = isStudy)
        dao.insertOrUpdateRestriction(updated)
    }

    suspend fun setAppHiddenStatus(packageName: String, isHidden: Boolean) {
        val existing = dao.getRestriction(packageName)
        val updated = existing?.copy(isHidden = isHidden)
            ?: AppRestriction(packageName = packageName, isHidden = isHidden)
        dao.insertOrUpdateRestriction(updated)
    }

    suspend fun updateCalculatorPasscode(passcode: String) {
        dao.updateCalculatorPasscode(passcode)
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun isConsecutiveDay(lastDateStr: String, todayStr: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val last = sdf.parse(lastDateStr)?.time ?: return false
            val today = sdf.parse(todayStr)?.time ?: return false
            val diffDays = (today - last) / (1000 * 60 * 60 * 24)
            diffDays == 1L
        } catch (e: Exception) {
            false
        }
    }

    private fun getDaoTitleForRealm(level: Int): String {
        return when (level) {
            0 -> "Mortal Seeker"
            1 -> "Qi Cultivator"
            2 -> "Foundation Disciple"
            3 -> "Golden Core Elder"
            4 -> "Nascent Soul Sovereign"
            5 -> "Soul Formation Venerable"
            6 -> "Void Grandmaster"
            7 -> "Dao Monarch"
            8 -> "Tribulation Saint"
            else -> "True Celestial Immortal"
        }
    }
}

sealed class BreakthroughResult {
    data class Success(val newRealm: CultivationRealm, val newSubStage: Int, val isMajorAscension: Boolean) : BreakthroughResult()
    data class Failed(val lostQi: Long) : BreakthroughResult()
    data class NotEnoughQi(val currentQi: Long, val maxQi: Long) : BreakthroughResult()
}
