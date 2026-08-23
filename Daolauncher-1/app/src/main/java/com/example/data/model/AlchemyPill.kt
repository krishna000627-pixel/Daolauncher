package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alchemy_inventory")
data class AlchemyPill(
    @PrimaryKey val id: String,
    val name: String,
    val chineseName: String,
    val description: String,
    val stoneCost: Int,
    val qiCost: Int,
    val count: Int,
    val effectDescription: String,
    val durationMinutes: Int,
    val tier: Int // 1 to 5
)

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val qiEarned: Int,
    val spiritStonesEarned: Int,
    val mode: String = "MIND_BREATHING",
    val notes: String = ""
)

@Entity(tableName = "pinned_apps")
data class PinnedApp(
    @PrimaryKey val packageName: String,
    val orderIndex: Int,
    val customLabel: String? = null
)
