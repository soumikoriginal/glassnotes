package cocm.glass.note.pr.presentation.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cocm.glass.note.pr.domain.model.NoteColor
import cocm.glass.note.pr.ui.components.GlassButton
import cocm.glass.note.pr.ui.components.GlassCard
import cocm.glass.note.pr.ui.components.GlassIconButton
import cocm.glass.note.pr.ui.components.GlassTextField
import cocm.glass.note.pr.ui.theme.GlassTheme
import cocm.glass.note.pr.ui.theme.getNoteColor
import java.util.*

@Composable
fun ColorPickerDialog(
    currentColor: NoteColor,
    onColorSelected: (NoteColor) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Note Color",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val colors = listOf(
                    NoteColor.DEFAULT,
                    NoteColor.SOFT_WHITE,
                    NoteColor.SOFT_GRAY,
                    NoteColor.RED,
                    NoteColor.ORANGE,
                    NoteColor.YELLOW,
                    NoteColor.GREEN,
                    NoteColor.BLUE,
                    NoteColor.PURPLE,
                    NoteColor.PINK
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.chunked(3).forEach { rowColors ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowColors.forEach { color ->
                                ColorOption(
                                    color = color,
                                    isSelected = color == currentColor,
                                    onClick = {
                                        onColorSelected(color)
                                        onDismiss()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill empty slots
                            repeat(3 - rowColors.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlassButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun ColorOption(
    color: NoteColor,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = getNoteColor(color)
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(color = backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LabelPickerDialog(
    availableLabels: List<String>,
    selectedLabels: List<String>,
    onLabelsChanged: (List<String>) -> Unit,
    onCreateLabel: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newLabelText by remember { mutableStateOf("") }
    val selectedSet = remember(selectedLabels) { selectedLabels.toMutableSet() }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Labels",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Create new label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassTextField(
                        value = newLabelText,
                        onValueChange = { newLabelText = it },
                        placeholder = "New label",
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    GlassButton(
                        onClick = {
                            if (newLabelText.isNotBlank()) {
                                onCreateLabel(newLabelText.trim())
                                newLabelText = ""
                            }
                        },
                        enabled = newLabelText.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Available labels
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableLabels) { label ->
                        LabelRow(
                            label = label,
                            isSelected = selectedSet.contains(label),
                            onClick = {
                                if (selectedSet.contains(label)) {
                                    selectedSet.remove(label)
                                } else {
                                    selectedSet.add(label)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlassButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    GlassButton(
                        onClick = {
                            onLabelsChanged(selectedSet.toList())
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else
                    Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ReminderPickerDialog(
    currentReminder: Long?,
    onReminderSet: (Long) -> Unit,
    onReminderRemove: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    
    if (currentReminder != null) {
        calendar.timeInMillis = currentReminder
    }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Set Reminder",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (currentReminder != null) {
                    Text(
                        text = "Current reminder: ${formatReminderTime(currentReminder)}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlassButton(
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    calendar.set(Calendar.YEAR, year)
                                    calendar.set(Calendar.MONTH, month)
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                    
                                    TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                            calendar.set(Calendar.MINUTE, minute)
                                            calendar.set(Calendar.SECOND, 0)
                                            onReminderSet(calendar.timeInMillis)
                                            onDismiss()
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pick Date & Time")
                    }

                    if (currentReminder != null) {
                        GlassButton(
                            onClick = {
                                onReminderRemove()
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Remove Reminder")
                        }
                    }

                    GlassButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

private fun formatReminderTime(timeMillis: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    return String.format(
        "%02d/%02d/%04d %02d:%02d",
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE)
    )
}

@Composable
fun ChecklistItemRow(
    item: cocm.glass.note.pr.domain.model.ChecklistItem,
    onCheckedChange: (Boolean) -> Unit,
    onTextChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange
        )
        
        GlassTextField(
            value = item.text,
            onValueChange = onTextChange,
            placeholder = "Checklist item",
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        
        GlassIconButton(
            onClick = onDelete,
            icon = Icons.Default.Close,
            contentDescription = "Delete item"
        )
    }
}

@Composable
fun DrawingDialog(
    onDrawingSaved: (android.graphics.Bitmap) -> Unit,
    onDismiss: () -> Unit
) {
    val paths = remember { mutableStateListOf<List<androidx.compose.ui.geometry.Offset>>() }
    var currentPath = remember { mutableStateListOf<androidx.compose.ui.geometry.Offset>() }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Drawing Canvas",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row {
                        TextButton(onClick = {
                            paths.clear()
                            currentPath.clear()
                        }) {
                            Text("Clear")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        GlassIconButton(
                            onClick = onDismiss,
                            icon = Icons.Default.Close
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                ) {
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .androidx.compose.ui.input.pointer.pointerInput(Unit) {
                                androidx.compose.foundation.gestures.detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPath = mutableStateListOf(offset)
                                    },
                                    onDrag = { change, _ ->
                                        currentPath.add(change.position)
                                    },
                                    onDragEnd = {
                                        if (currentPath.isNotEmpty()) {
                                            paths.add(currentPath.toList())
                                            currentPath = mutableStateListOf()
                                        }
                                    }
                                )
                            }
                    ) {
                        for (path in paths) {
                            for (i in 0 until path.size - 1) {
                                drawLine(
                                    color = Color.Black,
                                    start = path[i],
                                    end = path[i + 1],
                                    strokeWidth = 6f,
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            }
                        }
                        for (i in 0 until currentPath.size - 1) {
                            drawLine(
                                color = Color.Black,
                                start = currentPath[i],
                                end = currentPath[i + 1],
                                strokeWidth = 6f,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val bitmap = android.graphics.Bitmap.createBitmap(800, 800, android.graphics.Bitmap.Config.ARGB_8888)
                            val canvas = android.graphics.Canvas(bitmap)
                            canvas.drawColor(android.graphics.Color.WHITE)
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                strokeWidth = 6f
                                style = android.graphics.Paint.Style.STROKE
                                strokeCap = android.graphics.Paint.Cap.ROUND
                                strokeJoin = android.graphics.Paint.Join.ROUND
                                isAntiAlias = true
                            }
                            for (path in paths) {
                                for (i in 0 until path.size - 1) {
                                    canvas.drawLine(path[i].x, path[i].y, path[i + 1].x, path[i + 1].y, paint)
                                }
                            }
                            onDrawingSaved(bitmap)
                            onDismiss()
                        }
                    ) {
                        Text("Save Drawing")
                    }
                }
            }
        }
    }
}

