package cocm.glass.note.pr.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import cocm.glass.note.pr.data.local.converters.Converters
import cocm.glass.note.pr.domain.model.ChecklistItem
import cocm.glass.note.pr.domain.model.NoteColor
import cocm.glass.note.pr.domain.model.NoteType

@Entity(tableName = "notes")
@TypeConverters(Converters::class)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val noteType: NoteType = NoteType.TEXT,
    val color: NoteColor = NoteColor.DEFAULT,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val reminderTime: Long? = null,
    val hasReminder: Boolean = false,
    val checklistData: List<ChecklistItem> = emptyList(),
    val labels: List<String> = emptyList(),
    val imagePaths: List<String> = emptyList(),
    val drawingPath: String? = null
)
