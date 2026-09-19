package cocm.glass.note.pr.presentation.editor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import cocm.glass.note.pr.di.AppContainer
import cocm.glass.note.pr.domain.model.NoteColor
import cocm.glass.note.pr.domain.model.NoteType
import cocm.glass.note.pr.presentation.components.*
import cocm.glass.note.pr.ui.components.*
import java.io.File

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
    var showDrawingCanvas by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = container.imageHelper.saveImageFromUri(it)
            if (savedPath != null) {
                viewModel.addImage(savedPath)
            }
        }
    }

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
                            Icons.Filled.PushPin else Icons.Outlined.PushPin,
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
                onImageClick = { imagePickerLauncher.launch("image/*") },
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
            // Labels display
            val labels = uiState.note?.labels ?: emptyList()
            if (labels.isNotEmpty()) {
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(labels) { label ->
                            InputChip(
                                selected = true,
                                onClick = { },
                                label = { Text(label) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove label",
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable {
                                                viewModel.updateLabels(labels.filter { it != label })
                                            }
                                    )
                                }
                            )
                        }
                    }
                }
            }

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

                Spacer(modifier = Modifier.height(4.dp))
            }

            // Attached Drawing preview
            if (uiState.drawingPath != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = File(uiState.drawingPath!!),
                            contentDescription = "Drawing",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        IconButton(
                            onClick = { viewModel.removeDrawing() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove drawing",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Attached Images preview
            if (uiState.imagePaths.isNotEmpty()) {
                items(uiState.imagePaths) { imagePath ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = File(imagePath),
                            contentDescription = "Attached image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { viewModel.removeImage(imagePath) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove image",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
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

    if (showDrawingCanvas) {
        DrawingDialog(
            onDrawingSaved = { bitmap ->
                val path = container.drawingHelper.saveDrawing(bitmap)
                if (path != null) {
                    viewModel.setDrawing(path)
                }
            },
            onDismiss = { showDrawingCanvas = false }
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
