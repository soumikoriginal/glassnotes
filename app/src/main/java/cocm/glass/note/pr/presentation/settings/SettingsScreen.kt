package cocm.glass.note.pr.presentation.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cocm.glass.note.pr.di.AppContainer
import cocm.glass.note.pr.ui.components.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private fun String.capitalized(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    container: AppContainer,
    onNavigateBack: () -> Unit
) {
    val settingsState by viewModel.settingsState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showThemeDialog by remember { mutableStateOf(false) }
    var showIntensityDialog by remember { mutableStateOf(false) }
    var showViewModeDialog by remember { mutableStateOf(false) }
    var showSortOrderDialog by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val notes = container.noteRepository.getAllNotes().first()
                val result = container.backupManager.exportBackup(notes, it)
                if (result.isSuccess) {
                    snackbarHostState.showSnackbar("Backup exported successfully (${notes.size} notes)")
                } else {
                    snackbarHostState.showSnackbar("Failed to export: ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val result = container.backupManager.importBackup(it)
                if (result.isSuccess) {
                    val notes = result.getOrNull() ?: emptyList()
                    notes.forEach { note ->
                        container.noteRepository.insertNote(note)
                    }
                    snackbarHostState.showSnackbar("Restored ${notes.size} notes successfully")
                } else {
                    snackbarHostState.showSnackbar("Failed to restore: ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GlassTopBar(
                title = "Settings",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Appearance Section
            item {
                SettingsSection(title = "Appearance") {
                    SettingsItem(
                        title = "Theme",
                        subtitle = settingsState.theme.name.lowercase().capitalized(),
                        icon = Icons.Default.Palette,
                        onClick = { showThemeDialog = true }
                    )

                    SettingsItem(
                        title = "Glass Intensity",
                        subtitle = settingsState.glassIntensity.name.lowercase().capitalized(),
                        icon = Icons.Default.Opacity,
                        onClick = { showIntensityDialog = true }
                    )

                    SettingsSwitchItem(
                        title = "Dynamic Background",
                        subtitle = "Subtle animated background",
                        icon = Icons.Default.Wallpaper,
                        checked = settingsState.dynamicBackground,
                        onCheckedChange = viewModel::setDynamicBackground
                    )

                    SettingsItem(
                        title = "View Mode",
                        subtitle = settingsState.viewMode.name.lowercase().capitalized(),
                        icon = Icons.Default.ViewModule,
                        onClick = { showViewModeDialog = true }
                    )

                    SettingsSwitchItem(
                        title = "Reduce Motion",
                        subtitle = "Disable animations",
                        icon = Icons.Default.Animation,
                        checked = settingsState.reduceMotion,
                        onCheckedChange = viewModel::setReduceMotion
                    )
                }
            }

            // Notes Section
            item {
                SettingsSection(title = "Notes") {
                    SettingsItem(
                        title = "Sort Order",
                        subtitle = when (settingsState.sortOrder) {
                            SortOrder.LAST_EDITED -> "Last edited"
                            SortOrder.CREATED -> "Created date"
                            SortOrder.ALPHABETICAL -> "Alphabetical"
                            SortOrder.CUSTOM -> "Custom"
                        },
                        icon = Icons.Default.Sort,
                        onClick = { showSortOrderDialog = true }
                    )
                }
            }

            // Backup Section
            item {
                SettingsSection(title = "Backup & Restore") {
                    SettingsItem(
                        title = "Export Backup",
                        subtitle = "Save notes to local JSON file",
                        icon = Icons.Default.Upload,
                        onClick = {
                            exportLauncher.launch("glass_notes_backup_${System.currentTimeMillis()}.json")
                        }
                    )

                    SettingsItem(
                        title = "Import Backup",
                        subtitle = "Restore from JSON backup file",
                        icon = Icons.Default.Download,
                        onClick = {
                            importLauncher.launch(arrayOf("application/json", "*/*"))
                        }
                    )
                }
            }

            // Security Section
            item {
                SettingsSection(title = "Security") {
                    SettingsSwitchItem(
                        title = "App Lock",
                        subtitle = "Protect app with PIN or biometric",
                        icon = Icons.Default.Lock,
                        checked = settingsState.appLockEnabled,
                        onCheckedChange = viewModel::setAppLock
                    )

                    if (settingsState.appLockEnabled) {
                        SettingsSwitchItem(
                            title = "Biometric Unlock",
                            subtitle = "Use fingerprint, face, or device PIN",
                            icon = Icons.Default.Fingerprint,
                            checked = settingsState.biometricEnabled,
                            onCheckedChange = viewModel::setBiometric
                        )
                    }
                }
            }

            // About Section
            item {
                SettingsSection(title = "About") {
                    SettingsItem(
                        title = "Glass Notes",
                        subtitle = "Version 1.0.0",
                        icon = Icons.Default.Info,
                        onClick = { }
                    )

                    SettingsItem(
                        title = "Privacy",
                        subtitle = "Your notes. Your device. Fully offline.",
                        icon = Icons.Default.PrivacyTip,
                        onClick = { }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showThemeDialog) {
        SingleChoiceDialog(
            title = "Choose Theme",
            options = listOf(
                Theme.SYSTEM to "System Default",
                Theme.LIGHT to "Light",
                Theme.DARK to "Dark"
            ),
            selectedOption = settingsState.theme,
            onSelect = { viewModel.setTheme(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showIntensityDialog) {
        SingleChoiceDialog(
            title = "Glass Intensity",
            options = listOf(
                GlassIntensity.LOW to "Low",
                GlassIntensity.MEDIUM to "Medium",
                GlassIntensity.HIGH to "High"
            ),
            selectedOption = settingsState.glassIntensity,
            onSelect = { viewModel.setGlassIntensity(it) },
            onDismiss = { showIntensityDialog = false }
        )
    }

    if (showViewModeDialog) {
        SingleChoiceDialog(
            title = "Default View Mode",
            options = listOf(
                ViewMode.GRID to "Grid (2 columns)",
                ViewMode.LIST to "List (Single column)"
            ),
            selectedOption = settingsState.viewMode,
            onSelect = { viewModel.setViewMode(it) },
            onDismiss = { showViewModeDialog = false }
        )
    }

    if (showSortOrderDialog) {
        SingleChoiceDialog(
            title = "Sort Notes By",
            options = listOf(
                SortOrder.LAST_EDITED to "Last edited",
                SortOrder.CREATED to "Created date",
                SortOrder.ALPHABETICAL to "Alphabetical (A-Z)"
            ),
            selectedOption = settingsState.sortOrder,
            onSelect = { viewModel.setSortOrder(it) },
            onDismiss = { showSortOrderDialog = false }
        )
    }
}

@Composable
fun <T> SingleChoiceDialog(
    title: String,
    options: List<Pair<T, String>>,
    selectedOption: T,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { (option, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(option)
                                onDismiss()
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = {
                                onSelect(option)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )

        GlassCard {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            GlassSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
