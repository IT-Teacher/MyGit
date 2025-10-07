package com.example.habit_tracker.data

import androidx.room.TypeConverter

enum class Priority {
    LOW, NORMAL, HIGH
}

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority?): String? = priority?.name

    @TypeConverter
    fun toPriority(value: String?): Priority? = value?.let { Priority.valueOf(it) }
}
