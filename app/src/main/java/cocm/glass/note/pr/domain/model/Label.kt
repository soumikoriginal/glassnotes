package cocm.glass.note.pr.domain.model

data class Label(
    val id: Long = 0,
    val name: String,
    val color: String = "default",
    val createdAt: Long = System.currentTimeMillis()
)
