package cocm.glass.note.pr.presentation.trash

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cocm.glass.note.pr.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    viewModel: TrashViewModel,
    onNavigateBack: () -> Unit,
    onNoteClick: (Long) -> Unit
) {
    val deletedNotes by viewModel.deletedNotes.collectAsState()
    val showEmptyTrashDialog by viewModel.showEmptyTrashDialog.collectAsState()

    Scaffold(
        topBar = {
            GlassTopBar(
                title = "Trash",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = onNavigateBack,
                actions = {
                    if (deletedNotes.isNotEmpty()) {
                        GlassIconButton(
                            onClick = viewModel::showEmptyTrashDialog,
                            icon = Icons.Default.DeleteForever
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (deletedNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    
                    Text(
                        text = "Trash is empty",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    
                    Text(
                        text = "Deleted notes will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(deletedNotes, key = { it.id }) { note ->
                    TrashNoteCard(
                        note = note,
                        onClick = { onNoteClick(note.id) },
                        onRestore = { viewModel.onRestore(note) },
                        onPermanentlyDelete = { viewModel.onPermanentlyDelete(note) }
                    )
                }
            }
        }
    }

    if (showEmptyTrashDialog) {
        GlassDialog(
            onDismissRequest = viewModel::hideEmptyTrashDialog,
            title = "Empty trash?",
            text = "All notes in trash will be permanently deleted. This action cannot be undone.",
            confirmButton = {
                GlassButton(
                    onClick = viewModel::emptyTrash,
                    text = "Empty Trash"
                )
            },
            dismissButton = {
                GlassButton(
                    onClick = viewModel::hideEmptyTrashDialog,
                    text = "Cancel"
                )
            }
        )
    }
}

@Composable
fun TrashNoteCard(
    note: cocm.glass.note.pr.domain.model.Note,
    onClick: () -> Unit,
    onRestore: () -> Unit,
    onPermanentlyDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (note.title.isNotEmpty()) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (note.content.isNotEmpty()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(
                    onClick = onRestore,
                    icon = Icons.Default.RestoreFromTrash
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                GlassIconButton(
                    onClick = onPermanentlyDelete,
                    icon = Icons.Default.DeleteForever
                )
            }
        }
    }
}
