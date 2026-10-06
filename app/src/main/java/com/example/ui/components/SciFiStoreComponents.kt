package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// Visual palette matching the screenshot
val SciFiNeonCyan = Color(0xFF00F0FF)
val SciFiNeonCyanGlow = Color(0x6600F0FF)
val SciFiGold = Color(0xFFFFD700)
val SciFiDarkBg = Color(0xF0051320)
val SciFiGridLineColor = Color(0x2800E5FF)

/**
 * Draws the cosmic nebula background with deep space void,
 * glowing purple & cyan gas clouds, stellar dust, stars, and dark planet curvature.
 */
@Composable
fun CosmicStoreBackground(
    modifier: Modifier = Modifier
) {
    // Deterministic random stars
    val stars = remember {
        val rand = Random(42)
        List(90) {
            StarData(
                xRatio = rand.nextFloat(),
                yRatio = rand.nextFloat(),
                radius = rand.nextFloat() * 1.8f + 0.6f,
                alpha = rand.nextFloat() * 0.7f + 0.3f,
                hasSpikes = rand.nextFloat() < 0.12f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "starglow")
    val starGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starglow_val"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Deep space base background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF02040B),
                    Color(0xFF070B1E),
                    Color(0xFF030715)
                )
            ),
            size = size
        )

        // 2. Cosmic Nebula Clouds (Purple / Magenta upper-right)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x777B2CBF),
                    Color(0x445A189A),
                    Color(0x003C096C)
                ),
                center = Offset(w * 0.82f, h * 0.32f),
                radius = w * 0.75f
            ),
            center = Offset(w * 0.82f, h * 0.32f),
            radius = w * 0.75f
        )

        // 3. Central Brilliant Cyan/Blue Starburst Cluster
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x8800B4D8),
                    Color(0x550077B6),
                    Color(0x22023E8A),
                    Color(0x0003045E)
                ),
                center = Offset(w * 0.5f, h * 0.42f),
                radius = w * 0.85f
            ),
            center = Offset(w * 0.5f, h * 0.42f),
            radius = w * 0.85f
        )

        // 4. Soft Purple/Pink Mid Nebula
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x55B5179E),
                    Color(0x227209B7),
                    Color(0x00000000)
                ),
                center = Offset(w * 0.2f, h * 0.55f),
                radius = w * 0.55f
            ),
            center = Offset(w * 0.2f, h * 0.55f),
            radius = w * 0.55f
        )

        // 5. Stars
        stars.forEach { star ->
            val center = Offset(star.xRatio * w, star.yRatio * h)
            val effectiveAlpha = (star.alpha * starGlow).coerceIn(0.1f, 1.0f)
            drawCircle(
                color = Color.White.copy(alpha = effectiveAlpha),
                radius = star.radius,
                center = center
            )
            if (star.hasSpikes) {
                // 4-point subtle twinkle
                val spikeLen = star.radius * 3.5f
                drawLine(
                    color = SciFiNeonCyan.copy(alpha = effectiveAlpha * 0.75f),
                    start = Offset(center.x - spikeLen, center.y),
                    end = Offset(center.x + spikeLen, center.y),
                    strokeWidth = 1f
                )
                drawLine(
                    color = SciFiNeonCyan.copy(alpha = effectiveAlpha * 0.75f),
                    start = Offset(center.x, center.y - spikeLen),
                    end = Offset(center.x, center.y + spikeLen),
                    strokeWidth = 1f
                )
            }
        }

        // 6. Planet Horizon at the bottom
        val planetCenter = Offset(w * 0.65f, h * 1.15f)
        val planetRadius = w * 0.75f
        // Outer atmospheric glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x0000B4D8),
                    Color(0x5500F0FF),
                    Color(0x00000000)
                ),
                center = planetCenter,
                radius = planetRadius + 30f
            ),
            center = planetCenter,
            radius = planetRadius + 30f
        )
        // Planet body
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF040A18),
                    Color(0xFF02040B)
                ),
                center = planetCenter,
                radius = planetRadius
            ),
            center = planetCenter,
            radius = planetRadius
        )
    }
}

private data class StarData(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val alpha: Float,
    val hasSpikes: Boolean
)

/**
 * Builds the Path for the futuristic sci-fi tech card frame with
 * rounded corners and indented middle chamfer notches.
 */
