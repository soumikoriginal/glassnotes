package cocm.glass.note.pr.data.local.dao

import androidx.room.*
import cocm.glass.note.pr.data.local.entities.LabelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Query("SELECT * FROM labels ORDER BY name ASC")
    fun getAllLabels(): Flow<List<LabelEntity>>

    @Query("SELECT * FROM labels WHERE id = :id")
    suspend fun getLabelById(id: Long): LabelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(label: LabelEntity): Long

    @Update
    suspend fun updateLabel(label: LabelEntity)

    @Delete
    suspend fun deleteLabel(label: LabelEntity)

    @Query("DELETE FROM labels WHERE id = :id")
    suspend fun deleteLabelById(id: Long)
}
