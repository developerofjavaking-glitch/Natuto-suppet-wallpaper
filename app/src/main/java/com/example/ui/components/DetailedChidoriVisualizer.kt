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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class ElectricSpark(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var radius: Float,
    var alpha: Float,
    var color: Color,
    var life: Float = 1f
)

data class LightningBranch(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

/**
 * Ultra detailed anime Chidori (千鳥 - Thousand Birds) with:
 * - Real-time procedural branching jagged lightning bolts
 * - High-voltage sizzling micro-arcs
 * - Pulsating violet-cyan plasma core
 * - High-speed electric spark particle bursts
 * - Direct lightning conduit streaming from power cable into Chidori
 */
@Composable
fun DetailedChidoriVisualizer(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 260.dp,
    batteryLevel: Int = 85,
    isCharging: Boolean = true,
    showBatteryText: Boolean = true
) {
    val sparks = remember { mutableStateListOf<ElectricSpark>() }
    var lightningBolts by remember { mutableStateOf<List<LightningBranch>>(emptyList()) }

    // High-frequency lightning & spark generation loop (runs at ~60fps)
    LaunchedEffect(isCharging) {
        val colors = listOf(
            Color(0xFFFFFFFF), // pure white lightning core
            Color(0xFFE0B0FF), // electric violet
            Color(0xFF80D8FF), // neon cyan
            Color(0xFFB388FF)  // deep chidori purple
        )

        while (true) {
            // 1. Generate new procedural lightning bolts radiating from center
            val newBolts = mutableListOf<LightningBranch>()
            val boltCount = if (isCharging) Random.nextInt(7, 12) else Random.nextInt(4, 7)

            for (i in 0 until boltCount) {
                val angle = Random.nextFloat() * 2f * PI.toFloat()
                val length = Random.nextFloat() * 110f + 60f
                val points = mutableListOf<Offset>()
                var cx = 0f
                var cy = 0f
                points.add(Offset(cx, cy))

                val segments = Random.nextInt(5, 9)
                val segLength = length / segments

                for (s in 1..segments) {
                    val progress = s.toFloat() / segments
                    val jitterAngle = angle + (Random.nextFloat() - 0.5f) * 1.2f
                    cx += segLength * cos(jitterAngle) + (Random.nextFloat() - 0.5f) * 18f
                    cy += segLength * sin(jitterAngle) + (Random.nextFloat() - 0.5f) * 18f
                    points.add(Offset(cx, cy))
                }

                newBolts.add(
                    LightningBranch(
                        points = points,
                        color = colors[Random.nextInt(colors.size)],
                        strokeWidth = if (Random.nextBoolean()) 2.8f else 1.8f
                    )
                )
            }
            lightningBolts = newBolts

            // 2. Spawn spark particles
            val maxSparks = if (isCharging) 45 else 20
            if (sparks.size < maxSparks) {
                for (k in 0..3) {
                    val sparkAngle = Random.nextFloat() * 2f * PI.toFloat()
                    val speed = Random.nextFloat() * 8f + 3f
                    sparks.add(
                        ElectricSpark(
                            x = 0f,
                            y = 0f,
                            vx = cos(sparkAngle) * speed,
                            vy = sin(sparkAngle) * speed,
                            radius = Random.nextFloat() * 3.5f + 1.5f,
                            alpha = 1f,
                            color = colors[Random.nextInt(colors.size)]
                        )
                    )
                }
            }

            // Update existing sparks
            val it = sparks.listIterator()
            while (it.hasNext()) {
                val s = it.next()
                s.x += s.vx
                s.y += s.vy
                s.vx *= 0.94f // air drag
                s.vy *= 0.94f
                s.life -= 0.045f
                if (s.life <= 0f) {
                    it.remove()
                }
            }

            delay(24) // fast lightning flicker rate
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "chidori_pulse")
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(280, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.width / 2f * 0.38f

            // 1. Violet Outer Electric Corona (Aura)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE0B0FF).copy(alpha = 0.85f),
                        Color(0xFF7C4DFF).copy(alpha = 0.45f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 2.2f * auraPulse
                ),
                radius = baseRadius * 2.2f * auraPulse
            )

            // 2. Bottom Conduit Lightning (Power cable surge straight into Chidori)
            if (isCharging) {
                drawBottomPowerConduit(center, size.height)
            }

            // 3. Procedural Branching Lightning Bolts
            lightningBolts.forEach { bolt ->
                val path = Path()
                bolt.points.forEachIndexed { index, pt ->
                    val absX = center.x + pt.x
                    val absY = center.y + pt.y
                    if (index == 0) path.moveTo(absX, absY) else path.lineTo(absX, absY)
                }
                // Glow line under
                drawPath(
                    path = path,
                    color = bolt.color.copy(alpha = 0.45f),
                    style = Stroke(width = bolt.strokeWidth * 2.5f, cap = StrokeCap.Round)
                )
                // Crisp bright core line
                drawPath(
                    path = path,
                    color = bolt.color,
                    style = Stroke(width = bolt.strokeWidth, cap = StrokeCap.Round)
                )
            }

            // 4. Electric Spark Particles flying outward
            sparks.forEach { s ->
                val sx = center.x + s.x
                val sy = center.y + s.y
                drawCircle(
                    color = s.color.copy(alpha = s.life),
                    radius = s.radius * s.life,
                    center = Offset(sx, sy)
                )
            }

            // 5. Plasma Core (High Voltage Ball)
            val coreR = baseRadius * corePulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFE0B0FF),
                        Color(0xFFB388FF),
                        Color.Transparent
                    ),
                    center = center,
                    radius = coreR * 1.3f
                ),
                radius = coreR * 1.3f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFF80D8FF)),
                    center = center,
                    radius = coreR * 0.75f
                ),
                radius = coreR * 0.75f
            )

            // 6. Jagged Micro-Arcs in core
            for (m in 0..4) {
                val a1 = Random.nextFloat() * 2f * PI.toFloat()
                val a2 = a1 + (Random.nextFloat() - 0.5f) * 1.5f
                val r1 = coreR * 0.5f
                val r2 = coreR * 0.9f
                drawLine(
                    color = Color.White,
                    start = Offset(center.x + r1 * cos(a1), center.y + r1 * sin(a1)),
                    end = Offset(center.x + r2 * cos(a2), center.y + r2 * sin(a2)),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Overlay: Center Battery Percentage Text
        if (showBatteryText) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$batteryLevel%",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

private fun DrawScope.drawBottomPowerConduit(center: Offset, screenHeight: Float) {
    val startY = screenHeight
    val endY = center.y + 40f
    val path = Path()
    path.moveTo(center.x, startY)
    var currY = startY
    var currX = center.x
    val step = 20f

    while (currY > endY) {
        currY -= step
        currX += (Random.nextFloat() - 0.5f) * 22f
        path.lineTo(currX, currY)
    }
    path.lineTo(center.x, endY)

    // Draw glowing violet stream
    drawPath(
        path = path,
        color = Color(0xFFB388FF).copy(alpha = 0.5f),
        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
    )
    drawPath(
        path = path,
        color = Color.White,
        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
    )
}
