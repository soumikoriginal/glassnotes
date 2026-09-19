package cocm.glass.note.pr.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cocm.glass.note.pr.GlassNotesApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val app = context.applicationContext as GlassNotesApplication
        val noteRepository = app.container.noteRepository
        val reminderManager = app.container.reminderManager

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentTime = System.currentTimeMillis()
                val notes = noteRepository.getAllNotes().first()
                for (note in notes) {
                    val reminderTime = note.reminderTime
                    if (note.hasReminder && reminderTime != null && reminderTime > currentTime) {
                        reminderManager.scheduleReminder(
                            noteId = note.id,
                            title = note.title.ifBlank { "Reminder" },
                            timeMillis = reminderTime
                        )
                    }
                }
            } catch (e: Exception) {
                // Log and continue
            } finally {
                pendingResult.finish()
            }
        }
    }
}
