package cocm.glass.note.pr.data.local.converters

import androidx.room.TypeConverter
import cocm.glass.note.pr.domain.model.ChecklistItem
import cocm.glass.note.pr.domain.model.NoteType
import cocm.glass.note.pr.domain.model.NoteColor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromNoteType(value: NoteType): String = value.name

    @TypeConverter
    fun toNoteType(value: String): NoteType = try {
        NoteType.valueOf(value)
    } catch (e: Exception) {
        NoteType.TEXT
    }

    @TypeConverter
    fun fromNoteColor(value: NoteColor): String = value.name

    @TypeConverter
    fun toNoteColor(value: String): NoteColor = try {
        NoteColor.valueOf(value)
    } catch (e: Exception) {
        NoteColor.DEFAULT
    }

    @TypeConverter
    fun fromChecklistItems(items: List<ChecklistItem>): String = gson.toJson(items)

    @TypeConverter
    fun toChecklistItems(json: String): List<ChecklistItem> {
        val type = object : TypeToken<List<ChecklistItem>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String = gson.toJson(list)

    @TypeConverter
    fun toStringList(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}
