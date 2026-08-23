package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.DaoCategory
import com.example.data.model.TaskGrade

class CultivationConverters {
    @TypeConverter
    fun fromTaskGrade(grade: TaskGrade): String = grade.name

    @TypeConverter
    fun toTaskGrade(value: String): TaskGrade {
        return try {
            TaskGrade.valueOf(value)
        } catch (e: Exception) {
            TaskGrade.EARTH
        }
    }

    @TypeConverter
    fun fromDaoCategory(category: DaoCategory): String = category.name

    @TypeConverter
    fun toDaoCategory(value: String): DaoCategory {
        return try {
            DaoCategory.valueOf(value)
        } catch (e: Exception) {
            DaoCategory.SECT_DUTY
        }
    }
}
