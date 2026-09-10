package cocm.glass.note.pr.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        if (action != null) {
            Spacer(modifier = Modifier.height(32.dp))
            action()
        }
    }
}

@Composable
fun EmptyNotesState(
    onCreateNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Note,
        title = "Your thoughts belong here",
        description = "Create your first note and let Glass Notes keep it safe.",
        modifier = modifier,
        action = {
            GlassButton(
                text = "Create Note",
                onClick = onCreateNote
            )
        }
    )
}

@Composable
fun EmptySearchState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.SearchOff,
        title = "No results found",
        description = "Try searching with different keywords.",
        modifier = modifier
    )
}

@Composable
fun EmptyArchiveState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Archive,
        title = "No archived notes",
        description = "Archived notes will appear here.",
        modifier = modifier
    )
}

@Composable
fun EmptyTrashState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Delete,
        title = "Trash is empty",
        description = "Deleted notes will appear here.",
        modifier = modifier
    )
}

@Composable
fun EmptyLabelsState(
    onCreateLabel: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Label,
        title = "No labels yet",
        description = "Create labels to organize your notes.",
        modifier = modifier,
        action = {
            GlassButton(
                text = "Create Label",
                onClick = onCreateLabel
            )
        }
    )
}

@Composable
fun EmptyRemindersState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Notifications,
        title = "No reminders",
        description = "Set reminders on your notes to get notified.",
        modifier = modifier
    )
}
