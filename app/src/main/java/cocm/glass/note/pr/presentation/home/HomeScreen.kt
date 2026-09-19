package cocm.glass.note.pr.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToEditor: (Long?) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToTrash: () -> Unit,
    onNavigateToLabels: () -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Glass Notes",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                NavigationDrawerItem(
                    label = { Text("Notes") },
                    icon = { Icon(Icons.Default.Description, contentDescription = null) },
                    selected = true,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Labels") },
                    icon = { Icon(Icons.Default.Label, contentDescription = null) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            onNavigateToLabels()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Archive") },
                    icon = { Icon(Icons.Default.Archive, contentDescription = null) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            onNavigateToArchive()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Trash") },
                    icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            onNavigateToTrash()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            onNavigateToSettings()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
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
                        navigationIcon = Icons.Default.Menu,
                        onNavigationClick = {
                            coroutineScope.launch { drawerState.open() }
                        },
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
                        onPinClick = { viewModel.togglePinNote(it) }
                    )
                }
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
    onPinClick: (Note) -> Unit
) {
    if (viewMode == ViewMode.GRID) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp
        ) {
            if (pinnedNotes.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        text = "PINNED",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }
                items(pinnedNotes, key = { "pinned_${it.id}" }) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note) },
                        onPinClick = { onPinClick(note) }
                    )
                }
            }

            if (notes.isNotEmpty()) {
                if (pinnedNotes.isNotEmpty()) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "OTHER NOTES",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }
                }
                items(notes, key = { "note_${it.id}" }) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note) },
                        onPinClick = { onPinClick(note) }
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (pinnedNotes.isNotEmpty()) {
                item {
                    Text(
                        text = "PINNED",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }
                items(pinnedNotes, key = { "pinned_${it.id}" }) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note) },
                        onPinClick = { onPinClick(note) }
                    )
                }
            }

            if (notes.isNotEmpty()) {
                if (pinnedNotes.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "OTHER NOTES",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }
                }
                items(notes, key = { "note_${it.id}" }) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note) },
                        onPinClick = { onPinClick(note) }
                    )
                }
            }
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
