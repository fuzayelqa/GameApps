package com.example.ui.game

import android.app.Activity
import androidx.activity.compose.BackHandler
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdManager
import com.example.billing.BillingManager
import com.example.data.models.BoardStyle
import com.example.data.models.ControlType
import com.example.data.models.Direction
import com.example.data.models.FoodType
import com.example.data.models.GameMode
import com.example.data.models.GameState
import com.example.data.models.SnakeSkin
import com.example.game.GameViewModel
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NeonGreen
import kotlin.math.abs

@Composable
fun GameScreen(
    gameViewModel: GameViewModel,
    billingManager: BillingManager,
    adManager: AdManager,
    onNavigateHome: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val engineState by gameViewModel.engineState.collectAsState()
    val uiConfig by gameViewModel.uiConfig.collectAsState()
    val selectedMode by gameViewModel.selectedMode.collectAsState()
    val highScore by gameViewModel.highScore.collectAsState()
    val isPremium by billingManager.isPremium.collectAsState()
    val preferences = remember { com.example.data.preferences.SettingsPreferences(context) }
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val creditBalance by preferences.creditBalanceFlow.collectAsState(initial = 250)
    val revivePasses by preferences.revivePassesFlow.collectAsState(initial = 1)

    BackHandler {
        if (engineState.state == GameState.PLAYING) {
            gameViewModel.pauseGame()
        } else {
            onNavigateHome()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top HUD Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score & Level
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SCORE: ",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${engineState.score}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "LEVEL ${engineState.level} • ${selectedMode.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Time Attack timer or High Score
                if (selectedMode == GameMode.TIME_ATTACK) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (engineState.timeAttackRemainingSeconds <= 15) MaterialTheme.colorScheme.error.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "${engineState.timeAttackRemainingSeconds}s",
                            color = if (engineState.timeAttackRemainingSeconds <= 15) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$highScore",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Pause Button
                IconButton(
                    onClick = {
                        if (engineState.state == GameState.PLAYING) {
                            gameViewModel.pauseGame()
                        } else if (engineState.state == GameState.PAUSED) {
                            gameViewModel.resumeGame()
                        }
                    },
                    modifier = Modifier.testTag("game_pause_button")
                ) {
                    Icon(
                        imageVector = if (engineState.state == GameState.PAUSED) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause Game",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Snake Game Board Canvas with Gesture Drag Handler
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .aspectRatio(20f / 24f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (uiConfig.boardStyle) {
                            BoardStyle.GRID -> Color(0xFF0F1722)
                            BoardStyle.MINIMAL -> Color(0xFF0A0F14)
                            BoardStyle.RETRO -> Color(0xFF061A12)
                        }
                    )
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .pointerInput(Unit) {
                        var dragOffset = Offset.Zero
                        detectDragGestures(
                            onDragStart = { dragOffset = Offset.Zero },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount
                            },
                            onDragEnd = {
                                val dx = dragOffset.x
                                val dy = dragOffset.y
                                if (abs(dx) > abs(dy)) {
                                    if (dx > 30) gameViewModel.onDirectionInput(Direction.RIGHT)
                                    else if (dx < -30) gameViewModel.onDirectionInput(Direction.LEFT)
                                } else {
                                    if (dy > 30) gameViewModel.onDirectionInput(Direction.DOWN)
                                    else if (dy < -30) gameViewModel.onDirectionInput(Direction.UP)
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                val skinColor = Color(uiConfig.skin.hexColor)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cols = 20
                    val rows = 24
                    val cellWidth = size.width / cols
                    val cellHeight = size.height / rows

                    // 1. Draw Board Grid Lines
                    if (uiConfig.boardStyle == BoardStyle.GRID || uiConfig.boardStyle == BoardStyle.RETRO) {
                        val gridColor = if (uiConfig.boardStyle == BoardStyle.RETRO) {
                            Color(0x1400E676)
                        } else {
                            Color(0x12FFFFFF)
                        }
                        for (c in 1 until cols) {
                            drawLine(
                                color = gridColor,
                                start = Offset(c * cellWidth, 0f),
                                end = Offset(c * cellWidth, size.height),
                                strokeWidth = 1f
                            )
                        }
                        for (r in 1 until rows) {
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, r * cellHeight),
                                end = Offset(size.width, r * cellHeight),
                                strokeWidth = 1f
                            )
                        }
                    }

                    // 2. Draw Obstacles
                    for (obs in engineState.obstacles) {
                        drawRoundRect(
                            color = Color(0xFF64748B),
                            topLeft = Offset(obs.x * cellWidth + 2f, obs.y * cellHeight + 2f),
                            size = Size(cellWidth - 4f, cellHeight - 4f),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }

                    // 3. Draw Food
                    engineState.food?.let { foodItem ->
                        val fx = foodItem.position.x * cellWidth
                        val fy = foodItem.position.y * cellHeight
                        val foodColor = when (foodItem.type) {
                            FoodType.NORMAL -> Color(0xFFFF3B30) // Red Apple
                            FoodType.GOLDEN -> GoldPrimary
                            FoodType.BONUS -> Color(0xFF00E5FF) // Bonus Cyan
                        }

                        // Outer Glow
                        drawCircle(
                            color = foodColor.copy(alpha = 0.35f),
                            radius = (cellWidth / 1.5f),
                            center = Offset(fx + cellWidth / 2f, fy + cellHeight / 2f)
                        )

                        // Main food dot
                        drawCircle(
                            color = foodColor,
                            radius = (cellWidth / 2.3f),
                            center = Offset(fx + cellWidth / 2f, fy + cellHeight / 2f)
                        )

                        // Sparkle / Shine
                        drawCircle(
                            color = Color.White.copy(alpha = 0.8f),
                            radius = (cellWidth / 6f),
                            center = Offset(fx + cellWidth * 0.38f, fy + cellHeight * 0.38f)
                        )
                    }

                    // 4. Draw Snake Body
                    val snakeSegments = engineState.snake
                    for (i in snakeSegments.indices) {
                        val pt = snakeSegments[i]
                        val sx = pt.x * cellWidth
                        val sy = pt.y * cellHeight

                        if (i == 0) {
                            // Head
                            drawRoundRect(
                                color = skinColor,
                                topLeft = Offset(sx + 1.5f, sy + 1.5f),
                                size = Size(cellWidth - 3f, cellHeight - 3f),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                            // Eyes
                            val eyeRadius = cellWidth / 8f
                            val eyeOffset1: Offset
                            val eyeOffset2: Offset
                            when (engineState.direction) {
                                Direction.UP -> {
                                    eyeOffset1 = Offset(sx + cellWidth * 0.28f, sy + cellHeight * 0.3f)
                                    eyeOffset2 = Offset(sx + cellWidth * 0.72f, sy + cellHeight * 0.3f)
                                }
                                Direction.DOWN -> {
                                    eyeOffset1 = Offset(sx + cellWidth * 0.28f, sy + cellHeight * 0.7f)
                                    eyeOffset2 = Offset(sx + cellWidth * 0.72f, sy + cellHeight * 0.7f)
                                }
                                Direction.LEFT -> {
                                    eyeOffset1 = Offset(sx + cellWidth * 0.3f, sy + cellHeight * 0.28f)
                                    eyeOffset2 = Offset(sx + cellWidth * 0.3f, sy + cellHeight * 0.72f)
                                }
                                Direction.RIGHT -> {
                                    eyeOffset1 = Offset(sx + cellWidth * 0.7f, sy + cellHeight * 0.28f)
                                    eyeOffset2 = Offset(sx + cellWidth * 0.7f, sy + cellHeight * 0.72f)
                                }
                            }
                            drawCircle(Color.Black, eyeRadius, eyeOffset1)
                            drawCircle(Color.Black, eyeRadius, eyeOffset2)
                            drawCircle(Color.White, eyeRadius / 2f, eyeOffset1)
                            drawCircle(Color.White, eyeRadius / 2f, eyeOffset2)
                        } else {
                            // Body segment (slightly darker towards tail)
                            val fadeFactor = (1f - (i.toFloat() / (snakeSegments.size + 8))).coerceIn(0.6f, 1f)
                            val segmentColor = Color(
                                red = skinColor.red * fadeFactor,
                                green = skinColor.green * fadeFactor,
                                blue = skinColor.blue * fadeFactor,
                                alpha = 1f
                            )
                            drawRoundRect(
                                color = segmentColor,
                                topLeft = Offset(sx + 2f, sy + 2f),
                                size = Size(cellWidth - 4f, cellHeight - 4f),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                        }
                    }
                }
            }

            // On-Screen D-Pad Buttons (Visible if controlType == BUTTONS)
            if (uiConfig.controlType == ControlType.BUTTONS) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FilledIconButton(
                        onClick = { gameViewModel.onDirectionInput(Direction.UP) },
                        modifier = Modifier
                            .size(54.dp)
                            .testTag("dpad_up_button"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(40.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledIconButton(
                            onClick = { gameViewModel.onDirectionInput(Direction.LEFT) },
                            modifier = Modifier
                                .size(54.dp)
                                .testTag("dpad_left_button"),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        }
                        FilledIconButton(
                            onClick = { gameViewModel.onDirectionInput(Direction.DOWN) },
                            modifier = Modifier
                                .size(54.dp)
                                .testTag("dpad_down_button"),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        }
                        FilledIconButton(
                            onClick = { gameViewModel.onDirectionInput(Direction.RIGHT) },
                            modifier = Modifier
                                .size(54.dp)
                                .testTag("dpad_right_button"),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            } else {
                Text(
                    text = "Swipe in any direction to control snake",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }

        // Pause Modal Overlay
        if (engineState.state == GameState.PAUSED) {
            AlertDialog(
                onDismissRequest = { gameViewModel.resumeGame() },
                title = {
                    Text(
                        text = "GAME PAUSED",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GamingButton(
                            text = "RESUME",
                            onClick = { gameViewModel.resumeGame() },
                            icon = Icons.Default.PlayArrow,
                            testTag = "pause_resume_button"
                        )
                        GamingButton(
                            text = "RESTART",
                            onClick = { gameViewModel.restartGame() },
                            icon = Icons.Default.Refresh,
                            isPrimary = false,
                            testTag = "pause_restart_button"
                        )
                        GamingButton(
                            text = "HOME",
                            onClick = onNavigateHome,
                            icon = Icons.Default.Close,
                            isPrimary = false,
                            testTag = "pause_home_button"
                        )
                    }
                },
                confirmButton = {}
            )
        }

        // Game Over Modal Dialog
        if (engineState.state == GameState.GAME_OVER) {
            AlertDialog(
                onDismissRequest = { /* Force explicit button click */ },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        if (engineState.isNewRecord) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "NEW RECORD!",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                        Text(
                            text = "GAME OVER",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Final Score: ${engineState.score}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Level: ${engineState.level} • Food: ${engineState.foodCollected}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Revive / Continue options (if not yet revived)
                        if (!engineState.hasRevived) {
                            if (revivePasses > 0) {
                                GamingButton(
                                    text = "USE REVIVE SHIELD ($revivePasses LEFT)",
                                    onClick = {
                                        scope.launch {
                                            if (preferences.useReviveShield()) {
                                                gameViewModel.reviveWithRewardedAd()
                                            }
                                        }
                                    },
                                    icon = Icons.Default.Shield,
                                    isPrimary = true,
                                    testTag = "use_shield_revive_button"
                                )
                            } else if (creditBalance >= 50) {
                                GamingButton(
                                    text = "REVIVE (50 CREDITS)",
                                    onClick = {
                                        scope.launch {
                                            if (preferences.spendCredits(50)) {
                                                gameViewModel.reviveWithRewardedAd()
                                            }
                                        }
                                    },
                                    icon = Icons.Default.MonetizationOn,
                                    isGold = true,
                                    testTag = "credit_revive_button"
                                )
                            }

                            GamingButton(
                                text = if (isPremium) "FREE REVIVE (PREMIUM)" else "CONTINUE (WATCH AD)",
                                onClick = {
                                    if (activity != null) {
                                        adManager.showRewarded(
                                            activity = activity,
                                            isPremium = isPremium,
                                            onUserEarnedReward = {
                                                gameViewModel.reviveWithRewardedAd()
                                            },
                                            onDismiss = {}
                                        )
                                    } else {
                                        gameViewModel.reviveWithRewardedAd()
                                    }
                                },
                                icon = Icons.Default.Videocam,
                                isGold = isPremium,
                                isPrimary = false,
                                testTag = "revive_button"
                            )
                        }

                        GamingButton(
                            text = "PLAY AGAIN",
                            onClick = {
                                if (activity != null && !isPremium) {
                                    adManager.showInterstitial(activity, isPremium = false) {
                                        gameViewModel.restartGame()
                                    }
                                } else {
                                    gameViewModel.restartGame()
                                }
                            },
                            icon = Icons.Default.Refresh,
                            testTag = "game_over_play_again_button"
                        )

                        GamingButton(
                            text = "HOME",
                            onClick = {
                                if (activity != null && !isPremium) {
                                    adManager.showInterstitial(activity, isPremium = false) {
                                        onNavigateHome()
                                    }
                                } else {
                                    onNavigateHome()
                                }
                            },
                            icon = Icons.Default.Close,
                            isPrimary = false,
                            testTag = "game_over_home_button"
                        )
                    }
                },
                confirmButton = {}
            )
        }
    }
}
