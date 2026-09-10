package cocm.glass.note.pr.di

import android.content.Context
import cocm.glass.note.pr.data.backup.BackupManager
import cocm.glass.note.pr.data.local.ImageManager
import cocm.glass.note.pr.data.local.DrawingManager
import cocm.glass.note.pr.data.local.PreferencesManager
import cocm.glass.note.pr.data.local.database.GlassNotesDatabase
import cocm.glass.note.pr.data.repository.LabelRepositoryImpl
import cocm.glass.note.pr.data.repository.NoteRepositoryImpl
import cocm.glass.note.pr.domain.repository.LabelRepository
import cocm.glass.note.pr.domain.repository.NoteRepository
import cocm.glass.note.pr.domain.usecase.*
import cocm.glass.note.pr.util.BiometricHelper
import cocm.glass.note.pr.util.NotificationHelper
import cocm.glass.note.pr.util.PerformanceManager
import cocm.glass.note.pr.util.ReminderManager

interface AppContainer {
    val noteRepository: NoteRepository
    val labelRepository: LabelRepository
    val preferencesManager: PreferencesManager
    val backupManager: BackupManager
    val imageManager: ImageManager
    val drawingManager: DrawingManager
    val reminderManager: ReminderManager
    val biometricHelper: BiometricHelper
    val notificationHelper: NotificationHelper
    val performanceManager: PerformanceManager
    val noteUseCases: NoteUseCases
    val labelUseCases: LabelUseCases
    val viewModelFactory: ViewModelFactory
}

class AppContainerImpl(private val context: Context) : AppContainer {

    private val database: GlassNotesDatabase by lazy {
        GlassNotesDatabase.getDatabase(context)
    }

    override val noteRepository: NoteRepository by lazy {
        NoteRepositoryImpl(database.noteDao())
    }

    override val labelRepository: LabelRepository by lazy {
        LabelRepositoryImpl(database.labelDao())
    }

    override val preferencesManager: PreferencesManager by lazy {
        PreferencesManager(context)
    }

    override val backupManager: BackupManager by lazy {
        BackupManager(context)
    }

    override val imageManager: ImageManager by lazy {
        ImageManager(context)
    }

    override val drawingManager: DrawingManager by lazy {
        DrawingManager(context)
    }

    override val reminderManager: ReminderManager by lazy {
        ReminderManager(context)
    }

    override val biometricHelper: BiometricHelper by lazy {
        BiometricHelper(context)
    }

    override val notificationHelper: NotificationHelper by lazy {
        NotificationHelper(context)
    }

    override val performanceManager: PerformanceManager by lazy {
        PerformanceManager(context, preferencesManager)
    }

    override val noteUseCases: NoteUseCases by lazy {
        NoteUseCases(
            getAllNotes = GetAllNotesUseCase(noteRepository),
            getNoteById = GetNoteByIdUseCase(noteRepository),
            insertNote = InsertNoteUseCase(noteRepository),
            updateNote = UpdateNoteUseCase(noteRepository),
            deleteNote = DeleteNoteUseCase(noteRepository),
            getPinnedNotes = GetPinnedNotesUseCase(noteRepository),
            getArchivedNotes = GetArchivedNotesUseCase(noteRepository),
            getDeletedNotes = GetDeletedNotesUseCase(noteRepository),
            searchNotes = SearchNotesUseCase(noteRepository),
            getNotesByLabel = GetNotesByLabelUseCase(noteRepository),
            emptyTrash = EmptyTrashUseCase(noteRepository),
            togglePin = TogglePinNoteUseCase(noteRepository),
            archiveNote = ArchiveNoteUseCase(noteRepository),
            updateNoteColor = UpdateNoteColorUseCase(noteRepository),
            restoreNote = RestoreNoteUseCase(noteRepository),
            permanentlyDeleteNote = PermanentlyDeleteNoteUseCase(noteRepository)
        )
    }

    override val labelUseCases: LabelUseCases by lazy {
        LabelUseCases(
            getAllLabels = GetAllLabelsUseCase(noteRepository),
            addLabel = AddLabelUseCase(noteRepository),
            removeLabel = RemoveLabelUseCase(noteRepository)
        )
    }

    override val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(this)
    }
}
