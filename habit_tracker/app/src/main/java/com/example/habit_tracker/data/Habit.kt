package com.example.habit_tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "habits")
@TypeConverters(Converters::class)
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String?,
    val priority: Priority?,
    val createdEpochDay: Int,
    val reminderEnabled: Boolean = false,
    val reminderTime: String? = null
)
