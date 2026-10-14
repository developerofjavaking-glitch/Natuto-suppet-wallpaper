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

data class DimensionalFragment(
    var angle: Float,
    var dist: Float,
    var speed: Float,
    var size: Float,
    var alpha: Float,
    val color: Color
)

data class KamuiLightningArc(
    val points: List<Offset>,
    val color: Color,
    val width: Float
)

/**
 * Ultimate Kakashi Kamui Raikiri & Kamui Shuriken Charging Effect (神威雷切・神威手裏剣):
 * - Kamui Space-Time Black Hole Dimensional Vortex with gravitational suction
 * - 4 Rotating Giant Kamui Shuriken blades with electric cyan energy edges
 * - High-voltage Raikiri piercing lightning blade core
 * - Spatial fracture tears (dimensional glass cracks)
 * - Space debris and lightning arcs sucked into the Kamui void
 */
@Composable
fun DetailedKamuiRaikiriVisualizer(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 270.dp,
    batteryLevel: Int = 85,
    isCharging: Boolean = true,
    showBatteryText: Boolean = true
) {
    val fragments = remember { mutableStateListOf<DimensionalFragment>() }
    val lightningArcs = remember { mutableStateListOf<KamuiLightningArc>() }

    // Space fragments sucked into Kamui vortex
    LaunchedEffect(isCharging) {
        val maxF = if (isCharging) 40 else 20
        while (true) {
            if (fragments.size < maxF) {
                fragments.add(
                    DimensionalFragment(
                        angle = Random.nextFloat() * 2f * PI.toFloat(),
                        dist = 160f + Random.nextFloat() * 90f,
                        speed = 3.5f + Random.nextFloat() * 4.5f,
                        size = Random.nextFloat() * 4.5f + 1.5f,
                        alpha = Random.nextFloat() * 0.8f + 0.2f,
                        color = if (Random.nextBoolean()) Color(0xFF00E5FF) else Color(0xFFFF5252)
                    )
                )
            }

            val it = fragments.listIterator()
            while (it.hasNext()) {
                val f = it.next()
                f.dist -= f.speed
                f.angle += 0.06f // high vortex inward spin
                if (f.dist <= 15f) {
                    it.remove()
                }
            }

            // Generate erratic Kamui lightning strikes
            lightningArcs.clear()
            val arcCount = if (isCharging) Random.nextInt(6, 11) else 4
            for (i in 0 until arcCount) {
                val pts = mutableListOf<Offset>()
                val angle = Random.nextFloat() * 2f * PI.toFloat()
                var r = 25f
                pts.add(Offset(r * cos(angle), r * sin(angle)))
                val segments = 4
                for (s in 1..segments) {
                    r += 25f + Random.nextFloat() * 20f
                    val subAngle = angle + (Random.nextFloat() - 0.5f) * 0.9f
                    pts.add(Offset(r * cos(subAngle), r * sin(subAngle)))
                }
                lightningArcs.add(
                    KamuiLightningArc(
                        points = pts,
                        color = if (Random.nextBoolean()) Color.White else Color(0xFF00E5FF),
                        width = if (Random.nextBoolean()) 2.8f else 1.8f
                    )
                )
            }

            delay(28)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "kamui_super_trans")

    // Kamui Vortex High Speed Rotation
    val rotVortex by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCharging) 1200 else 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vortex_rot"
    )

    // Counter-spinning Kamui Shuriken Blades
    val rotShuriken by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCharging) 1800 else 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shuriken_rot"
    )

    // Core Lightning Cutter Voltage Pulse
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(320, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f * 0.44f

            // 1. Dimensional Fracture Glow (Outer Cyan Shockwave Aura)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.65f),
                        Color(0xFF006064).copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 2.3f * corePulse
                ),
                radius = radius * 2.3f * corePulse
            )

            // 2. Kamui Space-Time Spiral Vortex (Gravitational Wormhole)
            rotate(rotVortex, center) {
                drawKamuiSpaceWarp(center, radius * 1.55f)
            }

            // 3. 4 Giant Kamui Shuriken Blades (神威手裏剣)
            rotate(rotShuriken, center) {
                drawKamuiShuriken(center, radius * 1.85f, isCharging)
            }

            // 4. Inward Suction Debris & Energy Sparks
            fragments.forEach { f ->
                val px = center.x + f.dist * cos(f.angle)
                val py = center.y + f.dist * sin(f.angle)
                drawCircle(
                    color = f.color.copy(alpha = f.alpha),
                    radius = f.size,
                    center = Offset(px, py)
                )
            }

            // 5. Branching Raikiri Lightning Arcs
            lightningArcs.forEach { arc ->
                val path = Path()
                arc.points.forEachIndexed { idx, pt ->
                    val absX = center.x + pt.x
                    val absY = center.y + pt.y
                    if (idx == 0) path.moveTo(absX, absY) else path.lineTo(absX, absY)
                }
                drawPath(
                    path = path,
                    color = arc.color,
                    style = Stroke(width = arc.width.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 6. Central Black Hole Void with Glowing Raikiri Piercing Eye
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF05080E), Color(0xFF004D40), Color.Transparent),
                    center = center,
                    radius = radius * 0.75f
                ),
                radius = radius * 0.75f
            )

            // Searing White-Cyan Raikiri Blade Core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFF84FFFF), Color(0xFF00E5FF), Color.Transparent),
                    center = center,
                    radius = radius * 0.45f * corePulse
                ),
                radius = radius * 0.45f * corePulse
            )

            // Center Kakashi Mangekyo Sharingan Iris Pupil
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFFF1744)),
                    center = center,
                    radius = 9.dp.toPx()
                ),
                radius = 9.dp.toPx()
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

private fun DrawScope.drawKamuiSpaceWarp(center: Offset, radius: Float) {
    for (i in 0 until 14) {
        val angle = (i * (360f / 14f)) * (PI / 180f).toFloat()
        val path = Path()
        path.moveTo(center.x, center.y)
        val steps = 20
        for (s in 1..steps) {
            val t = s.toFloat() / steps
            val r = radius * t
            val rot = t * 3.5f
            val x = center.x + r * cos(angle + rot)
            val y = center.y + r * sin(angle + rot)
            path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = Color(0xFF00E5FF).copy(alpha = 0.55f),
            style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

private fun DrawScope.drawKamuiShuriken(center: Offset, bladeRadius: Float, isCharging: Boolean) {
    val bladeColor = Color(0xFF080C14)
    val edgeColor = if (isCharging) Color(0xFF00E5FF) else Color(0xFF80DEEA)

    for (b in 0 until 4) {
        val angleDeg = b * 90f
        rotate(angleDeg, center) {
            val path = Path().apply {
                moveTo(center.x, center.y)
                // Aerodynamic Kamui Shuriken blade
                cubicTo(
                    center.x - bladeRadius * 0.15f, center.y - bladeRadius * 0.35f,
                    center.x - bladeRadius * 0.35f, center.y - bladeRadius * 0.8f,
                    center.x, center.y - bladeRadius
                )
                cubicTo(
                    center.x + bladeRadius * 0.35f, center.y - bladeRadius * 0.8f,
                    center.x + bladeRadius * 0.15f, center.y - bladeRadius * 0.35f,
                    center.x, center.y
                )
                close()
            }
            drawPath(path = path, color = bladeColor)
            drawPath(
                path = path,
                color = edgeColor,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}
