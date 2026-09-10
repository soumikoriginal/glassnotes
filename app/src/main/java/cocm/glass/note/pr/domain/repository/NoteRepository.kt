package cocm.glass.note.pr.domain.repository

import cocm.glass.note.pr.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getPinnedNotes(): Flow<List<Note>>
    fun getArchivedNotes(): Flow<List<Note>>
    fun getDeletedNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    fun getNoteByIdFlow(id: Long): Flow<Note?>
    fun searchNotes(query: String): Flow<List<Note>>
    fun getNotesByLabel(label: String): Flow<List<Note>>
    fun getAllLabels(): Flow<List<String>>
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun deleteAllTrashedNotes()
    suspend fun emptyTrash()
    suspend fun deleteNoteById(id: Long)
    suspend fun moveToTrash(id: Long)
    suspend fun restoreFromTrash(id: Long)
    suspend fun setArchived(id: Long, archived: Boolean)
    suspend fun setPinned(id: Long, pinned: Boolean)
}
