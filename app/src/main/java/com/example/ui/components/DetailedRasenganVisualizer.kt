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
import kotlin.math.sqrt
import kotlin.random.Random

data class SuctionParticle(
    var angle: Float,
    var distance: Float,
    var speed: Float,
    var size: Float,
    var alpha: Float,
    var color: Color
)

/**
 * Ultra detailed anime Rasengan with:
 * - 4 rotating Rasenshuriken wind blades
 * - Multi-layer high-RPM swirling logarithmic spirals
 * - Condensed radiant white-blue chakra core
 * - 3D orbiting Truth-Seeking Orbs
 * - Inward chakra suction particle flow
 * - Expanding shockwave rings
 */
@Composable
fun DetailedRasenganVisualizer(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 260.dp,
    batteryLevel: Int = 85,
    isCharging: Boolean = true,
    showBatteryText: Boolean = true
) {
    val suctionParticles = remember { mutableStateListOf<SuctionParticle>() }

    // Suction particle physics (streaming inward into the Rasengan core)
    LaunchedEffect(isCharging) {
        val maxP = if (isCharging) 45 else 25
        while (true) {
            if (suctionParticles.size < maxP) {
                suctionParticles.add(
                    SuctionParticle(
                        angle = Random.nextFloat() * 2f * PI.toFloat(),
                        distance = 180f + Random.nextFloat() * 120f,
                        speed = 3.5f + Random.nextFloat() * 4.5f,
                        size = Random.nextFloat() * 4f + 2f,
                        alpha = Random.nextFloat() * 0.7f + 0.3f,
                        color = if (Random.nextBoolean()) Color(0xFF00E5FF) else Color(0xFFFFD54F)
                    )
                )
            }

            val it = suctionParticles.listIterator()
            while (it.hasNext()) {
                val p = it.next()
                p.distance -= p.speed
                p.angle += 0.045f // spiral trajectory inward
                if (p.distance <= 20f) {
                    it.remove()
                }
            }
            delay(16)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rasengan_rotations")

    // Layer 1: High speed core rotation
    val rotCore by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCharging) 900 else 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_core"
    )

    // Layer 2: Counter-spinning mid vortex
    val rotMid by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCharging) 1400 else 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_mid"
    )

    // Layer 3: Rasenshuriken 4 wind blades
    val rotWindShuriken by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCharging) 2000 else 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_shuriken"
    )

    // Shockwave pulse
    val shockwaveScale by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shockwave"
    )

    // Core breathing glow
    val coreGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_glow"
    )

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.width / 2f * 0.42f

            // 1. Expanding Shockwave Distortion Ring
            val swRadius = baseRadius * 2.2f * shockwaveScale
            val swAlpha = ((1f - shockwaveScale) * 0.6f).coerceIn(0f, 1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00E5FF).copy(alpha = swAlpha), Color.Transparent),
                    center = center,
                    radius = swRadius
                ),
                radius = swRadius
            )
            drawCircle(
                color = Color(0xFF00F5D4).copy(alpha = swAlpha * 0.8f),
                radius = swRadius,
                style = Stroke(width = 2.dp.toPx())
            )

            // 2. 4-Blade Rasenshuriken Wind Blades (Wind Scythes)
            rotate(rotWindShuriken, center) {
                drawRasenshurikenBlades(center, baseRadius * 2.1f, isCharging)
            }

            // 3. Suction particles streaming into core
            suctionParticles.forEach { p ->
                val px = center.x + p.distance * cos(p.angle)
                val py = center.y + p.distance * sin(p.angle)
                drawCircle(
                    color = p.color.copy(alpha = p.alpha),
                    radius = p.size,
                    center = Offset(px, py)
                )
            }

            // 4. Outer Rotating Spiral Vortex Filaments (Clockwise)
            rotate(rotCore, center) {
                drawSpiralFilaments(
                    center = center,
                    radius = baseRadius * 1.55f,
                    strands = 14,
                    color = Color(0xFF00E5FF),
                    strokeWidth = 2.5f
                )
            }

            // 5. Counter-Rotating Dense Vortex Filaments (Counter-Clockwise)
            rotate(rotMid, center) {
                drawSpiralFilaments(
                    center = center,
                    radius = baseRadius * 1.25f,
                    strands = 18,
                    color = Color(0xFF80D8FF),
                    strokeWidth = 3f,
                    reverse = true
                )
            }

            // 6. Condensed Radiant Chakra Core (Pulsing Sphere)
            val coreR = baseRadius * coreGlow
            // Outer cyan aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFF00E5FF),
                        Color(0xFF0091EA),
                        Color.Transparent
                    ),
                    center = center,
                    radius = coreR * 1.4f
                ),
                radius = coreR * 1.4f
            )

            // Dense inner glowing core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFF84FFFF), Color(0xFF00B0FF)),
                    center = center,
                    radius = coreR * 0.85f
                ),
                radius = coreR * 0.85f
            )

            // 7. Truth-Seeking Orbs / Orbiting Nodes (求道玉)
            for (i in 0 until 4) {
                val orbAngle = (rotMid * 0.6f + i * 90f) * (PI / 180f).toFloat()
                val orbDist = baseRadius * 1.75f
                val orbCenter = Offset(
                    center.x + orbDist * cos(orbAngle),
                    center.y + (orbDist * 0.45f) * sin(orbAngle) // 3D ellipse perspective
                )
                // Black core with cyan corona
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = 0.5f),
                    radius = 8.dp.toPx(),
                    center = orbCenter
                )
                drawCircle(
                    color = Color(0xFF0B0E14),
                    radius = 5.dp.toPx(),
                    center = orbCenter
                )
            }
        }

        // Overlay: Bold Center Battery Text / Seal Kanji
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

