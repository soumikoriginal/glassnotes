package cocm.glass.note.pr.domain.repository

import cocm.glass.note.pr.domain.model.Label
import kotlinx.coroutines.flow.Flow

interface LabelRepository {
    fun getAllLabels(): Flow<List<Label>>
    suspend fun getLabelById(id: Long): Label?
    suspend fun insertLabel(label: Label): Long
    suspend fun updateLabel(label: Label)
    suspend fun deleteLabel(label: Label)
    suspend fun deleteLabelById(id: Long)
}
