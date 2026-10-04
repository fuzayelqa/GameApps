package com.example.game

import com.example.data.models.Difficulty
import com.example.data.models.Direction
import com.example.data.models.FoodType
import com.example.data.models.GameMode
import com.example.data.models.GameState
import kotlin.random.Random

data class GridPoint(val x: Int, val y: Int)

data class FoodItem(
    val position: GridPoint,
    val type: FoodType,
    val expiresAtTime: Long = 0L // 0 means does not expire
)

data class GameEngineState(
    val state: GameState = GameState.IDLE,
    val snake: List<GridPoint> = emptyList(),
    val direction: Direction = Direction.RIGHT,
    val food: FoodItem? = null,
    val obstacles: List<GridPoint> = emptyList(),
    val score: Int = 0,
    val level: Int = 1,
    val foodCollected: Int = 0,
    val gameDurationSeconds: Int = 0,
    val timeAttackRemainingSeconds: Int = 90,
    val isNewRecord: Boolean = false,
    val hasRevived: Boolean = false
)

class SnakeEngine(
    val gridWidth: Int = 20,
    val gridHeight: Int = 26
) {
    private var currentState = GameEngineState()
    private var nextDirection: Direction = Direction.RIGHT
    private var currentDifficulty: Difficulty = Difficulty.NORMAL
    private var currentGameMode: GameMode = GameMode.CLASSIC
    private var currentHighScore: Int = 0

    // Callback triggers for sounds / events
    var onEatFood: ((FoodType) -> Unit)? = null
    var onLevelUp: ((Int) -> Unit)? = null
    var onGameOver: ((Boolean) -> Unit)? = null

    fun getState(): GameEngineState = currentState

    fun setupNewGame(
        mode: GameMode,
        difficulty: Difficulty,
        highScore: Int
    ) {
        currentGameMode = mode
        currentDifficulty = difficulty
        currentHighScore = highScore
        nextDirection = Direction.RIGHT

        val startX = gridWidth / 3
        val startY = gridHeight / 2
        val initialSnake = listOf(
            GridPoint(startX, startY),
            GridPoint(startX - 1, startY),
            GridPoint(startX - 2, startY)
        )

        val obstacles = if (mode == GameMode.OBSTACLE) {
            generateObstacles(initialSnake)
        } else {
            emptyList()
        }

        currentState = GameEngineState(
            state = GameState.IDLE,
            snake = initialSnake,
            direction = Direction.RIGHT,
            obstacles = obstacles,
            score = 0,
            level = 1,
            foodCollected = 0,
            gameDurationSeconds = 0,
            timeAttackRemainingSeconds = if (mode == GameMode.TIME_ATTACK) 90 else 0,
            isNewRecord = false,
            hasRevived = false
        )

        // Spawn initial food
        spawnFood()
    }

    fun startPlaying() {
        if (currentState.state == GameState.IDLE || currentState.state == GameState.PAUSED) {
            currentState = currentState.copy(state = GameState.PLAYING)
        }
    }

    fun pause() {
        if (currentState.state == GameState.PLAYING) {
            currentState = currentState.copy(state = GameState.PAUSED)
        }
    }

    fun resume() {
        if (currentState.state == GameState.PAUSED) {
            currentState = currentState.copy(state = GameState.PLAYING)
        }
    }

    fun setDirection(newDirection: Direction) {
        // Prevent 180-degree turn
        if (!currentState.direction.isOpposite(newDirection) && !nextDirection.isOpposite(newDirection)) {
            nextDirection = newDirection
        }
    }

    fun tick(): GameEngineState {
        if (currentState.state != GameState.PLAYING) {
            return currentState
        }

        val currentSnake = currentState.snake
        val dir = nextDirection
        val head = currentSnake.first()

        // Calculate next head position
        var nextX = head.x
        var nextY = head.y

        when (dir) {
            Direction.UP -> nextY -= 1
            Direction.DOWN -> nextY += 1
            Direction.LEFT -> nextX -= 1
            Direction.RIGHT -> nextX += 1
        }

        // Handle wall collision or wrapping based on game mode
        if (currentGameMode == GameMode.ENDLESS) {
            // Screen wrap
            if (nextX < 0) nextX = gridWidth - 1
            else if (nextX >= gridWidth) nextX = 0
            if (nextY < 0) nextY = gridHeight - 1
            else if (nextY >= gridHeight) nextY = 0
        } else {
            // Wall collision check
            if (nextX < 0 || nextX >= gridWidth || nextY < 0 || nextY >= gridHeight) {
                triggerGameOver()
                return currentState
            }
        }

        val nextHead = GridPoint(nextX, nextY)

        // Check self collision (ignoring tail tip if snake doesn't grow this tick)
        val bodyToCheck = if (currentState.food?.position == nextHead) {
            currentSnake
        } else {
            currentSnake.dropLast(1)
        }

        if (bodyToCheck.contains(nextHead)) {
            triggerGameOver()
            return currentState
        }

        // Check obstacle collision
        if (currentState.obstacles.contains(nextHead)) {
            triggerGameOver()
            return currentState
        }

        // Check food collision
        val eatenFood = currentState.food?.takeIf { it.position == nextHead }
        val newSnake = mutableListOf(nextHead)

        var newScore = currentState.score
        var newFoodCollected = currentState.foodCollected
        var newLevel = currentState.level
        var isNewRecord = currentState.isNewRecord

        if (eatenFood != null) {
            // Snake grows: keep all current segments
            newSnake.addAll(currentSnake)
            newScore += eatenFood.type.points
            newFoodCollected += 1

            // Level progression: level up every 100 points
            val calculatedLevel = 1 + (newScore / 100)
            if (calculatedLevel > newLevel) {
                newLevel = calculatedLevel
                newScore += 100 // Level completion bonus
                onLevelUp?.invoke(newLevel)

                // In level 4+ for Classic mode, add obstacles if desired
                if (currentGameMode == GameMode.CLASSIC && newLevel >= 4 && currentState.obstacles.isEmpty()) {
                    val generated = generateObstacles(newSnake)
                    currentState = currentState.copy(obstacles = generated)
                }
            }

            if (newScore > currentHighScore && currentHighScore > 0 && !isNewRecord) {
                isNewRecord = true
            }

            onEatFood?.invoke(eatenFood.type)

            currentState = currentState.copy(
                snake = newSnake,
                direction = dir,
                score = newScore,
                level = newLevel,
                foodCollected = newFoodCollected,
                isNewRecord = isNewRecord
            )
            spawnFood()
        } else {
            // Normal move: drop tail
            newSnake.addAll(currentSnake.dropLast(1))
            currentState = currentState.copy(
                snake = newSnake,
                direction = dir
            )
        }

        return currentState
    }

    fun updateDuration(secondsElapsed: Int) {
        if (currentState.state == GameState.PLAYING) {
            val totalSeconds = currentState.gameDurationSeconds + secondsElapsed
            var remaining = currentState.timeAttackRemainingSeconds

            if (currentGameMode == GameMode.TIME_ATTACK) {
                remaining = maxOf(0, remaining - secondsElapsed)
                if (remaining <= 0) {
                    triggerGameOver()
                    return
                }
            }

            currentState = currentState.copy(
                gameDurationSeconds = totalSeconds,
                timeAttackRemainingSeconds = remaining
            )
        }
    }

    fun continueWithReward(): Boolean {
        if (currentState.state != GameState.GAME_OVER || currentState.hasRevived) {
            return false
        }

        // Revive: trim snake if long and position head safely away from walls
        val currentSnake = currentState.snake
        val trimmed = if (currentSnake.size > 5) currentSnake.take(currentSnake.size - 2) else currentSnake
        val safeHead = GridPoint(
            x = trimmed.first().x.coerceIn(1, gridWidth - 2),
            y = trimmed.first().y.coerceIn(1, gridHeight - 2)
        )
        val revivedSnake = listOf(safeHead) + trimmed.drop(1)

        currentState = currentState.copy(
            state = GameState.PLAYING,
            snake = revivedSnake,
            hasRevived = true
        )
        return true
    }

    private fun spawnFood() {
        val occupied = currentState.snake.toSet() + currentState.obstacles.toSet()
        val availablePoints = mutableListOf<GridPoint>()

        for (x in 0 until gridWidth) {
            for (y in 0 until gridHeight) {
                val pt = GridPoint(x, y)
                if (!occupied.contains(pt)) {
                    availablePoints.add(pt)
                }
            }
        }

        if (availablePoints.isEmpty()) {
            triggerGameOver()
            return
        }

        val chosenPoint = availablePoints[Random.nextInt(availablePoints.size)]

        // Food type selection: 70% Normal, 20% Golden, 10% Bonus
        val roll = Random.nextInt(100)
        val foodType = when {
            roll < 70 -> FoodType.NORMAL
            roll < 90 -> FoodType.GOLDEN
            else -> FoodType.BONUS
        }

        currentState = currentState.copy(
            food = FoodItem(position = chosenPoint, type = foodType)
        )
    }

    private fun generateObstacles(snake: List<GridPoint>): List<GridPoint> {
        val obstacles = mutableListOf<GridPoint>()
        val snakeSet = snake.toSet()

        // Place 3 to 5 obstacle blocks
        val obstacleBlocks = 4
        for (i in 0 until obstacleBlocks) {
            val ox = Random.nextInt(2, gridWidth - 3)
            val oy = Random.nextInt(2, gridHeight - 3)
            val block = listOf(
                GridPoint(ox, oy),
                GridPoint(ox + 1, oy),
                GridPoint(ox, oy + 1)
            )
            if (block.none { snakeSet.contains(it) }) {
                obstacles.addAll(block)
            }
        }
        return obstacles
    }

    private fun triggerGameOver() {
        val isNewRecord = currentState.score > currentHighScore && currentState.score > 0
        currentState = currentState.copy(
            state = GameState.GAME_OVER,
            isNewRecord = isNewRecord
        )
        onGameOver?.invoke(isNewRecord)
    }

    fun getTickDelayMillis(): Long {
        // Base delay decreases with level
        val baseSpeed = when (currentState.level) {
            1 -> 220L
            2 -> 190L
            3 -> 160L
            4 -> 135L
            5 -> 115L
            6 -> 100L
            else -> maxOf(70L, 100L - ((currentState.level - 6) * 5L))
        }

        // Multiply by difficulty (EASY is slower / higher delay, HARD is faster / lower delay)
        val adjusted = (baseSpeed / currentDifficulty.speedMultiplier).toLong()
        return adjusted.coerceIn(50L, 300L)
    }
}
