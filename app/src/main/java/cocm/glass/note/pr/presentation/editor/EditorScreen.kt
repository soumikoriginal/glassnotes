package cocm.glass.note.pr.presentation.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cocm.glass.note.pr.di.AppContainer
import cocm.glass.note.pr.domain.model.NoteColor
import cocm.glass.note.pr.domain.model.NoteType
import cocm.glass.note.pr.presentation.components.*
import cocm.glass.note.pr.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    noteId: Long?,
    viewModel: EditorViewModel,
    onNavigateBack: () -> Unit,
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showColorPicker by remember { mutableStateOf(false) }
    var showLabelPicker by remember { mutableStateOf(false) }
    var showReminderPicker by remember { mutableStateOf(false) }
    var showImagePicker by remember { mutableStateOf(false) }
    var showDrawingCanvas by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            viewModel.loadNote(noteId)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GlassTopBar(
                title = "",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = {
                    viewModel.saveNote()
                    onNavigateBack()
                },
                actions = {
                    GlassIconButton(
                        onClick = { viewModel.togglePin() },
                        icon = if (uiState.note?.isPinned == true)
                            Icons.Default.PushPin else Icons.Default.PushPin,
                        contentDescription = "Pin note",
                        tint = if (uiState.note?.isPinned == true)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    GlassIconButton(
                        onClick = {
                            viewModel.archive()
                            onNavigateBack()
                        },
                        icon = Icons.Default.Archive,
                        contentDescription = "Archive"
                    )
                    GlassIconButton(
                        onClick = {
                            viewModel.delete()
                            onNavigateBack()
                        },
                        icon = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            )
        },
        bottomBar = {
            EditorBottomBar(
                noteType = uiState.note?.noteType ?: NoteType.TEXT,
                onChecklistClick = { viewModel.addChecklistItem() },
                onImageClick = { showImagePicker = true },
                onDrawingClick = { showDrawingCanvas = true },
                onColorClick = { showColorPicker = true },
                onLabelClick = { showLabelPicker = true },
                onReminderClick = { showReminderPicker = true }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                // Title field
                GlassTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    placeholder = "Title",
                    textStyle = MaterialTheme.typography.headlineMedium,
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Checklist items
            if (uiState.note?.noteType == NoteType.CHECKLIST && uiState.checklistItems.isNotEmpty()) {
                items(uiState.checklistItems) { item ->
                    ChecklistItemRow(
                        item = item,
                        onCheckedChange = { checked ->
                            viewModel.updateChecklistItem(item.copy(isChecked = checked))
                        },
                        onTextChange = { text ->
                            viewModel.updateChecklistItem(item.copy(text = text))
                        },
                        onDelete = { viewModel.deleteChecklistItem(item.id) }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            item {
                // Content field (for text notes or additional content)
                if (uiState.note?.noteType != NoteType.CHECKLIST) {
                    GlassTextField(
                        value = uiState.content,
                        onValueChange = { viewModel.updateContent(it) },
                        placeholder = "Note",
                        textStyle = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }

                // Metadata
                if (uiState.note != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Edited ${formatTimestamp(uiState.note!!.updatedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }

    // Dialogs
    if (showColorPicker) {
        ColorPickerDialog(
            currentColor = uiState.note?.color ?: NoteColor.DEFAULT,
            onColorSelected = { viewModel.changeColor(it) },
            onDismiss = { showColorPicker = false }
        )
    }

    if (showLabelPicker) {
        LabelPickerDialog(
            availableLabels = uiState.availableLabels,
            selectedLabels = uiState.note?.labels ?: emptyList(),
            onLabelsChanged = { viewModel.updateLabels(it) },
            onCreateLabel = { viewModel.createLabel(it) },
            onDismiss = { showLabelPicker = false }
        )
    }

    if (showReminderPicker) {
        ReminderPickerDialog(
            currentReminder = uiState.note?.reminderTime,
            onReminderSet = { viewModel.setReminder(it) },
            onReminderRemove = { viewModel.removeReminder() },
            onDismiss = { showReminderPicker = false }
        )
    }
}

@Composable
private fun EditorBottomBar(
    noteType: NoteType,
    onChecklistClick: () -> Unit,
    onImageClick: () -> Unit,
    onDrawingClick: () -> Unit,
    onColorClick: () -> Unit,
    onLabelClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    GlassBottomBar {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GlassIconButton(
                onClick = onChecklistClick,
                icon = Icons.Default.CheckBox,
                contentDescription = "Add checklist item"
            )
            GlassIconButton(
                onClick = onImageClick,
                icon = Icons.Default.Image,
                contentDescription = "Add image"
            )
            GlassIconButton(
                onClick = onDrawingClick,
                icon = Icons.Default.Draw,
                contentDescription = "Add drawing"
            )
            GlassIconButton(
                onClick = onColorClick,
                icon = Icons.Default.Palette,
                contentDescription = "Change color"
            )
            GlassIconButton(
                onClick = onLabelClick,
                icon = Icons.Default.Label,
                contentDescription = "Add label"
            )
            GlassIconButton(
                onClick = onReminderClick,
                icon = Icons.Default.Alarm,
                contentDescription = "Set reminder"
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        diff < 604800000 -> "${diff / 86400000}d ago"
        else -> "${diff / 604800000}w ago"
    }
}
