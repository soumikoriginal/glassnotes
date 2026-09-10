package cocm.glass.note.pr.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "glass_notes_preferences")

data class SettingsPreferences(
    val theme: String,
    val glassIntensity: String,
    val dynamicBackground: Boolean,
    val viewMode: String,
    val reduceMotion: Boolean,
    val sortOrder: String,
    val appLockEnabled: Boolean,
    val biometricEnabled: Boolean
)

class PreferencesManager(private val context: Context) {

    companion object {
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val VIEW_MODE = stringPreferencesKey("view_mode")
        private val GLASS_INTENSITY = stringPreferencesKey("glass_intensity")
        private val DYNAMIC_BACKGROUND = booleanPreferencesKey("dynamic_background")
        private val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
        private val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        private val USE_BIOMETRIC = booleanPreferencesKey("use_biometric")
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val SORT_ORDER = stringPreferencesKey("sort_order")
        private val PERFORMANCE_MODE = stringPreferencesKey("performance_mode")
        private val RECENT_SEARCHES = stringSetPreferencesKey("recent_searches")

        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"
        const val THEME_SYSTEM = "SYSTEM"

        const val VIEW_GRID = "GRID"
        const val VIEW_LIST = "LIST"

        const val SORT_LAST_EDITED = "LAST_EDITED"
        const val SORT_CREATED = "CREATED"
        const val SORT_ALPHABETICAL = "ALPHABETICAL"

        const val PERFORMANCE_HIGH = "high"
        const val PERFORMANCE_BALANCED = "balanced"
        const val PERFORMANCE_BATTERY = "battery"

        const val GLASS_INTENSITY_LOW = "LOW"
        const val GLASS_INTENSITY_MEDIUM = "MEDIUM"
        const val GLASS_INTENSITY_HIGH = "HIGH"
    }

    val settingsFlow: Flow<SettingsPreferences> = context.dataStore.data.map { preferences ->
        SettingsPreferences(
            theme = preferences[THEME_MODE] ?: THEME_SYSTEM,
            glassIntensity = preferences[GLASS_INTENSITY] ?: GLASS_INTENSITY_MEDIUM,
            dynamicBackground = preferences[DYNAMIC_BACKGROUND] ?: true,
            viewMode = preferences[VIEW_MODE] ?: VIEW_GRID,
            reduceMotion = preferences[REDUCE_MOTION] ?: false,
            sortOrder = preferences[SORT_ORDER] ?: SORT_LAST_EDITED,
            appLockEnabled = preferences[APP_LOCK_ENABLED] ?: false,
            biometricEnabled = preferences[USE_BIOMETRIC] ?: true
        )
    }

    val recentSearches: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[RECENT_SEARCHES]?.toList() ?: emptyList()
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: THEME_SYSTEM
    }

    val viewMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[VIEW_MODE] ?: VIEW_GRID
    }

    val glassIntensity: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[GLASS_INTENSITY] ?: GLASS_INTENSITY_MEDIUM
    }

    val dynamicBackground: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DYNAMIC_BACKGROUND] ?: true
    }

    val reduceMotion: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REDUCE_MOTION] ?: false
    }

    val appLockEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[APP_LOCK_ENABLED] ?: false
    }

    val useBiometric: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[USE_BIOMETRIC] ?: true
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }

    val isOnboardingComplete: Flow<Boolean> = onboardingCompleted

    val sortOrder: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SORT_ORDER] ?: SORT_LAST_EDITED
    }

    val performanceMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PERFORMANCE_MODE] ?: PERFORMANCE_BALANCED
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = theme
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    suspend fun setViewMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[VIEW_MODE] = mode
        }
    }

    suspend fun setGlassIntensity(intensity: String) {
        context.dataStore.edit { preferences ->
            preferences[GLASS_INTENSITY] = intensity
        }
    }

    suspend fun setDynamicBackground(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_BACKGROUND] = enabled
        }
    }

    suspend fun setReduceMotion(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REDUCE_MOTION] = enabled
        }
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[APP_LOCK_ENABLED] = enabled
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_BIOMETRIC] = enabled
        }
    }

    suspend fun setUseBiometric(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_BIOMETRIC] = enabled
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setOnboardingComplete() {
        setOnboardingCompleted(true)
    }

    suspend fun setSortOrder(order: String) {
        context.dataStore.edit { preferences ->
            preferences[SORT_ORDER] = order
        }
    }

    suspend fun setPerformanceMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PERFORMANCE_MODE] = mode
        }
    }

    suspend fun saveRecentSearches(searches: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[RECENT_SEARCHES] = searches.toSet()
        }
    }
}
