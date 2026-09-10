package cocm.glass.note.pr.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import cocm.glass.note.pr.receiver.ReminderReceiver

class ReminderManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(noteId: Long, title: String, timeMillis: Long) {
        try {
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("NOTE_ID", noteId)
                putExtra("NOTE_TITLE", title)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                noteId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Schedule the alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeMillis,
                    pendingIntent
                )
            }

            Log.d("ReminderManager", "Reminder scheduled for note: $noteId at $timeMillis")
        } catch (e: Exception) {
            Log.e("ReminderManager", "Error scheduling reminder", e)
        }
    }

    fun cancelReminder(noteId: Long) {
        try {
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                noteId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()

            Log.d("ReminderManager", "Reminder cancelled for note: $noteId")
        } catch (e: Exception) {
            Log.e("ReminderManager", "Error cancelling reminder", e)
        }
    }

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
}
