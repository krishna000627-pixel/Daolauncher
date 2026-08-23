package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_restrictions")
data class AppRestriction(
    @PrimaryKey val packageName: String,
    val isDistraction: Boolean = false,
    val isStudy: Boolean = false,
    val isHidden: Boolean = false,
    val unlockExpiresAt: Long = 0L,
    val lastStudyRewardAt: Long = 0L
)
