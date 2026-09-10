package cocm.glass.note.pr.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cocm.glass.note.pr.domain.model.Note
import cocm.glass.note.pr.ui.components.*
import cocm.glass.note.pr.ui.theme.getNoteColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToEditor: (Long?) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (showSearchBar) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.searchNotes(it)
                    },
                    onClose = {
                        showSearchBar = false
                        searchQuery = ""
                        viewModel.searchNotes("")
                    }
                )
            } else {
                GlassTopBar(
                    title = "Glass Notes",
                    actions = {
                        GlassIconButton(
                            onClick = { showSearchBar = true },
                            icon = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                        GlassIconButton(
                            onClick = {
                                viewModel.toggleViewMode()
                            },
                            icon = if (uiState.viewMode == ViewMode.GRID)
                                Icons.Default.List else Icons.Outlined.GridView,
                            contentDescription = "Toggle view"
                        )
                        GlassIconButton(
                            onClick = onNavigateToSettings,
                            icon = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                )
            }
        },
        floatingActionButton = {
            GlassFAB(
                onClick = { onNavigateToEditor(null) },
                icon = Icons.Default.Add,
                contentDescription = "Create note"
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.notes.isEmpty() && uiState.pinnedNotes.isEmpty()) {
                EmptyState(
                    title = "Your thoughts belong here",
                    subtitle = "Create your first note and let Glass Notes keep it safe",
                    icon = Icons.Default.Note,
                    actionText = "Create Note",
                    onActionClick = { onNavigateToEditor(null) }
                )
            } else {
                NotesContent(
                    pinnedNotes = uiState.pinnedNotes,
                    notes = uiState.notes,
                    viewMode = uiState.viewMode,
                    onNoteClick = { onNavigateToEditor(it.id) },
                    onPinClick = { viewModel.togglePinNote(it) },
                    onArchiveClick = { viewModel.archiveNote(it) },
                    onDeleteClick = { viewModel.deleteNote(it) }
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconButton(
                onClick = onClose,
                icon = Icons.Default.ArrowBack,
                contentDescription = "Close search"
            )
            GlassTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                placeholder = "Search notes...",
                singleLine = true
            )
        }
    }
}

@Composable
private fun NotesContent(
    pinnedNotes: List<Note>,
    notes: List<Note>,
    viewMode: ViewMode,
    onNoteClick: (Note) -> Unit,
    onPinClick: (Note) -> Unit,
    onArchiveClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (pinnedNotes.isNotEmpty()) {
            item {
                Text(
                    text = "PINNED",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
            }
            
            item {
                if (viewMode == ViewMode.GRID) {
                    NoteGrid(
                        notes = pinnedNotes,
                        onNoteClick = onNoteClick,
                        onPinClick = onPinClick
                    )
                } else {
                    NoteList(
                        notes = pinnedNotes,
                        onNoteClick = onNoteClick,
                        onPinClick = onPinClick
                    )
                }
            }
        }

        if (notes.isNotEmpty()) {
            if (pinnedNotes.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "OTHER NOTES",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                }
            }
            
            item {
                if (viewMode == ViewMode.GRID) {
                    NoteGrid(
                        notes = notes,
                        onNoteClick = onNoteClick,
                        onPinClick = onPinClick
                    )
                } else {
                    NoteList(
                        notes = notes,
                        onNoteClick = onNoteClick,
                        onPinClick = onPinClick
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteGrid(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onPinClick: (Note) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.height(((notes.size / 2 + 1) * 150).dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp,
        userScrollEnabled = false
    ) {
        items(notes, key = { it.id }) { note ->
            NoteCard(
                note = note,
                onClick = { onNoteClick(note) },
                onPinClick = { onPinClick(note) }
            )
        }
    }
}

@Composable
private fun NoteList(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onPinClick: (Note) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        notes.forEach { note ->
            NoteCard(
                note = note,
                onClick = { onNoteClick(note) },
                onPinClick = { onPinClick(note) }
            )
        }
    }
}

@Composable
private fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onPinClick: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        color = getNoteColor(note.color)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (note.title.isNotBlank()) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (note.content.isNotBlank()) {
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            AnimatedVisibility(visible = note.isPinned) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
