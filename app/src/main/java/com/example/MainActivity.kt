package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ads.AdManager
import com.example.auth.AuthManager
import com.example.billing.BillingManager
import com.example.data.database.AppDatabase
import com.example.data.firestore.FirestoreRepository
import com.example.data.models.ThemeType
import com.example.data.preferences.SettingsPreferences
import com.example.game.GameViewModel
import com.example.ui.navigation.SnakeDestinations
import com.example.ui.navigation.SnakeNavGraph
import com.example.ui.theme.SnakeGameTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    companion object {
        init {
            try {
                android.system.Os.setenv("LIBGL_ALWAYS_SOFTWARE", "1", true)
                android.system.Os.setenv("MESA_LOADER_DRIVER_OVERRIDE", "swrast", true)
                android.system.Os.setenv("MESA_DEBUG", "0", true)
                android.system.Os.setenv("EGL_LOG_LEVEL", "fatal", true)
            } catch (_: Throwable) {
                // Handled gracefully
            }
        }
    }

    private lateinit var preferences: SettingsPreferences
    private lateinit var authManager: AuthManager
    private lateinit var billingManager: BillingManager
    private lateinit var adManager: AdManager
    private lateinit var database: AppDatabase
    private lateinit var firestoreRepository: FirestoreRepository

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            val jsCacheDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/js")
            if (!jsCacheDir.exists()) jsCacheDir.mkdirs()
            val wasmCacheDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm")
            if (!wasmCacheDir.exists()) wasmCacheDir.mkdirs()
        } catch (_: Exception) {
            // Ignored if file creation fails
        }

        preferences = SettingsPreferences(applicationContext)
        authManager = AuthManager(applicationContext)
        billingManager = BillingManager(applicationContext, preferences)
        adManager = AdManager(applicationContext)
        database = AppDatabase.getInstance(applicationContext)
        firestoreRepository = FirestoreRepository(applicationContext)

        // Check if onboarding completed
        val isOnboardingCompleted = runBlocking {
            preferences.onboardingCompletedFlow.first()
        }

        val startDestination = if (isOnboardingCompleted) {
            SnakeDestinations.HOME
        } else {
            SnakeDestinations.ONBOARDING
        }

        setContent {
            val currentTheme by preferences.themeFlow.collectAsState(initial = ThemeType.DARK)

            SnakeGameTheme(themeType = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    SnakeNavGraph(
                        navController = navController,
                        gameViewModel = gameViewModel,
                        authManager = authManager,
                        billingManager = billingManager,
                        adManager = adManager,
                        preferences = preferences,
                        database = database,
                        firestoreRepository = firestoreRepository,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
