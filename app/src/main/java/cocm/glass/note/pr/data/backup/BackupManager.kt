package cocm.glass.note.pr.data.backup

import android.content.Context
import android.net.Uri
import cocm.glass.note.pr.domain.model.Note
import cocm.glass.note.pr.domain.model.NoteType
import cocm.glass.note.pr.domain.model.NoteColor
import cocm.glass.note.pr.domain.model.ChecklistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class BackupManager(private val context: Context) {

    suspend fun exportBackup(notes: List<Note>, uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val backupJson = JSONObject().apply {
                put("version", 1)
                put("timestamp", System.currentTimeMillis())
                put("notes", JSONArray(notes.map { it.toJson() }))
            }

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(backupJson.toString(2).toByteArray())
            } ?: return@withContext Result.failure(Exception("Failed to open output stream"))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importBackup(uri: Uri): Result<List<Note>> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes().toString(Charsets.UTF_8)
            } ?: return@withContext Result.failure(Exception("Failed to open input stream"))

            val backupJson = JSONObject(jsonString)
            val notesArray = backupJson.getJSONArray("notes")
            val notes = mutableListOf<Note>()

            for (i in 0 until notesArray.length()) {
                notes.add(noteFromJson(notesArray.getJSONObject(i)))
            }

            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Note.toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("content", content)
        put("noteType", noteType.name)
        put("color", color.name)
        put("isPinned", isPinned)
        put("isArchived", isArchived)
        put("isDeleted", isDeleted)
        put("createdAt", createdAt)
        put("updatedAt", updatedAt)
        put("reminderTime", reminderTime ?: JSONObject.NULL)
        put("hasReminder", hasReminder)
        put("labels", JSONArray(labels))
        put("imagePaths", JSONArray(imagePaths))
        put("drawingPath", drawingPath ?: JSONObject.NULL)

        // Checklist data
        val checklistArray = JSONArray()
        checklistData.forEach { item ->
            checklistArray.put(JSONObject().apply {
                put("id", item.id)
                put("text", item.text)
                put("isChecked", item.isChecked)
                put("order", item.order)
            })
        }
        put("checklistData", checklistArray)
    }

    private fun noteFromJson(json: JSONObject): Note {
        val labelsArray = json.optJSONArray("labels")
        val labels = mutableListOf<String>()
        if (labelsArray != null) {
            for (i in 0 until labelsArray.length()) {
                labels.add(labelsArray.getString(i))
            }
        }

        val imagePathsArray = json.optJSONArray("imagePaths")
        val imagePaths = mutableListOf<String>()
        if (imagePathsArray != null) {
            for (i in 0 until imagePathsArray.length()) {
                imagePaths.add(imagePathsArray.getString(i))
            }
        }

        val checklistArray = json.optJSONArray("checklistData")
        val checklistData = mutableListOf<ChecklistItem>()
        if (checklistArray != null) {
            for (i in 0 until checklistArray.length()) {
                val itemJson = checklistArray.getJSONObject(i)
                checklistData.add(ChecklistItem(
                    id = itemJson.getString("id"),
                    text = itemJson.getString("text"),
                    isChecked = itemJson.getBoolean("isChecked"),
                    order = itemJson.getInt("order")
                ))
            }
        }

        return Note(
            id = json.optLong("id", 0),
            title = json.optString("title", ""),
            content = json.optString("content", ""),
            noteType = try { NoteType.valueOf(json.optString("noteType", "TEXT")) } catch (e: Exception) { NoteType.TEXT },
            color = try { NoteColor.valueOf(json.optString("color", "DEFAULT")) } catch (e: Exception) { NoteColor.DEFAULT },
            isPinned = json.optBoolean("isPinned", false),
            isArchived = json.optBoolean("isArchived", false),
            isDeleted = json.optBoolean("isDeleted", false),
            createdAt = json.optLong("createdAt", System.currentTimeMillis()),
            updatedAt = json.optLong("updatedAt", System.currentTimeMillis()),
            reminderTime = json.optLong("reminderTime", 0).takeIf { it > 0 },
            hasReminder = json.optBoolean("hasReminder", false),
            labels = labels,
            imagePaths = imagePaths,
            drawingPath = json.optString("drawingPath").takeIf { it.isNotEmpty() && it != "null" },
            checklistData = checklistData
        )
    }
}
