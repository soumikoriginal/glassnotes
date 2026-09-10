package cocm.glass.note.pr.data.repository

import cocm.glass.note.pr.data.local.dao.NoteDao
import cocm.glass.note.pr.data.local.entities.NoteEntity
import cocm.glass.note.pr.data.local.entities.toDomainModel
import cocm.glass.note.pr.data.local.entities.toEntity
import cocm.glass.note.pr.domain.model.Note
import cocm.glass.note.pr.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(private val noteDao: NoteDao) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllNotes().map { it.map { entity -> entity.toDomainModel() } }

    override fun getPinnedNotes(): Flow<List<Note>> =
        noteDao.getPinnedNotes().map { it.map { entity -> entity.toDomainModel() } }

    override fun getArchivedNotes(): Flow<List<Note>> =
        noteDao.getArchivedNotes().map { it.map { entity -> entity.toDomainModel() } }

    override fun getDeletedNotes(): Flow<List<Note>> =
        noteDao.getDeletedNotes().map { it.map { entity -> entity.toDomainModel() } }

    override suspend fun getNoteById(id: Long): Note? =
        noteDao.getNoteById(id)?.toDomainModel()

    override fun getNoteByIdFlow(id: Long): Flow<Note?> =
        noteDao.getNoteByIdFlow(id).map { it?.toDomainModel() }

    override fun searchNotes(query: String): Flow<List<Note>> =
        noteDao.searchNotes(query).map { it.map { entity -> entity.toDomainModel() } }

    override fun getNotesByLabel(label: String): Flow<List<Note>> =
        noteDao.getNotesByLabel(label).map { it.map { entity -> entity.toDomainModel() } }

    override fun getAllLabels(): Flow<List<String>> =
        noteDao.getAllLabels()

    override suspend fun insertNote(note: Note): Long =
        noteDao.insertNote(note.toEntity())

    override suspend fun updateNote(note: Note) =
        noteDao.updateNote(note.toEntity())

    override suspend fun deleteNote(note: Note) =
        noteDao.deleteNote(note.toEntity())

    override suspend fun deleteAllTrashedNotes() =
        noteDao.deleteAllTrashedNotes()

    override suspend fun emptyTrash() =
        noteDao.deleteAllTrashedNotes()

    override suspend fun deleteNoteById(id: Long) =
        noteDao.deleteNoteById(id)

    override suspend fun moveToTrash(id: Long) =
        noteDao.moveToTrash(id)

    override suspend fun restoreFromTrash(id: Long) =
        noteDao.restoreFromTrash(id)

    override suspend fun setArchived(id: Long, archived: Boolean) =
        noteDao.setArchived(id, archived)

    override suspend fun setPinned(id: Long, pinned: Boolean) =
        noteDao.setPinned(id, pinned)
}
