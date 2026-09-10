package cocm.glass.note.pr.data.local.entities

import cocm.glass.note.pr.domain.model.Note

fun NoteEntity.toDomainModel(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        noteType = noteType,
        color = color,
        isPinned = isPinned,
        isArchived = isArchived,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        reminderTime = reminderTime,
        hasReminder = hasReminder,
        checklistData = checklistData,
        labels = labels,
        imagePaths = imagePaths,
        drawingPath = drawingPath
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        noteType = noteType,
        color = color,
        isPinned = isPinned,
        isArchived = isArchived,
        isDeleted = isDeleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        reminderTime = reminderTime,
        hasReminder = hasReminder,
        checklistData = checklistData,
        labels = labels,
        imagePaths = imagePaths,
        drawingPath = drawingPath
    )
}
