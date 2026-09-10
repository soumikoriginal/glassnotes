package cocm.glass.note.pr.presentation.labels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cocm.glass.note.pr.domain.model.Note
import cocm.glass.note.pr.domain.usecase.NoteUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LabelsViewModel(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    val labels: StateFlow<List<String>> = noteUseCases.getAllNotes()
        .map { notes ->
            notes.filter { !it.isDeleted }
                .flatMap { it.labels }
                .distinct()
                .sorted()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getLabelNoteCount(label: String): Flow<Int> = noteUseCases.getAllNotes()
        .map { notes ->
            notes.count { !it.isDeleted && label in it.labels }
        }
}
