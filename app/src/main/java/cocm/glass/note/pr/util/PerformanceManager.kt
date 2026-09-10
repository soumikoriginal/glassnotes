package cocm.glass.note.pr.util

import android.content.Context
import android.os.Build
import cocm.glass.note.pr.data.local.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class PerformanceManager(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) {

    enum class GlassQuality {
        HIGH,
        BALANCED,
        BATTERY_SAVER
    }

    fun getGlassQuality(): GlassQuality {
        // Get saved performance mode from preferences
        val savedMode = runBlocking {
            preferencesManager.performanceMode.first()
        }
        return when (savedMode) {
            PreferencesManager.PERFORMANCE_HIGH -> GlassQuality.HIGH
            PreferencesManager.PERFORMANCE_BATTERY -> GlassQuality.BATTERY_SAVER
            else -> GlassQuality.BALANCED
        }
    }

    suspend fun setGlassQuality(quality: GlassQuality) {
        val mode = when (quality) {
            GlassQuality.HIGH -> PreferencesManager.PERFORMANCE_HIGH
            GlassQuality.BALANCED -> PreferencesManager.PERFORMANCE_BALANCED
            GlassQuality.BATTERY_SAVER -> PreferencesManager.PERFORMANCE_BATTERY
        }
        preferencesManager.setPerformanceMode(mode)
    }

    fun shouldUseBlur(): Boolean {
        return when (getGlassQuality()) {
            GlassQuality.HIGH -> true
            GlassQuality.BALANCED -> true
            GlassQuality.BATTERY_SAVER -> false
        }
    }

    fun getBlurRadius(): Float {
        return when (getGlassQuality()) {
            GlassQuality.HIGH -> 25f
            GlassQuality.BALANCED -> 15f
            GlassQuality.BATTERY_SAVER -> 0f
        }
    }

    fun shouldAnimateTransitions(): Boolean {
        return when (getGlassQuality()) {
            GlassQuality.HIGH -> true
            GlassQuality.BALANCED -> true
            GlassQuality.BATTERY_SAVER -> false
        }
    }

    private fun isLowEndDevice(): Boolean {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory() / (1024 * 1024) // MB

        return maxMemory < 512 ||
               Build.VERSION.SDK_INT < Build.VERSION_CODES.N ||
               Runtime.getRuntime().availableProcessors() < 4
    }

    private fun isMidRangeDevice(): Boolean {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory() / (1024 * 1024) // MB

        return maxMemory in 512..2048
    }
}
