package cocm.glass.note.pr.data.local.dao

import androidx.room.*
import cocm.glass.note.pr.data.local.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isPinned = 1 AND isDeleted = 0 AND isArchived = 0 ORDER BY updatedAt DESC")
    fun getPinnedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isArchived = 1 AND isDeleted = 0 ORDER BY updatedAt DESC")
    fun getArchivedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY updatedAt DESC")
    fun getDeletedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteByIdFlow(id: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')")
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND :label IN (SELECT value FROM json_each(labels))")
    fun getNotesByLabel(label: String): Flow<List<NoteEntity>>

    @Query("SELECT DISTINCT value FROM notes, json_each(labels) WHERE isDeleted = 0")
    fun getAllLabels(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE isDeleted = 1")
    suspend fun deleteAllTrashedNotes()

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("UPDATE notes SET isDeleted = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun moveToTrash(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isDeleted = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun restoreFromTrash(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isArchived = :archived, updatedAt = :timestamp WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isPinned = :pinned, updatedAt = :timestamp WHERE id = :id")
    suspend fun setPinned(id: Long, pinned: Boolean, timestamp: Long = System.currentTimeMillis())
}
