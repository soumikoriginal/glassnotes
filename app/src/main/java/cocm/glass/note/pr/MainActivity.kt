package cocm.glass.note.pr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import cocm.glass.note.pr.navigation.GlassNotesNavigation
import cocm.glass.note.pr.presentation.lock.LockScreen
import cocm.glass.note.pr.presentation.onboarding.OnboardingScreen
import cocm.glass.note.pr.ui.theme.GlassNotesTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    // Tracks whether the app-lock gate is currently shown.
    private var isLocked by mutableStateOf(true)
    private var isAppInBackground = false

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op either way */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()

        val app = application as GlassNotesApplication
        val container = app.container

        setContent {
            val themeMode by container.preferencesManager.themeMode.collectAsState(initial = "SYSTEM")
            val isDarkTheme = when (themeMode.uppercase()) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            val isOnboardingComplete by container.preferencesManager.isOnboardingComplete.collectAsState(initial = null)
            val appLockEnabled by container.preferencesManager.appLockEnabled.collectAsState(initial = false)
            val useBiometric by container.preferencesManager.useBiometric.collectAsState(initial = true)

            // If App Lock is off, make sure we never show the lock gate.
            LaunchedEffect(appLockEnabled) {
                if (!appLockEnabled) isLocked = false
            }

            GlassNotesTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        isOnboardingComplete == null -> {
                            // Initial reading from DataStore in progress
                        }
                        isOnboardingComplete == false -> {
                            OnboardingScreen(
                                onFinish = {
                                    lifecycleScope.launch {
                                        container.preferencesManager.setOnboardingComplete()
                                    }
                                }
                            )
                        }
                        appLockEnabled && isLocked -> {
                            LockScreen(
                                biometricAvailable = useBiometric && container.biometricHelper.isBiometricAvailable(),
                                onAuthenticateBiometric = {
                                    container.biometricHelper.authenticate(
                                        activity = this@MainActivity,
                                        onSuccess = { isLocked = false },
                                        onError = { /* keep locked; user can retry via Unlock button */ },
                                        onFailed = { /* keep locked; user can retry */ }
                                    )
                                }
                            )
                        }
                        else -> {
                            GlassNotesNavigation(
                                container = container,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        isAppInBackground = true
    }

    override fun onResume() {
        super.onResume()
        if (isAppInBackground) {
            isAppInBackground = false
            val app = application as GlassNotesApplication
            lifecycleScope.launch {
                val enabled = app.container.preferencesManager.appLockEnabled.first()
                if (enabled) {
                    isLocked = true
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
