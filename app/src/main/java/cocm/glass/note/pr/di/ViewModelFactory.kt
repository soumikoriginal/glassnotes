package cocm.glass.note.pr.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cocm.glass.note.pr.presentation.archive.ArchiveViewModel
import cocm.glass.note.pr.presentation.editor.EditorViewModel
import cocm.glass.note.pr.presentation.home.HomeViewModel
import cocm.glass.note.pr.presentation.labels.LabelsViewModel
import cocm.glass.note.pr.presentation.search.SearchViewModel
import cocm.glass.note.pr.presentation.settings.SettingsViewModel
import cocm.glass.note.pr.presentation.trash.TrashViewModel

class ViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    noteRepository = container.noteRepository,
                    labelRepository = container.labelRepository
                ) as T
            }
            modelClass.isAssignableFrom(EditorViewModel::class.java) -> {
                EditorViewModel(
                    noteRepository = container.noteRepository,
                    reminderManager = container.reminderManager
                ) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(
                    searchNotes = container.noteUseCases.searchNotes,
                    deleteNote = container.noteUseCases.deleteNote,
                    togglePinNote = container.noteUseCases.togglePin,
                    archiveNote = container.noteUseCases.archiveNote,
                    updateNoteColor = container.noteUseCases.updateNoteColor,
                    preferencesManager = container.preferencesManager
                ) as T
            }
            modelClass.isAssignableFrom(LabelsViewModel::class.java) -> {
                LabelsViewModel(
                    noteUseCases = container.noteUseCases
                ) as T
            }
            modelClass.isAssignableFrom(ArchiveViewModel::class.java) -> {
                ArchiveViewModel(
                    noteUseCases = container.noteUseCases
                ) as T
            }
            modelClass.isAssignableFrom(TrashViewModel::class.java) -> {
                TrashViewModel(
                    getDeletedNotes = container.noteUseCases.getDeletedNotes,
                    restoreNote = container.noteUseCases.restoreNote,
                    permanentlyDeleteNote = container.noteUseCases.permanentlyDeleteNote
                ) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    preferencesManager = container.preferencesManager
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
