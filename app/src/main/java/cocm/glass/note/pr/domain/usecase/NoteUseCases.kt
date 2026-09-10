package cocm.glass.note.pr.domain.usecase

import cocm.glass.note.pr.domain.model.Note
import cocm.glass.note.pr.domain.model.NoteColor
import cocm.glass.note.pr.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repository.getAllNotes()
}

class GetNoteByIdUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: Long): Note? = repository.getNoteById(id)
}

class InsertNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note): Long = repository.insertNote(note)
}

class UpdateNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) = repository.updateNote(note)
}

class DeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) = repository.deleteNote(note)
}

class GetPinnedNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repository.getPinnedNotes()
}

class GetArchivedNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repository.getArchivedNotes()
}

class GetDeletedNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repository.getDeletedNotes()
}

class SearchNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(query: String): Flow<List<Note>> = repository.searchNotes(query)
}

class GetNotesByLabelUseCase(private val repository: NoteRepository) {
    operator fun invoke(label: String): Flow<List<Note>> = repository.getNotesByLabel(label)
}

class EmptyTrashUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke() = repository.emptyTrash()
}

class TogglePinNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note, isPinned: Boolean) {
        repository.updateNote(note.copy(isPinned = isPinned))
    }
}

class ArchiveNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note, isArchived: Boolean) {
        repository.updateNote(note.copy(isArchived = isArchived))
    }
}

class UpdateNoteColorUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note, color: NoteColor) {
        repository.updateNote(note.copy(color = color))
    }
}

class RestoreNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        repository.updateNote(note.copy(isDeleted = false))
    }
}

class PermanentlyDeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}

data class NoteUseCases(
    val getAllNotes: GetAllNotesUseCase,
    val getNoteById: GetNoteByIdUseCase,
    val insertNote: InsertNoteUseCase,
    val updateNote: UpdateNoteUseCase,
    val deleteNote: DeleteNoteUseCase,
    val getPinnedNotes: GetPinnedNotesUseCase,
    val getArchivedNotes: GetArchivedNotesUseCase,
    val getDeletedNotes: GetDeletedNotesUseCase,
    val searchNotes: SearchNotesUseCase,
    val getNotesByLabel: GetNotesByLabelUseCase,
    val emptyTrash: EmptyTrashUseCase,
    val togglePin: TogglePinNoteUseCase,
    val archiveNote: ArchiveNoteUseCase,
    val updateNoteColor: UpdateNoteColorUseCase,
    val restoreNote: RestoreNoteUseCase,
    val permanentlyDeleteNote: PermanentlyDeleteNoteUseCase
)
