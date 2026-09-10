package cocm.glass.note.pr.domain.usecase

import cocm.glass.note.pr.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetAllLabelsUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<String>> = repository.getAllLabels()
}

class AddLabelUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(noteId: Long, label: String) {
        val note = repository.getNoteById(noteId)
        note?.let {
            val updatedLabels = it.labels.toMutableList().apply { add(label) }
            repository.updateNote(it.copy(labels = updatedLabels))
        }
    }
}

class RemoveLabelUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(noteId: Long, label: String) {
        val note = repository.getNoteById(noteId)
        note?.let {
            val updatedLabels = it.labels.toMutableList().apply { remove(label) }
            repository.updateNote(it.copy(labels = updatedLabels))
        }
    }
}

data class LabelUseCases(
    val getAllLabels: GetAllLabelsUseCase,
    val addLabel: AddLabelUseCase,
    val removeLabel: RemoveLabelUseCase
)
