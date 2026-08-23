package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cultivation_tasks")
data class CultivationTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val grade: TaskGrade = TaskGrade.EARTH,
    val category: DaoCategory = DaoCategory.SECT_DUTY,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: String? = null,
    val isDailyRecurring: Boolean = false,
    val priority: Int = 1 // 1: Normal, 2: High, 3: Heavenly Mandate
)
