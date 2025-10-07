package com.example.habit_tracker.data

import androidx.room.Entity

@Entity(tableName = "checkins", primaryKeys = ["habitId", "dateEpochDay"])
data class CheckIn(
    val habitId: Long,
    val dateEpochDay: Int
)
