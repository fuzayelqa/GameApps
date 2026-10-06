package com.example.data.models

enum class GameState {
    IDLE,
    PLAYING,
    PAUSED,
    GAME_OVER
}

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    fun isOpposite(other: Direction): Boolean {
        return (this == UP && other == DOWN) ||
                (this == DOWN && other == UP) ||
                (this == LEFT && other == RIGHT) ||
                (this == RIGHT && other == LEFT)
    }
}

enum class GameMode(val displayName: String, val isPremium: Boolean, val description: String) {
    CLASSIC("Classic Mode", false, "Standard snake gameplay with walls and progressive speed."),
    ENDLESS("Endless Mode", true, "Wrap around edges without dying from wall hits."),
    CHALLENGE("Challenge Mode", true, "Time pressure with high scoring golden food clusters."),
    OBSTACLE("Obstacle Mode", true, "Navigate around static and moving block obstacles."),
    TIME_ATTACK("Time Attack", true, "Score as much as possible in 90 intense seconds.")
}

enum class ControlType(val displayName: String) {
    SWIPE("Swipe Gestures"),
    BUTTONS("On-Screen D-Pad")
}

enum class Difficulty(val displayName: String, val speedMultiplier: Float) {
    EASY("Easy", 0.75f),
    NORMAL("Normal", 0.9f),
    HARD("Hard", 1.15f)
}

enum class ThemeType(val displayName: String, val isPremium: Boolean) {
    DARK("Dark Cyber", false),
    LIGHT("Clean Light", false),
    NEON("Neon Glow", true),
    SPACE("Deep Space", true),
    FOREST("Emerald Forest", true)
}

enum class SnakeSkin(val displayName: String, val isPremium: Boolean, val hexColor: Long) {
    CLASSIC("Classic Green", false, 0xFF00E676),
    NEON("Neon Cyan", true, 0xFF00E5FF),
    FIRE("Blazing Fire", true, 0xFFFF3D00),
    ICE("Frost Ice", true, 0xFF80D8FF),
    GALAXY("Galaxy Purple", true, 0xFFAA00FF),
    GOLD("Lustrous Gold", true, 0xFFFFD700)
}

enum class BoardStyle(val displayName: String) {
    GRID("Cyber Grid"),
    MINIMAL("Minimal Slate"),
    RETRO("Retro Arcade")
}

enum class FoodType(val points: Int, val isSpecial: Boolean) {
    NORMAL(10, false),
    GOLDEN(10, true),
    BONUS(10, true)
}
