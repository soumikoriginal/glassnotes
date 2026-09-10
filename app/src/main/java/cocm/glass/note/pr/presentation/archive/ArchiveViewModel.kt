package cocm.glass.note.pr.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cocm.glass.note.pr.domain.model.Note
import cocm.glass.note.pr.domain.model.NoteColor
import cocm.glass.note.pr.domain.usecase.NoteUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArchiveViewModel(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    val archivedNotes: StateFlow<List<Note>> = noteUseCases.getAllNotes()
        .map { notes -> notes.filter { it.isArchived && !it.isDeleted } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onUnarchive(note: Note) {
        viewModelScope.launch {
            noteUseCases.archiveNote(note, false)
        }
    }

    fun onDelete(note: Note) {
        viewModelScope.launch {
            noteUseCases.deleteNote(note)
        }
    }

    fun onColorChange(note: Note, color: NoteColor) {
        viewModelScope.launch {
            noteUseCases.updateNoteColor(note, color)
        }
    }
}
