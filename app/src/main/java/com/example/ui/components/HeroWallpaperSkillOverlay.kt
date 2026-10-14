package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class WallpaperCrow(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var wingPhase: Float,
    var size: Float
)

data class WallpaperLightningBolt(
    val points: List<Offset>,
    val color: Color
)

/**
 * Full-bleed animated wallpaper skill overlay that renders character-specific
 * legendary jutsu directly over the wallpaper artwork:
 * - Naruto: Animated Spinning Rasengan with wind blades, truth-seeking orbs, and Kurama flames
 * - Sasuke: Branching Chidori lightning storm, electric sparks, and Susanoo aura
 * - Itachi: Crimson Blood Moon, flying animated black crows, and Amaterasu black flames
 * - Kakashi: Kamui space-time warping vortex, Raikiri blade, and dimensional cracks
 */
@Composable
fun HeroWallpaperSkillOverlay(
    modifier: Modifier = Modifier,
    characterId: String,
    isCharging: Boolean = true
) {
    val crows = remember { mutableStateListOf<WallpaperCrow>() }
    val lightningBolts = remember { mutableStateListOf<WallpaperLightningBolt>() }

    // Crows & Lightning animation tickers
    LaunchedEffect(characterId) {
        if (characterId == "itachi_mangekyo") {
            crows.clear()
            for (i in 0..5) {
                crows.add(
                    WallpaperCrow(
                        x = Random.nextFloat(),
                        y = 0.15f + Random.nextFloat() * 0.35f,
                        vx = 0.003f + Random.nextFloat() * 0.003f,
                        vy = (Random.nextFloat() - 0.5f) * 0.001f,
                        wingPhase = Random.nextFloat() * 2f * PI.toFloat(),
                        size = 12f + Random.nextFloat() * 8f
                    )
                )
            }
            while (true) {
                crows.forEach { crow ->
                    crow.x += crow.vx
                    crow.y += crow.vy
                    crow.wingPhase += 0.28f
                    if (crow.x > 1.1f) {
                        crow.x = -0.1f
                        crow.y = 0.15f + Random.nextFloat() * 0.35f
                    }
                }
                delay(20)
            }
        } else if (characterId == "sasuke_rinnegan" || characterId == "kakashi_raikiri") {
            while (true) {
                lightningBolts.clear()
                val count = Random.nextInt(3, 7)
                for (i in 0 until count) {
                    val pts = mutableListOf<Offset>()
                    val startX = 0.2f + Random.nextFloat() * 0.6f
                    val startY = 0.3f + Random.nextFloat() * 0.4f
                    pts.add(Offset(startX, startY))
                    var cx = startX
                    var cy = startY
                    for (s in 0..4) {
                        cx += (Random.nextFloat() - 0.5f) * 0.12f
                        cy += (Random.nextFloat() - 0.5f) * 0.12f
                        pts.add(Offset(cx, cy))
                    }
                    lightningBolts.add(
                        WallpaperLightningBolt(
                            points = pts,
                            color = if (characterId == "sasuke_rinnegan") Color(0xFFB388FF) else Color(0xFF00E5FF)
                        )
                    )
                }
                delay(40)
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "wallpaper_jutsu")

    // High speed rotation for Rasengan / Kamui
    val rotJutsu by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCharging) 1200 else 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_wallpaper_jutsu"
    )

    // Counter spin
    val rotCounter by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCharging) 1800 else 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_counter"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        when (characterId) {
            "naruto_sixpaths" -> {
                // Naruto's Hand Skill: Massive Rasengan with Rasenshuriken wind blades
                val handCenter = Offset(w * 0.52f, h * 0.58f)
                val baseR = w * 0.18f

                // Golden Kurama Aura around body
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD54F).copy(alpha = 0.45f),
                            Color(0xFFFF9100).copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = handCenter,
                        radius = baseR * 2.8f
                    ),
                    radius = baseR * 2.8f
                )

                // 4 Wind Scythe Blades
                rotate(rotCounter, handCenter) {
                    drawWallpaperWindBlades(handCenter, baseR * 2.2f)
                }

                // Swirling Spiral Filaments
                rotate(rotJutsu, handCenter) {
                    drawWallpaperSpirals(handCenter, baseR * 1.4f, Color(0xFF00E5FF))
                }

                // Radiant White-Cyan Rasengan Core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, Color(0xFF80D8FF), Color(0xFF0091EA)),
                        center = handCenter,
                        radius = baseR * 0.75f
                    ),
                    radius = baseR * 0.75f
                )

                // Orbiting Truth-Seeking Orbs
                for (i in 0 until 4) {
                    val orbAngle = (rotJutsu + i * 90f) * (PI / 180f).toFloat()
                    val orbDist = baseR * 1.8f
                    val oc = Offset(
                        handCenter.x + orbDist * cos(orbAngle),
                        handCenter.y + (orbDist * 0.5f) * sin(orbAngle)
                    )
                    drawCircle(color = Color(0xFF00E5FF), radius = 5.dp.toPx(), center = oc)
                    drawCircle(color = Color(0xFF0B0E14), radius = 3.5.dp.toPx(), center = oc)
                }
            }

            "sasuke_rinnegan" -> {
                val handCenter = Offset(w * 0.55f, h * 0.60f)
                val baseR = w * 0.16f

                // Susanoo Violet Flame Aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFB388FF).copy(alpha = 0.5f),
                            Color(0xFF7C4DFF).copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = handCenter,
                        radius = baseR * 2.5f
                    ),
                    radius = baseR * 2.5f
                )

                // Chidori High-Voltage Plasma Core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, Color(0xFFE0B0FF), Color(0xFFB388FF)),
                        center = handCenter,
                        radius = baseR * 0.7f
                    ),
                    radius = baseR * 0.7f
                )

                // Branching Lightning Strikes
                lightningBolts.forEach { bolt ->
                    val path = Path()
                    bolt.points.forEachIndexed { idx, p ->
                        val px = p.x * w
                        val py = p.y * h
                        if (idx == 0) path.moveTo(px, py) else path.lineTo(px, py)
                    }
                    drawPath(
                        path = path,
                        color = bolt.color,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            "itachi_mangekyo" -> {
                // Crimson Blood Moon Glow in background
                val moonCenter = Offset(w * 0.72f, h * 0.26f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF1744).copy(alpha = 0.45f),
                            Color(0xFF880E4F).copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = moonCenter,
                        radius = w * 0.35f
                    ),
                    radius = w * 0.35f
                )

                // Flying Black Crows crossing the moon
                crows.forEach { crow ->
                    drawFlyingCrow(
                        center = Offset(crow.x * w, crow.y * h),
                        size = crow.size,
                        wingPhase = crow.wingPhase
                    )
                }

                // Swirling Amaterasu Black Flames at the base
                rotate(rotJutsu * 0.8f, Offset(w * 0.5f, h * 0.85f)) {
                    drawAmaterasuWallpaperFlames(Offset(w * 0.5f, h * 0.85f), w * 0.32f)
                }
            }

            "kakashi_raikiri" -> {
                val handCenter = Offset(w * 0.52f, h * 0.58f)
                val baseR = w * 0.16f

                // Kamui Space-Time Spiral Vortex
                rotate(rotJutsu, handCenter) {
                    for (i in 0 until 10) {
                        val angle = (i * 36f) * (PI / 180f).toFloat()
                        val path = Path()
                        path.moveTo(handCenter.x, handCenter.y)
                        val steps = 14
                        for (s in 1..steps) {
                            val t = s.toFloat() / steps
                            val r = baseR * 2.2f * t
                            val rot = t * 3.0f
                            val x = handCenter.x + r * cos(angle + rot)
                            val y = handCenter.y + r * sin(angle + rot)
                            path.lineTo(x, y)
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFF00E5FF).copy(alpha = 0.5f),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                // Searing Raikiri Electric Core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, Color(0xFF80DEEA), Color(0xFF00ACC1)),
                        center = handCenter,
                        radius = baseR * 0.75f
                    ),
                    radius = baseR * 0.75f
                )

                // Lightning Strikes
                lightningBolts.forEach { bolt ->
                    val path = Path()
                    bolt.points.forEachIndexed { idx, p ->
                        val px = p.x * w
                        val py = p.y * h
                        if (idx == 0) path.moveTo(px, py) else path.lineTo(px, py)
                    }
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawWallpaperWindBlades(center: Offset, bladeRadius: Float) {
    for (blade in 0 until 4) {
        val angleDeg = blade * 90f
        rotate(angleDeg, center) {
            val path = Path().apply {
                moveTo(center.x, center.y)
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
            drawPath(path = path, color = Color(0xFF80D8FF).copy(alpha = 0.45f))
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.8f),
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

private fun DrawScope.drawWallpaperSpirals(center: Offset, radius: Float, color: Color) {
    val strands = 10
    val angleStep = (2f * PI / strands).toFloat()
    for (i in 0 until strands) {
        val startAngle = i * angleStep
        val path = Path()
        val steps = 18
        for (s in 0..steps) {
            val t = s.toFloat() / steps
            val r = radius * t
            val theta = startAngle + t * 2.8f
            val x = center.x + r * cos(theta)
            val y = center.y + r * sin(theta)
            if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = color.copy(alpha = 0.65f),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

private fun DrawScope.drawFlyingCrow(center: Offset, size: Float, wingPhase: Float) {
    val wingLift = sin(wingPhase) * size * 0.7f
    val path = Path().apply {
        // Left wing
        moveTo(center.x - size, center.y - wingLift)
        cubicTo(
            center.x - size * 0.5f, center.y - wingLift * 0.5f,
            center.x - size * 0.2f, center.y,
            center.x, center.y
        )
        // Right wing
        cubicTo(
            center.x + size * 0.2f, center.y,
            center.x + size * 0.5f, center.y - wingLift * 0.5f,
            center.x + size, center.y - wingLift
        )
        // Tail
        lineTo(center.x, center.y + size * 0.4f)
        close()
    }
    drawPath(path = path, color = Color(0xFF101014))
}

private fun DrawScope.drawAmaterasuWallpaperFlames(center: Offset, radius: Float) {
    for (i in 0 until 6) {
        val angleDeg = i * 60f
        rotate(angleDeg, center) {
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.4f)
                cubicTo(
                    center.x - 14f, center.y - radius * 0.7f,
                    center.x + 14f, center.y - radius * 1.0f,
                    center.x, center.y - radius * 1.2f
                )
                cubicTo(
                    center.x + 18f, center.y - radius * 0.9f,
                    center.x + 8f, center.y - radius * 0.6f,
                    center.x, center.y - radius * 0.4f
                )
                close()
            }
            drawPath(path = path, color = Color(0xFF121216))
            drawPath(
                path = path,
                color = Color(0xFFFF1744),
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}
