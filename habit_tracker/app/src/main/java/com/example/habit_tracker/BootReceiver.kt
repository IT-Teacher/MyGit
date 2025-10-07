package com.example.habit_tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.habit_tracker.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.get(context)
                val habitDao = db.habitDao()
                val habits = habitDao.getAllOnce()
                for (habit in habits) {
                    if (habit.reminderEnabled && habit.reminderTime != null) {
                        ReminderScheduler.scheduleReminder(
                            context,
                            habit.id,
                            habit.name,
                            habit.reminderTime
                        )
                    }
                }
            }
        }
    }
}
