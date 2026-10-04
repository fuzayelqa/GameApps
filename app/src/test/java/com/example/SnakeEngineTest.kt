package com.example

import com.example.data.models.Difficulty
import com.example.data.models.Direction
import com.example.data.models.FoodType
import com.example.data.models.GameMode
import com.example.data.models.GameState
import com.example.game.GridPoint
import com.example.game.SnakeEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SnakeEngineTest {

    private lateinit var engine: SnakeEngine

    @Before
    fun setup() {
        engine = SnakeEngine(gridWidth = 20, gridHeight = 24)
        engine.setupNewGame(mode = GameMode.CLASSIC, difficulty = Difficulty.NORMAL, highScore = 100)
    }

    @Test
    fun `initial state is correct`() {
        val state = engine.getState()
        assertEquals(GameState.IDLE, state.state)
        assertEquals(3, state.snake.size)
        assertEquals(0, state.score)
        assertEquals(1, state.level)
        assertEquals(Direction.RIGHT, state.direction)
    }

    @Test
    fun `cannot reverse direction immediately`() {
        engine.startPlaying()
        // Snake is moving RIGHT, setting LEFT should be ignored
        engine.setDirection(Direction.LEFT)
        val state = engine.tick()
        assertNotEquals(Direction.LEFT, state.direction)
        assertEquals(Direction.RIGHT, state.direction)
    }

    @Test
    fun `snake moves forward on tick`() {
        engine.startPlaying()
        val initialHead = engine.getState().snake.first()
        val updated = engine.tick()
        val newHead = updated.snake.first()
        assertEquals(initialHead.x + 1, newHead.x)
        assertEquals(initialHead.y, newHead.y)
    }

    @Test
    fun `pause and resume functions correctly`() {
        engine.startPlaying()
        engine.pause()
        assertEquals(GameState.PAUSED, engine.getState().state)
        engine.resume()
        assertEquals(GameState.PLAYING, engine.getState().state)
    }
}
