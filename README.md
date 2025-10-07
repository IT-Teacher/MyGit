# Habit Tracker

A simple Habit Tracker Android app built with **Kotlin**, **Jetpack Compose** and **Room**.  
Tracks daily habits, shows streaks, and can schedule daily reminder notifications for each habit.

# Features
- Add / edit / delete habits (name, category, priority).
- Daily check-in for each habit (today checkbox).
- Streak calculation and 7-day visual strip.
- Optional daily reminder per habit (time picker).
- Reminders persist across device reboots.
- Built with Jetpack Compose (Material3) and Room.

# How reminders work (short)
1. When user adds/edits a habit and enables a reminder, the app stores `reminderEnabled` and `reminderTime` in the `habits` table.  
2. `ReminderScheduler` schedules a daily alarm using `AlarmManager` with a `PendingIntent` that targets `ReminderReceiver`.  
3. `ReminderReceiver` builds and shows the notification when the alarm triggers.  
4. On device boot, `BootReceiver` queries the DB and re-schedules reminders.

## Important permissions (AndroidManifest)
Your `AndroidManifest.xml` should include:
```xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
