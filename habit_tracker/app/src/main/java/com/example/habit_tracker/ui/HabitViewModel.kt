package com.example.habit_tracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_tracker.ReminderScheduler
import com.example.habit_tracker.data.Habit
import com.example.habit_tracker.data.HabitRepository
import com.example.habit_tracker.data.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = HabitRepository.get(app)
    private val context = app.applicationContext

    val habits: StateFlow<List<Habit>> = repo.observeHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val refreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = refreshing

    fun addHabit(
        name: String,
        category: String?,
        priority: Priority?,
        reminderEnabled: Boolean,
        reminderTime: String?
    ) = viewModelScope.launch {
        val habitId = repo.addHabit(name, category, priority, reminderEnabled, reminderTime)

        if (reminderEnabled && reminderTime != null) {
            ReminderScheduler.scheduleReminder(context, habitId, name, reminderTime)
        }
    }


    fun deleteHabit(habit: Habit) = viewModelScope.launch {
        repo.deleteHabit(habit)
        ReminderScheduler.cancelReminder(context, habit.id)
    }

    fun updateHabit(habit: Habit) = viewModelScope.launch {
        repo.updateHabit(habit)

        if (habit.reminderEnabled && habit.reminderTime != null) {
            ReminderScheduler.scheduleReminder(context, habit.id, habit.name, habit.reminderTime)
        } else {
            ReminderScheduler.cancelReminder(context, habit.id)
        }
    }

    fun setTodayChecked(habitId: Long, checked: Boolean) = viewModelScope.launch {
        repo.setTodayChecked(habitId, checked)
    }

    suspend fun setTodayCheckedSync(habitId: Long, checked: Boolean) {
        repo.setTodayChecked(habitId, checked)
    }
}
