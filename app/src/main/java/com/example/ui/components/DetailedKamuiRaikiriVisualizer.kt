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

data class LightningSlice(
    val p1: Offset,
    val p2: Offset,
    val color: Color
)

/**
 * Ultra detailed Kakashi Kamui Dimensional Distortion & Raikiri Lightning Cutter
 */
@Composable
fun DetailedKamuiRaikiriVisualizer(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 260.dp,
    batteryLevel: Int = 85,
    isCharging: Boolean = true,
    showBatteryText: Boolean = true
) {
    val lightningSlices = remember { mutableStateListOf<LightningSlice>() }

    LaunchedEffect(isCharging) {
        val colors = listOf(Color(0xFF00E5FF), Color.White, Color(0xFF80DEEA))
        while (true) {
            lightningSlices.clear()
            val count = if (isCharging) Random.nextInt(5, 9) else 4
            for (i in 0 until count) {
                val a1 = Random.nextFloat() * 2f * PI.toFloat()
                val a2 = a1 + (Random.nextFloat() - 0.5f) * 1.5f
                val r1 = 30f + Random.nextFloat() * 40f
                val r2 = 90f + Random.nextFloat() * 70f
                lightningSlices.add(
                    LightningSlice(
                        p1 = Offset(r1 * cos(a1), r1 * sin(a1)),
                        p2 = Offset(r2 * cos(a2), r2 * sin(a2)),
                        color = colors[Random.nextInt(colors.size)]
                    )
                )
            }
            delay(35)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "kamui_trans")
    val kamuiWarp by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCharging) 1800 else 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "kamui_warp"
    )

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f * 0.45f

            // 1. Space-Time Warping Swirl Lines (Kamui Black Hole Effect)
            rotate(kamuiWarp, center) {
                for (i in 0 until 12) {
                    val angle = (i * 30f) * (PI / 180f).toFloat()
                    val path = Path()
                    path.moveTo(center.x, center.y)
                    val steps = 16
                    for (s in 1..steps) {
                        val t = s.toFloat() / steps
                        val r = radius * 1.6f * t
                        val rot = t * 3.2f
                        val x = center.x + r * cos(angle + rot)
                        val y = center.y + r * sin(angle + rot)
                        path.lineTo(x, y)
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF00E5FF).copy(alpha = 0.5f),
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // 2. Central Dimensional Void Hole
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF0B0E14), Color(0xFF006064), Color.Transparent),
                    center = center,
                    radius = radius * 0.85f
                ),
                radius = radius * 0.85f
            )

            // 3. Raikiri Electric Blades (Slicing outward)
            lightningSlices.forEach { slice ->
                drawLine(
                    color = slice.color,
                    start = Offset(center.x + slice.p1.x, center.y + slice.p1.y),
                    end = Offset(center.x + slice.p2.x, center.y + slice.p2.y),
                    strokeWidth = 2.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 4. Center Glowing Sharingan Eye Accent
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFFF1744), Color.Transparent),
                    center = center,
                    radius = 16.dp.toPx()
                ),
                radius = 16.dp.toPx()
            )
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