fun buildSciFiTechPath(width: Float, height: Float, cornerRadius: Float = 26f, indentW: Float = 70f, indentH: Float = 10f, chamfer: Float = 12f): Path {
    val path = Path()
    val r = cornerRadius.coerceAtMost(height / 2f)
    val midX = width / 2f
    val halfIndent = (indentW / 2f).coerceAtMost(midX - r - chamfer)

    // Start on left edge at vertical center
    path.moveTo(0f, height / 2f)

    // Left edge to top-left corner
    path.lineTo(0f, r)
    path.quadraticTo(0f, 0f, r, 0f)

    // Top edge towards center notch
    path.lineTo(midX - halfIndent - chamfer, 0f)
    path.lineTo(midX - halfIndent, indentH)
    path.lineTo(midX + halfIndent, indentH)
    path.lineTo(midX + halfIndent + chamfer, 0f)

    // Top edge to top-right corner
    path.lineTo(width - r, 0f)
    path.quadraticTo(width, 0f, width, r)

    // Right edge to bottom-right corner
    path.lineTo(width, height - r)
    path.quadraticTo(width, height, width - r, height)

    // Bottom edge towards center notch
    path.lineTo(midX + halfIndent + chamfer, height)
    path.lineTo(midX + halfIndent, height - indentH)
    path.lineTo(midX - halfIndent, height - indentH)
    path.lineTo(midX - halfIndent - chamfer, height)

    // Bottom edge to bottom-left corner
    path.lineTo(r, height)
    path.quadraticTo(0f, height, 0f, height - r)

    // Left edge back to start
    path.close()
    return path
}

/**
 * Futuristic sci-fi tech frame component with neon cyan glow,
 * cyber-grid background pattern, and chamfered bracket profile.
 */
@Composable
fun SciFiTechPanel(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = SciFiNeonCyan,
    glowColor: Color = SciFiNeonCyanGlow,
    backgroundColor: Color = SciFiDarkBg,
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = borderColor),
            onClick = onClick
        )
    } else {
        Modifier
    }

    val tagModifier = if (testTag != null) Modifier.testTag(testTag) else Modifier

    Box(
        modifier = modifier
            .then(tagModifier)
            .then(clickableModifier),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val path = buildSciFiTechPath(
                width = w,
                height = h,
                cornerRadius = 24f,
                indentW = w * 0.38f,
                indentH = 8f,
                chamfer = 12f
            )

            // 1. Fill background inside path
            drawPath(
                path = path,
                color = backgroundColor,
                style = Fill
            )

            // 2. Cyber-grid pattern clipped inside
            val gridSpacing = 16f
            var gx = gridSpacing
            while (gx < w) {
                drawLine(
                    color = SciFiGridLineColor,
                    start = Offset(gx, 0f),
                    end = Offset(gx, h),
                    strokeWidth = 1f
                )
                gx += gridSpacing
            }
            var gy = gridSpacing
            while (gy < h) {
                drawLine(
                    color = SciFiGridLineColor,
                    start = Offset(0f, gy),
                    end = Offset(w, gy),
                    strokeWidth = 1f
                )
                gy += gridSpacing
            }

            // 3. Outer Neon Glow Stroke
            drawPath(
                path = path,
                color = glowColor,
                style = Stroke(
                    width = 6f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 4. Sharp Neon Border Stroke
            drawPath(
                path = path,
                color = borderColor,
                style = Stroke(
                    width = 2.4f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 5. Corner Accent Highlights (Cyan dots/bars)
            val accentLength = 12f
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = Offset(w * 0.12f, 2f),
                end = Offset(w * 0.12f + accentLength, 2f),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = Offset(w * 0.88f - accentLength, 2f),
                end = Offset(w * 0.88f, 2f),
                strokeWidth = 2f
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

/**
 * Standard Sci-Fi Credit Package Card used in the 2-column grid.
 */
@Composable
fun SciFiPackageCard(
    creditsText: String,
    priceText: String,
    isSpecial: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null
) {
    SciFiTechPanel(
        modifier = modifier
            .height(72.dp),
        onClick = onClick,
        borderColor = if (isSpecial) SciFiNeonCyan else SciFiNeonCyan,
        glowColor = if (isSpecial) Color(0x9900F0FF) else SciFiNeonCyanGlow,
        backgroundColor = if (isSpecial) Color(0xF506192A) else SciFiDarkBg,
        testTag = testTag
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = creditsText,
                color = if (isSpecial) SciFiGold else Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = priceText,
                color = if (isSpecial) SciFiNeonCyan else SciFiGold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
