package cocm.glass.note.pr.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cocm.glass.note.pr.data.local.PreferencesManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsState(
    val theme: Theme = Theme.SYSTEM,
    val glassIntensity: GlassIntensity = GlassIntensity.MEDIUM,
    val dynamicBackground: Boolean = true,
    val viewMode: ViewMode = ViewMode.GRID,
    val reduceMotion: Boolean = false,
    val defaultNoteColor: String = "default",
    val sortOrder: SortOrder = SortOrder.LAST_EDITED,
    val appLockEnabled: Boolean = false,
    val biometricEnabled: Boolean = false
)

enum class Theme {
    LIGHT, DARK, SYSTEM
}

enum class GlassIntensity {
    LOW, MEDIUM, HIGH
}

enum class ViewMode {
    GRID, LIST
}

enum class SortOrder {
    LAST_EDITED, CREATED, ALPHABETICAL, CUSTOM
}

class SettingsViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()

    init {
        loadSettings()
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            _settingsState.update { it.copy(theme = theme) }
            preferencesManager.setTheme(theme.name)
        }
    }

    fun setGlassIntensity(intensity: GlassIntensity) {
        viewModelScope.launch {
            _settingsState.update { it.copy(glassIntensity = intensity) }
            preferencesManager.setGlassIntensity(intensity.name)
        }
    }

    fun setDynamicBackground(enabled: Boolean) {
        viewModelScope.launch {
            _settingsState.update { it.copy(dynamicBackground = enabled) }
            preferencesManager.setDynamicBackground(enabled)
        }
    }

    fun setViewMode(mode: ViewMode) {
        viewModelScope.launch {
            _settingsState.update { it.copy(viewMode = mode) }
            preferencesManager.setViewMode(mode.name)
        }
    }

    fun setReduceMotion(enabled: Boolean) {
        viewModelScope.launch {
            _settingsState.update { it.copy(reduceMotion = enabled) }
            preferencesManager.setReduceMotion(enabled)
        }
    }

    fun setSortOrder(order: SortOrder) {
        viewModelScope.launch {
            _settingsState.update { it.copy(sortOrder = order) }
            preferencesManager.setSortOrder(order.name)
        }
    }

    fun setAppLock(enabled: Boolean) {
        viewModelScope.launch {
            _settingsState.update { it.copy(appLockEnabled = enabled) }
            preferencesManager.setAppLockEnabled(enabled)
        }
    }

    fun setBiometric(enabled: Boolean) {
        viewModelScope.launch {
            _settingsState.update { it.copy(biometricEnabled = enabled) }
            preferencesManager.setBiometricEnabled(enabled)
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            preferencesManager.settingsFlow.collect { prefs ->
                _settingsState.update {
                    it.copy(
                        theme = Theme.valueOf(prefs.theme),
                        glassIntensity = GlassIntensity.valueOf(prefs.glassIntensity),
                        dynamicBackground = prefs.dynamicBackground,
                        viewMode = ViewMode.valueOf(prefs.viewMode),
                        reduceMotion = prefs.reduceMotion,
                        sortOrder = SortOrder.valueOf(prefs.sortOrder),
                        appLockEnabled = prefs.appLockEnabled,
                        biometricEnabled = prefs.biometricEnabled
                    )
                }
            }
        }
    }
}
