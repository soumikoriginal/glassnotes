package cocm.glass.note.pr.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cocm.glass.note.pr.data.local.PreferencesManager
import cocm.glass.note.pr.domain.model.Note
import cocm.glass.note.pr.domain.model.NoteColor
import cocm.glass.note.pr.domain.usecase.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchNotes: SearchNotesUseCase,
    private val deleteNote: DeleteNoteUseCase,
    private val togglePinNote: TogglePinNoteUseCase,
    private val archiveNote: ArchiveNoteUseCase,
    private val updateNoteColor: UpdateNoteColorUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Note>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Load recent searches from preferences
        loadRecentSearches()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query

        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        searchNotes(query).collect { notes ->
            _searchResults.value = notes.filter { !it.isDeleted }
        }
    }

    fun addToRecentSearches(query: String) {
        if (query.isBlank()) return

        val current = _recentSearches.value.toMutableList()
        current.remove(query)
        current.add(0, query)

        // Keep only last 10 searches
        _recentSearches.value = current.take(10)

        // Save to preferences
        saveRecentSearches()
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
        saveRecentSearches()
    }

    fun onPinToggle(note: Note) {
        viewModelScope.launch {
            togglePinNote(note, !note.isPinned)
        }
    }

    fun onArchive(note: Note) {
        viewModelScope.launch {
            archiveNote(note, !note.isArchived)
        }
    }

    fun onDelete(note: Note) {
        viewModelScope.launch {
            deleteNote(note)
        }
    }

    fun onColorChange(note: Note, color: NoteColor) {
        viewModelScope.launch {
            updateNoteColor(note, color)
        }
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            preferencesManager.recentSearches.collect { searches ->
                _recentSearches.value = searches
            }
        }
    }

    private fun saveRecentSearches() {
        viewModelScope.launch {
            preferencesManager.saveRecentSearches(_recentSearches.value)
        }
    }
}
