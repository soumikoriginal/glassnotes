package cocm.glass.note.pr.presentation.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cocm.glass.note.pr.domain.model.ChecklistItem
import cocm.glass.note.pr.domain.model.Note
import cocm.glass.note.pr.domain.model.NoteColor
import cocm.glass.note.pr.domain.model.NoteType
import cocm.glass.note.pr.domain.repository.NoteRepository
import cocm.glass.note.pr.util.ReminderManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class EditorUiState(
    val note: Note? = null,
    val title: String = "",
    val content: String = "",
    val checklistItems: List<ChecklistItem> = emptyList(),
    val imagePaths: List<String> = emptyList(),
    val drawingPath: String? = null,
    val availableLabels: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false
)

class EditorViewModel(
    private val noteRepository: NoteRepository,
    private val reminderManager: ReminderManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private var noteId: Long? = null

    init {
        loadAvailableLabels()
    }

    fun loadNote(id: Long) {
        noteId = id
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val note = noteRepository.getNoteById(id)
            note?.let {
                _uiState.update {
                    it.copy(
                        note = note,
                        title = note.title,
                        content = note.content,
                        checklistItems = note.checklistData,
                        imagePaths = note.imagePaths,
                        drawingPath = note.drawingPath,
                        isLoading = false
                    )
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
        autoSave()
    }

    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
        autoSave()
    }

    fun addChecklistItem() {
        val newItem = ChecklistItem(
            id = UUID.randomUUID().toString(),
            text = "",
            isChecked = false,
            order = _uiState.value.checklistItems.size
        )
        _uiState.update {
            it.copy(checklistItems = it.checklistItems + newItem)
        }
        autoSave()
    }

    fun updateChecklistItem(item: ChecklistItem) {
        _uiState.update {
            val updatedItems = it.checklistItems.map { existing ->
                if (existing.id == item.id) item else existing
            }
            it.copy(checklistItems = updatedItems)
        }
        autoSave()
    }

    fun deleteChecklistItem(itemId: String) {
        _uiState.update {
            val updatedItems = it.checklistItems.filter { item -> item.id != itemId }
            it.copy(checklistItems = updatedItems)
        }
        autoSave()
    }

    fun reorderChecklistItems(items: List<ChecklistItem>) {
        _uiState.update {
            val reorderedItems = items.mapIndexed { index, item ->
                item.copy(order = index)
            }
            it.copy(checklistItems = reorderedItems)
        }
        autoSave()
    }

    fun addImage(imagePath: String) {
        _uiState.update {
            it.copy(imagePaths = it.imagePaths + imagePath)
        }
        autoSave()
    }

    fun removeImage(imagePath: String) {
        _uiState.update {
            it.copy(imagePaths = it.imagePaths.filter { it != imagePath })
        }
        autoSave()
    }

    fun setDrawing(drawingPath: String) {
        _uiState.update {
            it.copy(drawingPath = drawingPath)
        }
        autoSave()
    }

    fun removeDrawing() {
        _uiState.update {
            it.copy(drawingPath = null)
        }
        autoSave()
    }

    fun updateLabels(labels: List<String>) {
        val currentNote = _uiState.value.note ?: return
        viewModelScope.launch {
            noteRepository.updateNote(currentNote.copy(labels = labels))
            _uiState.update { it.copy(note = currentNote.copy(labels = labels)) }
        }
    }

    fun createLabel(label: String) {
        if (label.isNotBlank() && !_uiState.value.availableLabels.contains(label)) {
            _uiState.update {
                it.copy(availableLabels = it.availableLabels + label)
            }
        }
    }

    fun setReminder(timestamp: Long) {
        val currentNote = _uiState.value.note ?: return
        viewModelScope.launch {
            val updated = currentNote.copy(
                reminderTime = timestamp,
                hasReminder = true
            )
            noteRepository.updateNote(updated)
            _uiState.update { it.copy(note = updated) }
            reminderManager.scheduleReminder(
                noteId = updated.id,
                title = updated.title.ifBlank { "Reminder" },
                timeMillis = timestamp
            )
        }
    }

    fun removeReminder() {
        val currentNote = _uiState.value.note ?: return
        viewModelScope.launch {
            val updated = currentNote.copy(
                reminderTime = null,
                hasReminder = false
            )
            noteRepository.updateNote(updated)
            _uiState.update { it.copy(note = updated) }
            reminderManager.cancelReminder(updated.id)
        }
    }

    fun saveNote() {
        autoSave()
    }

    private fun autoSave() {
        val currentState = _uiState.value
        val currentNote = currentState.note
        val hasChecklistItems = currentState.checklistItems.isNotEmpty()
        val hasImages = currentState.imagePaths.isNotEmpty()
        val hasDrawing = currentState.drawingPath != null

        // Determine note type
        val noteType = when {
            hasChecklistItems && (hasImages || hasDrawing) -> NoteType.MIXED
            hasChecklistItems -> NoteType.CHECKLIST
            hasImages -> NoteType.IMAGE
            hasDrawing -> NoteType.DRAWING
            else -> NoteType.TEXT
        }

        if (currentNote != null) {
            // Update existing note
            viewModelScope.launch {
                noteRepository.updateNote(
                    currentNote.copy(
                        title = currentState.title,
                        content = currentState.content,
                        checklistData = currentState.checklistItems,
                        imagePaths = currentState.imagePaths,
                        drawingPath = currentState.drawingPath,
                        noteType = noteType,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        } else if (currentState.title.isNotBlank() || currentState.content.isNotBlank() ||
                   hasChecklistItems || hasImages || hasDrawing) {
            // Create new note
            val newNote = Note(
                id = 0, // Will be auto-generated by Room
                title = currentState.title,
                content = currentState.content,
                noteType = noteType,
                color = NoteColor.DEFAULT,
                checklistData = currentState.checklistItems,
                imagePaths = currentState.imagePaths,
                drawingPath = currentState.drawingPath,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            viewModelScope.launch {
                val insertedId = noteRepository.insertNote(newNote)
                val insertedNote = newNote.copy(id = insertedId)
                _uiState.update { it.copy(note = insertedNote) }
                noteId = insertedId
            }
        }
    }

    fun togglePin() {
        val currentNote = _uiState.value.note ?: return
        viewModelScope.launch {
            val updated = currentNote.copy(isPinned = !currentNote.isPinned)
            noteRepository.updateNote(updated)
            _uiState.update { it.copy(note = updated) }
        }
    }

    fun changeColor(color: NoteColor) {
        val currentNote = _uiState.value.note ?: return
        viewModelScope.launch {
            val updated = currentNote.copy(color = color)
            noteRepository.updateNote(updated)
            _uiState.update { it.copy(note = updated) }
        }
    }

    fun archive() {
        val currentNote = _uiState.value.note ?: return
        viewModelScope.launch {
            noteRepository.updateNote(currentNote.copy(isArchived = true))
        }
    }

    fun delete() {
        val currentNote = _uiState.value.note ?: return
        viewModelScope.launch {
            noteRepository.updateNote(currentNote.copy(isDeleted = true))
        }
    }

    private fun loadAvailableLabels() {
        viewModelScope.launch {
            noteRepository.getAllNotes().collect { notes ->
                val allLabels = notes.flatMap { it.labels }.distinct()
                _uiState.update { it.copy(availableLabels = allLabels) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Final save on ViewModel destruction
        autoSave()
    }
}
