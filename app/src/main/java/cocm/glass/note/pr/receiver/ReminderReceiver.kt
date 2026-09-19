package cocm.glass.note.pr.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cocm.glass.note.pr.GlassNotesApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getLongExtra("NOTE_ID", -1L)
        if (noteId == -1L) return

        val noteTitle = intent.getStringExtra("NOTE_TITLE") ?: "Reminder"
        val pendingResult = goAsync()

        val app = context.applicationContext as GlassNotesApplication
        val notificationHelper = app.container.notificationHelper
        val noteRepository = app.container.noteRepository

        // Get the note content for the notification safely with goAsync
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val note = noteRepository.getNoteById(noteId)
                if (note != null) {
                    notificationHelper.showReminderNotification(
                        noteId = noteId.toString(),
                        title = note.title.ifEmpty { "Reminder" },
                        content = note.content
                    )
                }
            } catch (e: Exception) {
                // Fallback notification
                notificationHelper.showReminderNotification(
                    noteId = noteId.toString(),
                    title = noteTitle,
                    content = ""
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}
