package com.example.habit_tracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: Habit): Long

    @Delete
    suspend fun delete(habit: Habit)

    @Update
    suspend fun update(habit: Habit): Int

    @Query("SELECT * FROM habits ORDER BY priority DESC, id DESC")
    fun getAll(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE category = :category ORDER BY priority DESC, id DESC")
    fun getByCategory(category: String): Flow<List<Habit>>

    @Query("SELECT DISTINCT category FROM habits WHERE category IS NOT NULL")
    suspend fun getCategories(): List<String>

    @Query("SELECT * FROM habits WHERE reminderEnabled = 1")
    suspend fun getAllWithReminders(): List<Habit>

    @Query("SELECT * FROM habits")
    suspend fun getAllOnce(): List<Habit>

}
