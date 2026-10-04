package com.example.data.models

data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val isPremium: Boolean = false,
    val isEmailVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class GameStatistics(
    val userId: String = "",
    val highScore: Int = 0,
    val highestLevel: Int = 1,
    val longestSnake: Int = 3,
    val totalGames: Int = 0,
    val foodCollected: Int = 0,
    val bestGameTimeSeconds: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

data class UserSettingsSync(
    val userId: String = "",
    val controlType: String = ControlType.SWIPE.name,
    val difficulty: String = Difficulty.NORMAL.name,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val theme: String = ThemeType.DARK.name,
    val snakeSkin: String = SnakeSkin.CLASSIC.name,
    val updatedAt: Long = System.currentTimeMillis()
)
