package com.example.habit_tracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CheckInDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(checkIn: CheckIn)

    @Query("DELETE FROM checkins WHERE habitId = :habitId AND dateEpochDay = :date")
    suspend fun delete(habitId: Long, date: Int)

    @Query("SELECT * FROM checkins WHERE habitId = :habitId AND dateEpochDay BETWEEN :start AND :end")
    suspend fun getForRange(habitId: Long, start: Int, end: Int): List<CheckIn>

    @Query("SELECT * FROM checkins WHERE habitId = :habitId")
    suspend fun getAllForHabit(habitId: Long): List<CheckIn>
}
