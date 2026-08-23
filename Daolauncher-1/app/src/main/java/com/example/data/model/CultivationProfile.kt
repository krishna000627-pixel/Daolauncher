package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cultivation_profile")
data class CultivationProfile(
    @PrimaryKey val id: Int = 1,
    val realmLevel: Int = 0,
    val realmSubStage: Int = 1, // 1: Early, 2: Mid, 3: Late, 4: Peak
    val currentQi: Long = 20L,
    val maxQi: Long = 100L,
    val spiritStones: Long = 50L,
    val totalTasksCompleted: Int = 0,
    val totalFocusMinutes: Long = 0L,
    val consecutiveDaoDays: Int = 1,
    val lastActiveDate: String = "",
    val tribulationBreakthroughCount: Int = 0,
    val daoTitle: String = "Aspiring Daoist",
    val activeBuffPill: String? = null,
    val buffExpiresAt: Long = 0L,
    val zenModeEnabled: Boolean = false,
    val calculatorPasscode: String = "8888"
) {
    val currentRealm: CultivationRealm
        get() = CultivationRealm.fromLevel(realmLevel)

    val stageName: String
        get() = when (realmSubStage) {
            1 -> "Early Stage"
            2 -> "Mid Stage"
            3 -> "Late Stage"
            else -> "Peak Stage"
        }

    val fullRankDisplay: String
        get() = "${currentRealm.title} ($stageName)"

    val isReadyForBreakthrough: Boolean
        get() = currentQi >= maxQi
}
