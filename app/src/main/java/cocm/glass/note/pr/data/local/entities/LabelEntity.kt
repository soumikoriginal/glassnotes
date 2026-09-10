package cocm.glass.note.pr.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "labels")
data class LabelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: String = "default",
    val createdAt: Long = System.currentTimeMillis()
)
