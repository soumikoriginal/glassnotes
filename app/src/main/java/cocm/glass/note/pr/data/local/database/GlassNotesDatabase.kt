package cocm.glass.note.pr.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cocm.glass.note.pr.data.local.converters.Converters
import cocm.glass.note.pr.data.local.dao.LabelDao
import cocm.glass.note.pr.data.local.dao.NoteDao
import cocm.glass.note.pr.data.local.entities.LabelEntity
import cocm.glass.note.pr.data.local.entities.NoteEntity

@Database(
    entities = [NoteEntity::class, LabelEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GlassNotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun labelDao(): LabelDao

    companion object {
        @Volatile
        private var INSTANCE: GlassNotesDatabase? = null

        fun getDatabase(context: Context): GlassNotesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GlassNotesDatabase::class.java,
                    "glass_notes_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
