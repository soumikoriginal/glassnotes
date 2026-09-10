package cocm.glass.note.pr.data.repository

import cocm.glass.note.pr.data.local.dao.LabelDao
import cocm.glass.note.pr.data.local.entities.LabelEntity
import cocm.glass.note.pr.domain.model.Label
import cocm.glass.note.pr.domain.repository.LabelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LabelRepositoryImpl(private val labelDao: LabelDao) : LabelRepository {
    
    override fun getAllLabels(): Flow<List<Label>> = 
        labelDao.getAllLabels().map { it.map { entity -> entity.toDomain() } }

    override suspend fun getLabelById(id: Long): Label? = 
        labelDao.getLabelById(id)?.toDomain()

    override suspend fun insertLabel(label: Label): Long = 
        labelDao.insertLabel(label.toEntity())

    override suspend fun updateLabel(label: Label) = 
        labelDao.updateLabel(label.toEntity())

    override suspend fun deleteLabel(label: Label) = 
        labelDao.deleteLabel(label.toEntity())

    override suspend fun deleteLabelById(id: Long) = 
        labelDao.deleteLabelById(id)

    private fun LabelEntity.toDomain() = Label(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt
    )

    private fun Label.toEntity() = LabelEntity(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt
    )
}