private fun DrawScope.drawRasenshurikenBlades(center: Offset, bladeRadius: Float, isCharging: Boolean) {
    val bladeColor = Color(0xFF80D8FF).copy(alpha = if (isCharging) 0.55f else 0.35f)
    val edgeColor = Color(0xFFFFFFFF).copy(alpha = if (isCharging) 0.85f else 0.55f)

    for (blade in 0 until 4) {
        val angleDeg = blade * 90f
        rotate(angleDeg, center) {
            val path = Path().apply {
                moveTo(center.x, center.y)
                // Curved scythe blade
                cubicTo(
                    center.x + bladeRadius * 0.3f, center.y - bladeRadius * 0.2f,
                    center.x + bladeRadius * 0.8f, center.y - bladeRadius * 0.5f,
                    center.x + bladeRadius, center.y - bladeRadius * 0.1f
                )
                cubicTo(
                    center.x + bladeRadius * 0.7f, center.y + bladeRadius * 0.1f,
                    center.x + bladeRadius * 0.3f, center.y + bladeRadius * 0.15f,
                    center.x, center.y
                )
                close()
            }
            drawPath(path = path, color = bladeColor)
            drawPath(
                path = path,
                color = edgeColor,
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

private fun DrawScope.drawSpiralFilaments(
    center: Offset,
    radius: Float,
    strands: Int,
    color: Color,
    strokeWidth: Float,
    reverse: Boolean = false
) {
    val angleStep = (2f * PI / strands).toFloat()
    for (i in 0 until strands) {
        val startAngle = i * angleStep
        val path = Path()
        val steps = 24
        for (s in 0..steps) {
            val t = s.toFloat() / steps
            val r = radius * t
            val spin = if (reverse) -t * 2.8f else t * 2.8f
            val theta = startAngle + spin
            val x = center.x + r * cos(theta)
            val y = center.y + r * sin(theta)
            if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = color.copy(alpha = (0.25f + 0.55f * (i % 2)).coerceIn(0f, 1f)),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}
