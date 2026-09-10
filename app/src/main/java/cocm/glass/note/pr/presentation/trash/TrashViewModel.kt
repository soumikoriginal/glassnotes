package cocm.glass.note.pr.presentation.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cocm.glass.note.pr.domain.model.Note
import cocm.glass.note.pr.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TrashViewModel(
    private val getDeletedNotes: GetDeletedNotesUseCase,
    private val restoreNote: RestoreNoteUseCase,
    private val permanentlyDeleteNote: PermanentlyDeleteNoteUseCase
) : ViewModel() {

    val deletedNotes: StateFlow<List<Note>> = getDeletedNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _showEmptyTrashDialog = MutableStateFlow(false)
    val showEmptyTrashDialog = _showEmptyTrashDialog.asStateFlow()

    fun onRestore(note: Note) {
        viewModelScope.launch {
            restoreNote(note)
        }
    }

    fun onPermanentlyDelete(note: Note) {
        viewModelScope.launch {
            permanentlyDeleteNote(note)
        }
    }

    fun showEmptyTrashDialog() {
        _showEmptyTrashDialog.value = true
    }

    fun hideEmptyTrashDialog() {
        _showEmptyTrashDialog.value = false
    }

    fun emptyTrash() {
        viewModelScope.launch {
            deletedNotes.value.forEach { note ->
                permanentlyDeleteNote(note)
            }
            _showEmptyTrashDialog.value = false
        }
    }
}
