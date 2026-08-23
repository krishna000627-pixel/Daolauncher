package com.example.data.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val icon: Drawable? = null,
    val isPinned: Boolean = false,
    val isSystemApp: Boolean = false,
    val category: String = "App",
    val isDistraction: Boolean = false,
    val isStudy: Boolean = false,
    val isHidden: Boolean = false,
    val unlockExpiresAt: Long = 0L
) {
    val isCurrentlyUnlocked: Boolean
        get() = unlockExpiresAt > System.currentTimeMillis()

    val remainingUnlockMinutes: Int
        get() {
            val remainingMs = unlockExpiresAt - System.currentTimeMillis()
            return if (remainingMs > 0) ((remainingMs + 59999) / 60000).toInt() else 0
        }
}
