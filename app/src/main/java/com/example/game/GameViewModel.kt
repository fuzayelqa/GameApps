package com.example.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.AuthManager
import com.example.data.database.AppDatabase
import com.example.data.database.GameRecordEntity
import com.example.data.firestore.FirestoreRepository
import com.example.data.models.BoardStyle
import com.example.data.models.ControlType
import com.example.data.models.Difficulty
import com.example.data.models.Direction
import com.example.data.models.GameMode
import com.example.data.models.GameState
import com.example.data.models.SnakeSkin
import com.example.data.models.ThemeType
import com.example.data.preferences.SettingsPreferences
import com.example.sound.SoundManager
import com.example.sound.VibrationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GameUiConfig(
    val controlType: ControlType = ControlType.SWIPE,
    val difficulty: Difficulty = Difficulty.NORMAL,
    val theme: ThemeType = ThemeType.DARK,
    val skin: SnakeSkin = SnakeSkin.CLASSIC,
    val boardStyle: BoardStyle = BoardStyle.GRID,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

class GameViewModel(
    application: Application,
    private val preferences: SettingsPreferences,
    private val soundManager: SoundManager,
    private val vibrationManager: VibrationManager,
    private val authManager: AuthManager,
    private val firestoreRepository: FirestoreRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        SettingsPreferences(application),
        SoundManager(application),
        VibrationManager(application),
        AuthManager(application),
        FirestoreRepository(application)
    )

    private val database = AppDatabase.getInstance(application)
    private val gameRecordDao = database.gameRecordDao()

    private val engine = SnakeEngine(gridWidth = 20, gridHeight = 24)

    private val _engineState = MutableStateFlow(engine.getState())
    val engineState: StateFlow<GameEngineState> = _engineState.asStateFlow()

    private val _selectedMode = MutableStateFlow(GameMode.CLASSIC)
    val selectedMode: StateFlow<GameMode> = _selectedMode.asStateFlow()

    private val _highScore = MutableStateFlow(0)
    val highScore: StateFlow<Int> = _highScore.asStateFlow()

    val uiConfig: StateFlow<GameUiConfig> = combine(
        preferences.controlTypeFlow,
        preferences.difficultyFlow,
        preferences.themeFlow,
        preferences.snakeSkinFlow,
        preferences.boardStyleFlow
    ) { control, diff, theme, skin, style ->
        GameUiConfig(
            controlType = control,
            difficulty = diff,
            theme = theme,
            skin = skin,
            boardStyle = style,
            soundEnabled = soundManager.isSoundEnabled,
            musicEnabled = soundManager.isMusicEnabled,
            vibrationEnabled = vibrationManager.isVibrationEnabled
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        GameUiConfig()
    )

    private var gameLoopJob: Job? = null
    private var timerJob: Job? = null

    init {
        // Observe local high score
        viewModelScope.launch {
            gameRecordDao.getHighScore().collect { score ->
                _highScore.value = score ?: 0
            }
        }

        // Configure engine callbacks
        engine.onEatFood = { type ->
            soundManager.playEatFood(isGolden = type.isSpecial)
            vibrationManager.vibrateEatFood()
        }

        engine.onLevelUp = { level ->
            soundManager.playLevelUp()
            vibrationManager.vibrateLevelUp()
        }

        engine.onGameOver = { isNewRecord ->
            if (isNewRecord) {
                soundManager.playNewRecord()
                vibrationManager.vibrateNewRecord()
            } else {
                soundManager.playGameOver()
                vibrationManager.vibrateGameOver()
            }
            onGameEnded()
        }

        // Sync settings with managers
        viewModelScope.launch {
            preferences.soundFlow.collect { soundManager.isSoundEnabled = it }
        }
        viewModelScope.launch {
            preferences.musicFlow.collect { soundManager.isMusicEnabled = it }
        }
        viewModelScope.launch {
            preferences.vibrationFlow.collect { vibrationManager.isVibrationEnabled = it }
        }
    }

    fun startNewGame(mode: GameMode = _selectedMode.value) {
        _selectedMode.value = mode
        viewModelScope.launch {
            val difficulty = preferences.difficultyFlow.first()
            val currentHigh = _highScore.value
            engine.setupNewGame(mode = mode, difficulty = difficulty, highScore = currentHigh)
            engine.startPlaying()
            _engineState.value = engine.getState()
            startGameLoops()
        }
    }

    private fun startGameLoops() {
        gameLoopJob?.cancel()
        timerJob?.cancel()

        // Snake movement ticker
        gameLoopJob = viewModelScope.launch {
            while (isActive && engine.getState().state == GameState.PLAYING) {
                val delayMs = engine.getTickDelayMillis()
                delay(delayMs)
                val updated = engine.tick()
                _engineState.value = updated
            }
        }

        // Seconds timer ticker
        timerJob = viewModelScope.launch {
            while (isActive && engine.getState().state == GameState.PLAYING) {
                delay(1000L)
                engine.updateDuration(1)
                _engineState.value = engine.getState()
            }
        }
    }

    fun onDirectionInput(direction: Direction) {
        engine.setDirection(direction)
    }

    fun pauseGame() {
        engine.pause()
        _engineState.value = engine.getState()
        gameLoopJob?.cancel()
        timerJob?.cancel()
    }

    fun resumeGame() {
        engine.resume()
        _engineState.value = engine.getState()
        startGameLoops()
    }

    fun restartGame() {
        startNewGame(_selectedMode.value)
    }

    fun reviveWithRewardedAd(): Boolean {
        val success = engine.continueWithReward()
        if (success) {
            _engineState.value = engine.getState()
            startGameLoops()
        }
        return success
    }

    private fun onGameEnded() {
        gameLoopJob?.cancel()
        timerJob?.cancel()

        val state = engine.getState()
        val record = GameRecordEntity(
            score = state.score,
            level = state.level,
            snakeLength = state.snake.size,
            foodCollected = state.foodCollected,
            gameDurationSeconds = state.gameDurationSeconds,
            gameMode = _selectedMode.value.name,
            date = System.currentTimeMillis()
        )

        viewModelScope.launch {
            // Save locally in Room
            gameRecordDao.insertRecord(record)

            // If user is authenticated, sync to Firestore
            if (!authManager.isGuest) {
                firestoreRepository.saveGameRecord(record)
                // Also update aggregated cloud stats
                val localHigh = _highScore.value
                val stats = com.example.data.models.GameStatistics(
                    userId = authManager.currentUser?.uid ?: "",
                    highScore = maxOf(localHigh, state.score),
                    highestLevel = state.level,
                    longestSnake = state.snake.size,
                    totalGames = 1,
                    foodCollected = state.foodCollected,
                    bestGameTimeSeconds = state.gameDurationSeconds
                )
                firestoreRepository.syncStatistics(stats)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        timerJob?.cancel()
        soundManager.release()
    }
}
