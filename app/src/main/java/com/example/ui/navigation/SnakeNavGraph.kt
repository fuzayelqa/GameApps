package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ads.AdManager
import com.example.auth.AuthManager
import com.example.auth.AuthState
import com.example.billing.BillingManager
import com.example.data.database.AppDatabase
import com.example.data.firestore.FirestoreRepository
import com.example.data.models.GameStatistics
import com.example.data.preferences.SettingsPreferences
import com.example.game.GameViewModel
import com.example.ui.auth.EmailVerificationScreen
import com.example.ui.auth.ForgotPasswordScreen
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.game.GameScreen
import com.example.ui.home.HomeScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.premium.PremiumScreen
import com.example.ui.records.RecordsScreen
import com.example.ui.settings.SettingsScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object SnakeDestinations {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val GAME = "game"
    const val PREMIUM = "premium"
    const val RECORDS = "records"
    const val SETTINGS = "settings"
    const val CREDIT_STORE = "credit_store"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val EMAIL_VERIFICATION = "email_verification"
}

@Composable
fun SnakeNavGraph(
    navController: NavHostController,
    gameViewModel: GameViewModel,
    authManager: AuthManager,
    billingManager: BillingManager,
    adManager: AdManager,
    preferences: SettingsPreferences,
    database: AppDatabase,
    firestoreRepository: FirestoreRepository,
    startDestination: String
) {
    val scope = rememberCoroutineScope()
    val authState by authManager.authState.collectAsState()
    val gameDao = database.gameRecordDao()

    val highScore by gameDao.getHighScore().collectAsState(initial = 0)
    val highestLevel by gameDao.getHighestLevel().collectAsState(initial = 1)
    val longestSnake by gameDao.getLongestSnake().collectAsState(initial = 3)
    val totalGames by gameDao.getTotalGames().collectAsState(initial = 0)
    val totalFood by gameDao.getTotalFoodCollected().collectAsState(initial = 0)
    val bestTime by gameDao.getBestGameTime().collectAsState(initial = 0)
    val recentRecords by gameDao.getRecentRecords().collectAsState(initial = emptyList())

    // Trigger Cloud Sync on successful sign-in
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            val user = (authState as AuthState.Authenticated).user
            val localStats = GameStatistics(
                userId = user.uid,
                highScore = highScore ?: 0,
                highestLevel = highestLevel ?: 1,
                longestSnake = longestSnake ?: 3,
                totalGames = totalGames,
                foodCollected = totalFood ?: 0,
                bestGameTimeSeconds = bestTime ?: 0
            )
            // Sync stats with cloud
            firestoreRepository.syncStatistics(localStats)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(SnakeDestinations.ONBOARDING) {
            OnboardingScreen(
                onFinishOnboarding = {
                    scope.launch {
                        preferences.setOnboardingCompleted(true)
                        navController.navigate(SnakeDestinations.HOME) {
                            popUpTo(SnakeDestinations.ONBOARDING) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(SnakeDestinations.HOME) {
            HomeScreen(
                highScore = highScore ?: 0,
                authManager = authManager,
                billingManager = billingManager,
                preferences = preferences,
                onPlayClick = {
                    gameViewModel.startNewGame()
                    navController.navigate(SnakeDestinations.GAME)
                },
                onStoreClick = { navController.navigate(SnakeDestinations.CREDIT_STORE) },
                onPremiumClick = { navController.navigate(SnakeDestinations.PREMIUM) },
                onRecordsClick = { navController.navigate(SnakeDestinations.RECORDS) },
                onSettingsClick = { navController.navigate(SnakeDestinations.SETTINGS) },
                onLoginClick = { navController.navigate(SnakeDestinations.LOGIN) }
            )
        }

        composable(SnakeDestinations.CREDIT_STORE) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val soundManager = androidx.compose.runtime.remember { com.example.sound.SoundManager(context) }
            val vibrationManager = androidx.compose.runtime.remember { com.example.sound.VibrationManager(context) }
            com.example.ui.store.CreditStoreScreen(
                preferences = preferences,
                soundManager = soundManager,
                vibrationManager = vibrationManager,
                onBack = { navController.popBackStack() }
            )
        }

        composable(SnakeDestinations.GAME) {
            GameScreen(
                gameViewModel = gameViewModel,
                billingManager = billingManager,
                adManager = adManager,
                onNavigateHome = {
                    navController.navigate(SnakeDestinations.HOME) {
                        popUpTo(SnakeDestinations.GAME) { inclusive = true }
                    }
                }
            )
        }

        composable(SnakeDestinations.PREMIUM) {
            PremiumScreen(
                billingManager = billingManager,
                onBack = { navController.popBackStack() }
            )
        }

        composable(SnakeDestinations.RECORDS) {
            RecordsScreen(
                gameRecords = recentRecords,
                highScore = highScore ?: 0,
                highestLevel = highestLevel ?: 1,
                longestSnake = longestSnake ?: 3,
                totalGames = totalGames,
                totalFood = totalFood ?: 0,
                bestTimeSeconds = bestTime ?: 0,
                authManager = authManager,
                billingManager = billingManager,
                onBack = { navController.popBackStack() }
            )
        }

        composable(SnakeDestinations.SETTINGS) {
            SettingsScreen(
                preferences = preferences,
                authManager = authManager,
                billingManager = billingManager,
                onNavigateToLogin = { navController.navigate(SnakeDestinations.LOGIN) },
                onNavigateToRegister = { navController.navigate(SnakeDestinations.REGISTER) },
                onNavigateToPremium = { navController.navigate(SnakeDestinations.PREMIUM) },
                onNavigateToStore = { navController.navigate(SnakeDestinations.CREDIT_STORE) },
                onResetRecords = {
                    scope.launch { gameDao.clearAllRecords() }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SnakeDestinations.LOGIN) {
            LoginScreen(
                authManager = authManager,
                onNavigateToRegister = { navController.navigate(SnakeDestinations.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(SnakeDestinations.FORGOT_PASSWORD) },
                onLoginSuccess = {
                    navController.navigate(SnakeDestinations.HOME) {
                        popUpTo(SnakeDestinations.LOGIN) { inclusive = true }
                    }
                },
                onContinueAsGuest = {
                    authManager.continueAsGuest()
                    navController.navigate(SnakeDestinations.HOME) {
                        popUpTo(SnakeDestinations.LOGIN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SnakeDestinations.REGISTER) {
            RegisterScreen(
                authManager = authManager,
                onNavigateToLogin = { navController.navigate(SnakeDestinations.LOGIN) },
                onRegisterSuccess = { email ->
                    navController.navigate(SnakeDestinations.EMAIL_VERIFICATION) {
                        popUpTo(SnakeDestinations.REGISTER) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(SnakeDestinations.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                authManager = authManager,
                onBack = { navController.popBackStack() }
            )
        }

        composable(SnakeDestinations.EMAIL_VERIFICATION) {
            val userEmail = authManager.currentUser?.email ?: "your email"
            EmailVerificationScreen(
                userEmail = userEmail,
                authManager = authManager,
                onVerified = {
                    navController.navigate(SnakeDestinations.HOME) {
                        popUpTo(SnakeDestinations.EMAIL_VERIFICATION) { inclusive = true }
                    }
                },
                onBackToHome = {
                    navController.navigate(SnakeDestinations.HOME) {
                        popUpTo(SnakeDestinations.EMAIL_VERIFICATION) { inclusive = true }
                    }
                }
            )
        }
    }
}
