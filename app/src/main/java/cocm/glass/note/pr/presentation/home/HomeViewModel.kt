package cocm.glass.note.pr.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cocm.glass.note.pr.domain.model.Note
import cocm.glass.note.pr.domain.model.Label
import cocm.glass.note.pr.domain.repository.NoteRepository
import cocm.glass.note.pr.domain.repository.LabelRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val notes: List<Note> = emptyList(),
    val pinnedNotes: List<Note> = emptyList(),
    val labels: List<Label> = emptyList(),
    val searchQuery: String = "",
    val selectedLabel: Label? = null,
    val isLoading: Boolean = false,
    val viewMode: ViewMode = ViewMode.GRID
)

enum class ViewMode {
    GRID, LIST
}

class HomeViewModel(
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
        loadLabels()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            noteRepository.getAllNotes()
                .collect { allNotes ->
                    val activeNotes = allNotes.filter { !it.isDeleted && !it.isArchived }
                    _uiState.update { state ->
                        state.copy(
                            notes = activeNotes.filter { !it.isPinned },
                            pinnedNotes = activeNotes.filter { it.isPinned }
                        )
                    }
                }
        }
    }

    private fun loadLabels() {
        viewModelScope.launch {
            labelRepository.getAllLabels()
                .collect { labels ->
                    _uiState.update { it.copy(labels = labels) }
                }
        }
    }

    fun searchNotes(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            loadNotes()
            return
        }

        viewModelScope.launch {
            noteRepository.searchNotes(query)
                .collect { searchResults ->
                    val activeNotes = searchResults.filter { !it.isDeleted && !it.isArchived }
                    _uiState.update { state ->
                        state.copy(
                            notes = activeNotes.filter { !it.isPinned },
                            pinnedNotes = activeNotes.filter { it.isPinned }
                        )
                    }
                }
        }
    }

    fun toggleViewMode() {
        _uiState.update { state ->
            state.copy(
                viewMode = if (state.viewMode == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID
            )
        }
    }

    fun togglePinNote(note: Note) {
        viewModelScope.launch {
            noteRepository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }

    fun archiveNote(note: Note) {
        viewModelScope.launch {
            noteRepository.updateNote(note.copy(isArchived = true))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.updateNote(note.copy(isDeleted = true))
        }
    }

    fun filterByLabel(label: Label?) {
        _uiState.update { it.copy(selectedLabel = label) }
        if (label == null) {
            loadNotes()
            return
        }

        viewModelScope.launch {
            noteRepository.getNotesByLabel(label.id.toString())
                .collect { filteredNotes ->
                    val activeNotes = filteredNotes.filter { !it.isDeleted && !it.isArchived }
                    _uiState.update { state ->
                        state.copy(
                            notes = activeNotes.filter { !it.isPinned },
                            pinnedNotes = activeNotes.filter { it.isPinned }
                        )
                    }
                }
        }
    }
}
