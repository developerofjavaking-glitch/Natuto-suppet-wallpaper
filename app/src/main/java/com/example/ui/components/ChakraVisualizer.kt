package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NarutoCharacter
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class LiveParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var radius: Float,
    var alpha: Float,
    var color: Color,
    val maxLife: Float = 1f,
    var life: Float = 1f
)

@Composable
fun ChakraParticleOverlay(
    modifier: Modifier = Modifier,
    character: NarutoCharacter,
    isCharging: Boolean = true,
    intensity: Float = 1.0f
) {
    val particles = remember { mutableStateListOf<LiveParticle>() }

    // Particle update tick
    LaunchedEffect(character.id, isCharging, intensity) {
        val maxParticles = (35 * intensity).toInt().coerceIn(15, 70)
        while (true) {
            // Spawn new particles
            if (particles.size < maxParticles) {
                particles.add(
                    LiveParticle(
                        x = Random.nextFloat(),
                        y = 1.05f,
                        vx = (Random.nextFloat() - 0.5f) * 0.004f,
                        vy = -(Random.nextFloat() * 0.008f + 0.004f) * (if (isCharging) 1.5f else 1.0f),
                        radius = Random.nextFloat() * 6f + 3f,
                        alpha = Random.nextFloat() * 0.7f + 0.3f,
                        color = if (Random.nextBoolean()) character.primaryColor else character.secondaryColor
                    )
                )
            }

            // Update existing
            val iterator = particles.listIterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                p.x += p.vx
                p.y += p.vy
                p.life -= 0.012f
                if (p.y < -0.05f || p.life <= 0f) {
                    iterator.remove()
                }
            }
            delay(16)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "chakra_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Draw glowing ambient chakra aura at bottom/center
        val radialBrush = Brush.radialGradient(
            colors = listOf(
                character.glowColor.copy(alpha = 0.35f * pulse),
                character.primaryColor.copy(alpha = 0.15f * pulse),
                Color.Transparent
            ),
            center = Offset(w / 2f, h * 0.65f),
            radius = w * 0.7f
        )
        drawRect(brush = radialBrush)

        // 2. Draw rising particles
        particles.forEach { p ->
            val px = p.x * w
            val py = p.y * h
            val pAlpha = (p.alpha * (p.life / p.maxLife)).coerceIn(0f, 1f)
            drawCircle(
                color = p.color.copy(alpha = pAlpha),
                radius = p.radius,
                center = Offset(px, py)
            )
        }

        // 3. Draw character-specific energy accents
        if (character.id.contains("sasuke") || character.id.contains("kakashi")) {
            // Lightning micro-arcs
            drawLightningArcs(character.primaryColor, w, h)
        }
    }
}

private fun DrawScope.drawLightningArcs(color: Color, w: Float, h: Float) {
    if (Random.nextFloat() > 0.65f) {
        val startX = w * (0.3f + Random.nextFloat() * 0.4f)
        val startY = h * (0.4f + Random.nextFloat() * 0.3f)
        val path = Path()
        path.moveTo(startX, startY)
        var cx = startX
        var cy = startY
        for (i in 0..4) {
            cx += (Random.nextFloat() - 0.5f) * 60f
            cy += Random.nextFloat() * 40f + 10f
            path.lineTo(cx, cy)
        }
        drawPath(
            path = path,
            color = color.copy(alpha = 0.8f),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

/**
 * Animated Eight Trigrams Sealing Style Core (八卦封印)
 */
@Composable
fun RotatingSealingCore(
    modifier: Modifier = Modifier,
    character: NarutoCharacter,
    batteryLevel: Int,
    isCharging: Boolean,
    sizeDp: Dp = 230.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "seal_rot")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCharging) 7000 else 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "seal_spin"
    )

    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCharging) 5000 else 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_spin"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "liquid_wave"
    )

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        // Canvas Sealing Formula & Wave Fill
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width / 2f - 12.dp.toPx()
            val innerRadius = outerRadius - 16.dp.toPx()

            // 1. Outer Glow Ring
            drawCircle(
                color = character.glowColor.copy(alpha = if (isCharging) 0.35f else 0.15f),
                radius = outerRadius + 6.dp.toPx(),
                style = Stroke(width = 12.dp.toPx())
            )

            // 2. Rotating Rune Ring (Trigram symbols)
            rotate(rotationAngle, center) {
                drawCircle(
                    color = character.primaryColor,
                    radius = outerRadius,
                    style = Stroke(width = 3.dp.toPx())
                )
                val step = 360f / 12f
                for (i in 0 until 12) {
                    val angleRad = (i * step) * (PI / 180f).toFloat()
                    val tickStart = Offset(
                        center.x + (outerRadius - 6.dp.toPx()) * cos(angleRad),
                        center.y + (outerRadius - 6.dp.toPx()) * sin(angleRad)
                    )
                    val tickEnd = Offset(
                        center.x + outerRadius * cos(angleRad),
                        center.y + outerRadius * sin(angleRad)
                    )
                    drawLine(
                        color = character.secondaryColor,
                        start = tickStart,
                        end = tickEnd,
                        strokeWidth = 2.5.dp.toPx()
                    )
                }
            }

            // 3. Counter-Rotating Inner Seal Ring
            rotate(counterRotation, center) {
                drawCircle(
                    color = character.secondaryColor.copy(alpha = 0.8f),
                    radius = innerRadius,
                    style = Stroke(width = 2.dp.toPx())
                )
                // 8 Trigram notches
                for (i in 0 until 8) {
                    val angleRad = (i * 45f) * (PI / 180f).toFloat()
                    val p1 = Offset(
                        center.x + (innerRadius - 8.dp.toPx()) * cos(angleRad),
                        center.y + (innerRadius - 8.dp.toPx()) * sin(angleRad)
                    )
                    drawCircle(
                        color = character.primaryColor,
                        radius = 2.5.dp.toPx(),
                        center = p1
                    )
                }
            }

            // 4. Sloshing Chakra Liquid Wave representing Battery %
            val liquidPath = Path()
            val fillPercent = (batteryLevel / 100f).coerceIn(0.05f, 1f)
            val fillHeight = (innerRadius * 2) * fillPercent
            val waterLevelY = center.y + innerRadius - fillHeight

            // Draw wave within circular clip
            val waveAmplitude = if (isCharging) 7.dp.toPx() else 3.dp.toPx()
            liquidPath.moveTo(center.x - innerRadius, center.y + innerRadius)

            var x = center.x - innerRadius
            while (x <= center.x + innerRadius) {
                val relX = x - (center.x - innerRadius)
                val y = waterLevelY + waveAmplitude * sin(wavePhase + (relX / innerRadius) * 2f)
                liquidPath.lineTo(x, y)
                x += 6f
            }
            liquidPath.lineTo(center.x + innerRadius, center.y + innerRadius)
            liquidPath.close()

            // Fill wave
            drawPath(
                path = liquidPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        character.secondaryColor.copy(alpha = 0.65f),
                        character.primaryColor.copy(alpha = 0.85f)
                    ),
                    startY = waterLevelY - waveAmplitude,
                    endY = center.y + innerRadius
                )
            )

            // Inner dark circular rim
            drawCircle(
                color = character.primaryColor.copy(alpha = 0.9f),
                radius = innerRadius,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Center Japanese Kanji Seal & Battery Text Overlay
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = character.sealKanji,
                fontSize = 80.sp,
                fontWeight = FontWeight.Black,
                color = character.primaryColor.copy(alpha = 0.25f)
            )
            Text(
                text = "$batteryLevel%",
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}
