package cocm.glass.note.pr.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import cocm.glass.note.pr.MainActivity
import cocm.glass.note.pr.R

class NotificationHelper(private val context: Context) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        private const val CHANNEL_ID = "glass_notes_reminders"
        private const val CHANNEL_NAME = "Reminders"
        private const val CHANNEL_DESCRIPTION = "Note reminders"
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showReminderNotification(noteId: String, title: String, content: String = "") {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NOTE_ID", noteId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // We'll create this
            .setContentTitle(title)
            .setContentText(content.take(100))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(noteId.hashCode(), notification)
    }
    
    fun cancelNotification(noteId: String) {
        notificationManager.cancel(noteId.hashCode())
    }
}
