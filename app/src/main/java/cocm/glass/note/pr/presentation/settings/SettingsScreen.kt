package cocm.glass.note.pr.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit
) {
    val settingsState by viewModel.settingsState.collectAsState()

    Scaffold(
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
                        subtitle = settingsState.theme.name.lowercase().capitalize(),
                        icon = Icons.Default.Palette,
                        onClick = { /* Show theme picker */ }
                    )
                    
                    SettingsItem(
                        title = "Glass Intensity",
                        subtitle = settingsState.glassIntensity.name.lowercase().capitalize(),
                        icon = Icons.Default.Opacity,
                        onClick = { /* Show intensity picker */ }
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
                        subtitle = settingsState.viewMode.name.lowercase().capitalize(),
                        icon = Icons.Default.ViewModule,
                        onClick = { /* Show view mode picker */ }
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
                        onClick = { /* Show sort picker */ }
                    )
                }
            }

            // Backup Section
            item {
                SettingsSection(title = "Backup & Restore") {
                    SettingsItem(
                        title = "Export Backup",
                        subtitle = "Save notes to local file",
                        icon = Icons.Default.Upload,
                        onClick = onBackupClick
                    )
                    
                    SettingsItem(
                        title = "Import Backup",
                        subtitle = "Restore from backup file",
                        icon = Icons.Default.Download,
                        onClick = onRestoreClick
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
                            subtitle = "Use fingerprint or face",
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
                        subtitle = "Your notes. Your device. Your privacy.",
                        icon = Icons.Default.PrivacyTip,
                        onClick = { }
                    )
                }
            }
        }
    }
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
