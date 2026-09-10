package cocm.glass.note.pr.domain.model

data class Note(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val noteType: NoteType = NoteType.TEXT,
    val color: NoteColor = NoteColor.DEFAULT,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val reminderTime: Long? = null,
    val hasReminder: Boolean = false,
    val checklistData: List<ChecklistItem> = emptyList(),
    val labels: List<String> = emptyList(),
    val imagePaths: List<String> = emptyList(),
    val drawingPath: String? = null
)

enum class NoteType {
    TEXT,
    CHECKLIST,
    IMAGE,
    DRAWING,
    MIXED
}

enum class NoteColor(val colorName: String, val colorValue: String) {
    DEFAULT("Default", "#FFFFFF"),
    SOFT_WHITE("Soft White", "#FAFAFA"),
    SOFT_GRAY("Soft Gray", "#E8EAED"),
    RED("Red", "#FFD7D9"),
    ORANGE("Orange", "#FFE4CC"),
    YELLOW("Yellow", "#FFF4CC"),
    GREEN("Green", "#D4F4DD"),
    BLUE("Blue", "#D3E3FD"),
    PURPLE("Purple", "#EDD7FF"),
    PINK("Pink", "#FFD9F2")
}

data class ChecklistItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String = "",
    val isChecked: Boolean = false,
    val order: Int = 0
)
