package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class FlameEmber(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    var alpha: Float,
    var color: Color
)

/**
 * Ultra detailed Itachi Mangekyo Sharingan & Amaterasu Black Fire:
 * - Animated rotating Mangekyo Sharingan 3-pinwheel blades
 * - Glowing crimson blood iris that contracts and expands
 * - Swirling black Amaterasu flames and rising black embers
 */
@Composable
fun DetailedAmaterasuVisualizer(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 260.dp,
    batteryLevel: Int = 85,
    isCharging: Boolean = true,
    showBatteryText: Boolean = true
) {
    val embers = remember { mutableStateListOf<FlameEmber>() }

    LaunchedEffect(isCharging) {
        while (true) {
            if (embers.size < 35) {
                embers.add(
                    FlameEmber(
                        x = (Random.nextFloat() - 0.5f) * 160f,
                        y = 120f + Random.nextFloat() * 40f,
                        vx = (Random.nextFloat() - 0.5f) * 1.5f,
                        vy = -Random.nextFloat() * 3.5f - 1.5f,
                        size = Random.nextFloat() * 4f + 2f,
                        alpha = Random.nextFloat() * 0.7f + 0.3f,
                        color = if (Random.nextBoolean()) Color(0xFFFF1744) else Color(0xFF1E1E1E)
                    )
                )
            }

            val it = embers.listIterator()
            while (it.hasNext()) {
                val e = it.next()
                e.x += e.vx
                e.y += e.vy
                e.alpha -= 0.015f
                if (e.y < -150f || e.alpha <= 0f) {
                    it.remove()
                }
            }
            delay(16)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "mangekyo_rot")
    val rotEye by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCharging) 8000 else 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_mangekyo"
    )

    val pupilPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pupil_pulse"
    )

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val irisRadius = size.width / 2f * 0.42f

            // 1. Blood Moon Crimson Aura Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF1744).copy(alpha = 0.55f),
                        Color(0xFF880E4F).copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = irisRadius * 2.2f
                ),
                radius = irisRadius * 2.2f
            )

            // 2. Swirling Amaterasu Black Flames (Outer Ring)
            rotate(rotEye * 1.5f, center) {
                drawAmaterasuFlames(center, irisRadius * 1.4f)
            }

            // 3. Blood Red Iris Background
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF1744),
                        Color(0xFFD50000),
                        Color(0xFF500000)
                    ),
                    center = center,
                    radius = irisRadius
                ),
                radius = irisRadius
            )
            drawCircle(
                color = Color.Black,
                radius = irisRadius,
                style = Stroke(width = 3.dp.toPx())
            )

            // 4. Rotating Itachi 3-Pinwheel Mangekyo Blades
            rotate(rotEye, center) {
                drawMangekyoPinwheel(center, irisRadius * pupilPulse)
            }

            // 5. Center Pupil
            drawCircle(
                color = Color.Black,
                radius = irisRadius * 0.22f * pupilPulse,
                center = center
            )

            // 6. Floating Rising Embers
            embers.forEach { e ->
                drawCircle(
                    color = e.color.copy(alpha = e.alpha),
                    radius = e.size,
                    center = Offset(center.x + e.x, center.y + e.y)
                )
            }
        }

        if (showBatteryText) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$batteryLevel%",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

private fun DrawScope.drawAmaterasuFlames(center: Offset, radius: Float) {
    for (i in 0 until 8) {
        val angleDeg = i * 45f
        rotate(angleDeg, center) {
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.6f)
                cubicTo(
                    center.x - 18f, center.y - radius * 0.9f,
                    center.x + 18f, center.y - radius * 1.15f,
                    center.x, center.y - radius * 1.35f
                )
                cubicTo(
                    center.x + 24f, center.y - radius * 1.1f,
                    center.x + 12f, center.y - radius * 0.8f,
                    center.x, center.y - radius * 0.6f
                )
                close()
            }
            // Black flame core with crimson border
            drawPath(path = path, color = Color(0xFF101010))
            drawPath(
                path = path,
                color = Color(0xFFFF1744),
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

private fun DrawScope.drawMangekyoPinwheel(center: Offset, radius: Float) {
    // 3 curved triangular pinwheel blades connected at center
    for (i in 0 until 3) {
        val angleDeg = i * 120f
        rotate(angleDeg, center) {
            val path = Path().apply {
                moveTo(center.x, center.y)
                // Curved blade reaching to rim
                cubicTo(
                    center.x - radius * 0.25f, center.y - radius * 0.4f,
                    center.x - radius * 0.1f, center.y - radius * 0.85f,
                    center.x, center.y - radius * 0.92f
                )
                cubicTo(
                    center.x + radius * 0.35f, center.y - radius * 0.65f,
                    center.x + radius * 0.25f, center.y - radius * 0.25f,
                    center.x, center.y
                )
                close()
            }
            drawPath(path = path, color = Color.Black)
        }
    }
}
