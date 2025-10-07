package com.example.habit_tracker.data

import android.content.Context
import com.example.habit_tracker.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HabitRepository private constructor(private val context: Context) {
    private val db = AppDatabase.get(context)
    private val habits = db.habitDao()
    private val checkIns = db.checkInDao()

    private val defaultCategories = listOf("Study", "Home", "Work", "Health", "Art")

    fun observeHabits(): Flow<List<Habit>> = habits.getAll()

    fun observeHabitsByCategory(category: String): Flow<List<Habit>> = habits.getByCategory(category)

    fun observeCategories(): Flow<List<String>> {
        return habits.getAll().map { list ->
            (defaultCategories + list.mapNotNull { it.category })
                .distinct()
                .sorted()
        }
    }

    suspend fun getCategories(): List<String> {
        val dbCats = habits.getCategories()
        return (defaultCategories + dbCats).distinct().sorted()
    }

    suspend fun addHabit(
        name: String,
        category: String?,
        priority: Priority?,
        reminderEnabled: Boolean = false,
        reminderTime: String? = null
    ): Long {
        val today = LocalDate.now().toEpochDay().toInt()
        val habit = Habit(
            name = name,
            category = category,
            priority = priority ?: Priority.NORMAL,
            createdEpochDay = today,
            reminderEnabled = reminderEnabled,
            reminderTime = reminderTime
        )
        val habitId = habits.insert(habit)

        if (reminderEnabled && reminderTime != null) {
            ReminderScheduler.scheduleReminder(context, habitId, name, reminderTime)
        }

        return habitId
    }

    suspend fun updateHabit(habit: Habit): Boolean {
        val rows = habits.update(habit)

        if (habit.reminderEnabled && habit.reminderTime != null) {
            ReminderScheduler.scheduleReminder(context, habit.id, habit.name, habit.reminderTime)
        } else {
            ReminderScheduler.cancelReminder(context, habit.id)
        }

        return rows > 0
    }

    suspend fun deleteHabit(habit: Habit) {
        habits.delete(habit)
        ReminderScheduler.cancelReminder(context, habit.id)
    }

    suspend fun setTodayChecked(habitId: Long, checked: Boolean) {
        val today = LocalDate.now().toEpochDay().toInt()
        if (checked) {
            checkIns.insert(CheckIn(habitId, today))
        } else {
            checkIns.delete(habitId, today)
        }
    }

    suspend fun isTodayChecked(habitId: Long): Boolean {
        val today = LocalDate.now().toEpochDay().toInt()
        return checkIns.getForRange(habitId, today, today).isNotEmpty()
    }

    suspend fun last7Days(habitId: Long): Map<Int, Boolean> {
        val end = LocalDate.now().toEpochDay().toInt()
        val start = LocalDate.now().minusDays(6).toEpochDay().toInt()
        val rows = checkIns.getForRange(habitId, start, end)
        val set = rows.map { it.dateEpochDay }.toSet()
        return (start..end).associateWith { it in set }
    }

    suspend fun currentStreak(habitId: Long): Int {
        val today = LocalDate.now().toEpochDay().toInt()
        val all = checkIns.getAllForHabit(habitId).map { it.dateEpochDay }.toHashSet()

        var day = today
        var streak = 0

        if (!all.contains(day)) {
            day -= 1
        }

        while (all.contains(day)) {
            streak++
            day--
        }

        return streak
    }

    companion object {
        @Volatile private var INSTANCE: HabitRepository? = null
        fun get(context: Context): HabitRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HabitRepository(context.applicationContext).also { INSTANCE = it }
            }
    }
}
