package com.example.wallpaper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.example.audio.AnimeSoundEngine
import com.example.data.CharacterRepository
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class NarutoLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return NarutoWallpaperEngine()
    }

    inner class NarutoWallpaperEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private var isVisible = false
        private var surfaceWidth = 0
        private var surfaceHeight = 0

        private var characterBitmap: Bitmap? = null
        private var currentCharacterId = ""

        private val backgroundPaint = Paint().apply { isFilterBitmap = true }
        private val particlePaint = Paint().apply { isAntiAlias = true }
        private val glowPaint = Paint().apply { isAntiAlias = true }
        private val skillPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
        }
        private val fillPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        private val particles = mutableListOf<WallpaperParticle>()
        private val touchSparks = mutableListOf<TouchSpark>()
        private var frameCount = 0L

        // Animated Crow state for Itachi
        private val wallpaperCrows = mutableListOf<LiveCrow>()

        private val drawRunnable = object : Runnable {
            override fun run() {
                drawFrame()
                if (isVisible) {
                    handler.postDelayed(this, 16) // ~60 FPS
                }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            isVisible = visible
            if (visible) {
                reloadCharacterBitmap()
                handler.post(drawRunnable)
            } else {
                handler.removeCallbacks(drawRunnable)
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            surfaceWidth = width
            surfaceHeight = height
            reloadCharacterBitmap()
            initParticles()
            initCrows()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            isVisible = false
            handler.removeCallbacks(drawRunnable)
            characterBitmap?.recycle()
            characterBitmap = null
        }

        override fun onTouchEvent(event: MotionEvent?) {
            super.onTouchEvent(event)
            if (event == null) return

            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    spawnTouchSpark(event.x, event.y)
                    val prefs = WallpaperPreferences(applicationContext)
                    if (prefs.soundOnTouch && event.action == MotionEvent.ACTION_DOWN) {
                        val character = CharacterRepository.getCharacterById(prefs.selectedCharacterId)
                        AnimeSoundEngine.playJutsu(character.soundType)
                    }
                }
            }
        }

        private fun reloadCharacterBitmap() {
            if (surfaceWidth <= 0 || surfaceHeight <= 0) return
            val prefs = WallpaperPreferences(applicationContext)
            val charId = prefs.selectedCharacterId
            if (charId == currentCharacterId && characterBitmap != null) return

            currentCharacterId = charId
            val character = CharacterRepository.getCharacterById(charId)

            try {
                val original = BitmapFactory.decodeResource(resources, character.drawableRes)
                if (original != null) {
                    val scale = maxOf(
                        surfaceWidth.toFloat() / original.width,
                        surfaceHeight.toFloat() / original.height
                    )
                    val scaledW = (original.width * scale).toInt()
                    val scaledH = (original.height * scale).toInt()
                    val scaled = Bitmap.createScaledBitmap(original, scaledW, scaledH, true)

                    val cropX = ((scaledW - surfaceWidth) / 2).coerceAtLeast(0)
                    val cropY = ((scaledH - surfaceHeight) / 2).coerceAtLeast(0)
                    val cropped = Bitmap.createBitmap(scaled, cropX, cropY, surfaceWidth, surfaceHeight)

                    characterBitmap?.recycle()
                    characterBitmap = cropped
                    if (scaled != original && scaled != cropped) scaled.recycle()
                    original.recycle()
                }
            } catch (_: Exception) {}
        }

        private fun initParticles() {
            particles.clear()
            val prefs = WallpaperPreferences(applicationContext)
            val count = when (prefs.particleDensity) {
                1 -> 25
                3 -> 75
                else -> 45
            }
            for (i in 0 until count) {
                particles.add(
                    WallpaperParticle(
                        x = Random.nextFloat() * surfaceWidth,
                        y = Random.nextFloat() * surfaceHeight,
                        vx = (Random.nextFloat() - 0.5f) * 1.5f,
                        vy = -Random.nextFloat() * 2.5f - 0.8f,
                        radius = Random.nextFloat() * 4.5f + 1.5f,
                        alpha = Random.nextFloat() * 0.7f + 0.3f,
                        color = getParticleColorForCurrentChar()
                    )
                )
            }
        }

        private fun initCrows() {
            wallpaperCrows.clear()
            for (i in 0..5) {
                wallpaperCrows.add(
                    LiveCrow(
                        x = Random.nextFloat() * surfaceWidth,
                        y = surfaceHeight * (0.15f + Random.nextFloat() * 0.3f),
                        vx = 2.5f + Random.nextFloat() * 2.5f,
                        vy = (Random.nextFloat() - 0.5f) * 0.8f,
                        wingPhase = Random.nextFloat() * 6.28f,
                        size = 18f + Random.nextFloat() * 12f
                    )
                )
            }
        }

        private fun getParticleColorForCurrentChar(): Int {
            return when (currentCharacterId) {
                "sasuke_rinnegan" -> Color.rgb(180, 130, 255)
                "itachi_mangekyo" -> Color.rgb(255, 60, 60)
                "kakashi_raikiri" -> Color.rgb(0, 230, 255)
                else -> Color.rgb(255, 190, 30) // Naruto gold
            }
        }

        private fun spawnTouchSpark(x: Float, y: Float) {
            val color = getParticleColorForCurrentChar()
            for (i in 0 until 14) {
                val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                val speed = Random.nextFloat() * 7f + 2f
                touchSparks.add(
                    TouchSpark(
                        x = x,
                        y = y,
                        vx = cos(angle) * speed,
                        vy = sin(angle) * speed,
                        radius = Random.nextFloat() * 5f + 2f,
                        alpha = 1f,
                        color = color
                    )
                )
            }
        }

        private fun drawFrame() {
            val holder = surfaceHolder ?: return
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    frameCount++

                    // 1. Draw character background image
                    val bmp = characterBitmap
                    if (bmp != null && !bmp.isRecycled) {
                        canvas.drawBitmap(bmp, 0f, 0f, backgroundPaint)
                    } else {
                        canvas.drawColor(Color.rgb(11, 14, 20))
                    }

                    // 2. Dynamic Ambient Chakra Aura Glow
                    val pulse = (sin(frameCount * 0.05f) * 0.5f + 0.5f)
                    val glowColor = getParticleColorForCurrentChar()
                    val glowAlpha = (30 + (pulse * 30)).toInt()
                    glowPaint.shader = RadialGradient(
                        surfaceWidth / 2f,
                        surfaceHeight * 0.62f,
                        surfaceWidth * 0.65f,
                        Color.argb(glowAlpha, Color.red(glowColor), Color.green(glowColor), Color.blue(glowColor)),
                        Color.TRANSPARENT,
                        Shader.TileMode.CLAMP
                    )
                    canvas.drawRect(0f, 0f, surfaceWidth.toFloat(), surfaceHeight.toFloat(), glowPaint)

                    // 3. Draw Character-Specific Animated Wallpaper Skills!
                    when (currentCharacterId) {
                        "naruto_sixpaths" -> {
                            drawNarutoAnimatedWallpaperSkill(canvas)
                        }
                        "sasuke_rinnegan" -> {
                            drawSasukeAnimatedWallpaperSkill(canvas)
                        }
                        "itachi_mangekyo" -> {
                            drawItachiAnimatedWallpaperSkill(canvas)
                        }
                        "kakashi_raikiri" -> {
                            drawKakashiAnimatedWallpaperSkill(canvas)
                        }
                        else -> {
                            drawNarutoAnimatedWallpaperSkill(canvas)
                        }
                    }

                    // 4. Floating chakra ambient particles
                    for (p in particles) {
                        p.x += p.vx
                        p.y += p.vy
                        if (p.y < -10f) {
                            p.y = surfaceHeight + 10f
                            p.x = Random.nextFloat() * surfaceWidth
                        }
                        if (p.x < 0f) p.x = surfaceWidth.toFloat()
                        if (p.x > surfaceWidth) p.x = 0f

                        particlePaint.color = p.color
                        particlePaint.alpha = (p.alpha * 255).toInt()
                        canvas.drawCircle(p.x, p.y, p.radius, particlePaint)
                    }

                    // 5. Touch interaction sparks
                    val it = touchSparks.iterator()
                    while (it.hasNext()) {
                        val s = it.next()
                        s.x += s.vx
                        s.y += s.vy
                        s.alpha -= 0.035f
                        if (s.alpha <= 0f) {
                            it.remove()
                        } else {
                            particlePaint.color = s.color
                            particlePaint.alpha = (s.alpha * 255).toInt()
                            canvas.drawCircle(s.x, s.y, s.radius * s.alpha, particlePaint)
                        }
                    }
                }
            } catch (_: Exception) {
            } finally {
                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas)
                    } catch (_: Exception) {}
                }
            }
        }

        /**
         * Naruto: Live Spinning Rasengan with rotating wind blades, spiral filaments, and Truth-Seeking Orbs!
         */
        private fun drawNarutoAnimatedWallpaperSkill(canvas: Canvas) {
            val hx = surfaceWidth * 0.52f
            val hy = surfaceHeight * 0.58f
            val baseR = surfaceWidth * 0.16f

            // Golden Kurama Aura
            fillPaint.shader = RadialGradient(
                hx, hy, baseR * 2.5f,
                intArrayOf(Color.argb(120, 255, 213, 79), Color.argb(40, 255, 145, 0), Color.TRANSPARENT),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(hx, hy, baseR * 2.5f, fillPaint)

            // Swirling Spiral Filaments
            canvas.save()
            canvas.rotate(frameCount * 3.5f, hx, hy)
            skillPaint.strokeWidth = 3f
            skillPaint.shader = null
            skillPaint.color = Color.rgb(0, 229, 255)
            for (i in 0 until 10) {
                val angle = (i * 36f) * (Math.PI / 180f).toFloat()
                val path = Path()
                path.moveTo(hx, hy)
                for (s in 1..14) {
                    val t = s / 14f
                    val r = baseR * 1.3f * t
                    val theta = angle + t * 2.8f
                    val px = hx + r * cos(theta)
                    val py = hy + r * sin(theta)
                    path.lineTo(px, py)
                }
                canvas.drawPath(path, skillPaint)
            }
            canvas.restore()

            // 4 Rotating Wind Scythe Blades
            canvas.save()
            canvas.rotate(-frameCount * 2.2f, hx, hy)
            fillPaint.shader = null
            fillPaint.color = Color.argb(90, 128, 216, 255)
            skillPaint.color = Color.WHITE
            skillPaint.strokeWidth = 2f
            for (b in 0 until 4) {
                canvas.save()
                canvas.rotate(b * 90f, hx, hy)
                val bladeR = baseR * 2.0f
                val path = Path()
                path.moveTo(hx, hy)
                path.cubicTo(
                    hx + bladeR * 0.3f, hy - bladeR * 0.2f,
                    hx + bladeR * 0.8f, hy - bladeR * 0.5f,
                    hx + bladeR, hy - bladeR * 0.1f
                )
                path.cubicTo(
                    hx + bladeR * 0.7f, hy + bladeR * 0.1f,
                    hx + bladeR * 0.3f, hy + bladeR * 0.15f,
                    hx, hy
                )
                path.close()
                canvas.drawPath(path, fillPaint)
                canvas.drawPath(path, skillPaint)
                canvas.restore()
            }
            canvas.restore()

            // Condensed White-Cyan Rasengan Core
            fillPaint.shader = RadialGradient(
                hx, hy, baseR * 0.7f,
                intArrayOf(Color.WHITE, Color.rgb(128, 216, 255), Color.rgb(0, 145, 234)),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(hx, hy, baseR * 0.7f, fillPaint)

            // Orbiting Truth-Seeking Orbs
            for (i in 0 until 4) {
                val orbAngle = (frameCount * 0.04f + i * (Math.PI / 2f)).toFloat()
                val dist = baseR * 1.7f
                val ox = hx + dist * cos(orbAngle)
                val oy = hy + (dist * 0.45f) * sin(orbAngle)
                fillPaint.shader = null
                fillPaint.color = Color.rgb(0, 229, 255)
                canvas.drawCircle(ox, oy, 12f, fillPaint)
                fillPaint.color = Color.rgb(11, 14, 20)
                canvas.drawCircle(ox, oy, 8f, fillPaint)
            }
        }

        /**
         * Sasuke: Crackling Chidori, Susanoo violet flame aura, and branching lightning strikes!
         */
        private fun drawSasukeAnimatedWallpaperSkill(canvas: Canvas) {
            val hx = surfaceWidth * 0.55f
            val hy = surfaceHeight * 0.60f
            val baseR = surfaceWidth * 0.16f

            // Susanoo Aura
            fillPaint.shader = RadialGradient(
                hx, hy, baseR * 2.5f,
                intArrayOf(Color.argb(120, 179, 136, 255), Color.argb(40, 124, 77, 255), Color.TRANSPARENT),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(hx, hy, baseR * 2.5f, fillPaint)

            // High Voltage Chidori Core
            fillPaint.shader = RadialGradient(
                hx, hy, baseR * 0.65f,
                intArrayOf(Color.WHITE, Color.rgb(224, 176, 255), Color.rgb(179, 136, 255)),
                floatArrayOf(0f, 0.4f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(hx, hy, baseR * 0.65f, fillPaint)

            // Branching Lightning Bolts
            skillPaint.shader = null
            skillPaint.color = Color.WHITE
            skillPaint.strokeWidth = 3f

            for (i in 0..4) {
                val angle = Random.nextFloat() * 6.28f
                var cx = hx
                var cy = hy
                val path = Path()
                path.moveTo(cx, cy)
                val segs = 4
                for (s in 0 until segs) {
                    cx += (Random.nextFloat() - 0.5f) * 60f + 25f * cos(angle)
                    cy += (Random.nextFloat() - 0.5f) * 60f + 25f * sin(angle)
                    path.lineTo(cx, cy)
                }
                canvas.drawPath(path, skillPaint)
            }
        }

        /**
         * Itachi: Crimson Blood Moon, flying animated black crows, and Amaterasu black flames!
         */
        private fun drawItachiAnimatedWallpaperSkill(canvas: Canvas) {
            // Blood Moon
            val mx = surfaceWidth * 0.72f
            val my = surfaceHeight * 0.26f
            val mr = surfaceWidth * 0.28f

            fillPaint.shader = RadialGradient(
                mx, my, mr,
                intArrayOf(Color.argb(140, 255, 23, 68), Color.argb(50, 136, 14, 79), Color.TRANSPARENT),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(mx, my, mr, fillPaint)

            // Flying Crows
            fillPaint.shader = null
            fillPaint.color = Color.rgb(16, 16, 20)
            for (crow in wallpaperCrows) {
                crow.x += crow.vx
                crow.y += crow.vy
                crow.wingPhase += 0.25f
                if (crow.x > surfaceWidth + 50f) {
                    crow.x = -50f
                    crow.y = surfaceHeight * (0.15f + Random.nextFloat() * 0.3f)
                }

                val wingLift = sin(crow.wingPhase) * crow.size * 0.65f
                val path = Path()
                path.moveTo(crow.x - crow.size, crow.y - wingLift)
                path.cubicTo(
                    crow.x - crow.size * 0.5f, crow.y - wingLift * 0.5f,
                    crow.x - crow.size * 0.2f, crow.y,
                    crow.x, crow.y
                )
                path.cubicTo(
                    crow.x + crow.size * 0.2f, crow.y,
                    crow.x + crow.size * 0.5f, crow.y - wingLift * 0.5f,
                    crow.x + crow.size, crow.y - wingLift
                )
                path.lineTo(crow.x, crow.y + crow.size * 0.4f)
                path.close()
                canvas.drawPath(path, fillPaint)
            }

            // Amaterasu Black Flames at base
            val fx = surfaceWidth * 0.5f
            val fy = surfaceHeight * 0.82f
            canvas.save()
            canvas.rotate(frameCount * 0.8f, fx, fy)
            fillPaint.color = Color.rgb(18, 18, 22)
            skillPaint.shader = null
            skillPaint.color = Color.rgb(255, 23, 68)
            skillPaint.strokeWidth = 2.5f

            for (i in 0 until 6) {
                canvas.save()
                canvas.rotate(i * 60f, fx, fy)
                val path = Path()
                val fr = surfaceWidth * 0.22f
                path.moveTo(fx, fy - fr * 0.4f)
                path.cubicTo(
                    fx - 18f, fy - fr * 0.7f,
                    fx + 18f, fy - fr * 1.0f,
                    fx, fy - fr * 1.25f
                )
                path.cubicTo(
                    fx + 24f, fy - fr * 0.9f,
                    fx + 10f, fy - fr * 0.6f,
                    fx, fy - fr * 0.4f
                )
                path.close()
                canvas.drawPath(path, fillPaint)
                canvas.drawPath(path, skillPaint)
                canvas.restore()
            }
            canvas.restore()
        }

        /**
         * Kakashi: Kamui Space-Time Dimensional Wormhole & Raikiri Lightning Cutter!
         */
        private fun drawKakashiAnimatedWallpaperSkill(canvas: Canvas) {
            val hx = surfaceWidth * 0.52f
            val hy = surfaceHeight * 0.58f
            val baseR = surfaceWidth * 0.16f

            // Kamui Space-Time Spiral Vortex
            canvas.save()
            canvas.rotate(frameCount * 2.8f, hx, hy)
            skillPaint.shader = null
            skillPaint.color = Color.argb(140, 0, 229, 255)
            skillPaint.strokeWidth = 2.5f

            for (i in 0 until 12) {
                val angle = (i * 30f) * (Math.PI / 180f).toFloat()
                val path = Path()
                path.moveTo(hx, hy)
                for (s in 1..16) {
                    val t = s / 16f
                    val r = baseR * 2.0f * t
                    val rot = t * 3.2f
                    val px = hx + r * cos(angle + rot)
                    val py = hy + r * sin(angle + rot)
                    path.lineTo(px, py)
                }
                canvas.drawPath(path, skillPaint)
            }
            canvas.restore()

            // 4 Kamui Shuriken Blades
            canvas.save()
            canvas.rotate(-frameCount * 1.8f, hx, hy)
            fillPaint.shader = null
            fillPaint.color = Color.rgb(8, 12, 20)
            skillPaint.color = Color.rgb(0, 229, 255)
            skillPaint.strokeWidth = 2f

            for (b in 0 until 4) {
                canvas.save()
                canvas.rotate(b * 90f, hx, hy)
                val sR = baseR * 1.8f
                val path = Path()
                path.moveTo(hx, hy)
                path.cubicTo(
                    hx - sR * 0.15f, hy - sR * 0.35f,
                    hx - sR * 0.35f, hy - sR * 0.8f,
                    hx, hy - sR
                )
                path.cubicTo(
                    hx + sR * 0.35f, hy - sR * 0.8f,
                    hx + sR * 0.15f, hy - sR * 0.35f,
                    hx, hy
                )
                path.close()
                canvas.drawPath(path, fillPaint)
                canvas.drawPath(path, skillPaint)
                canvas.restore()
            }
            canvas.restore()

            // Searing White-Cyan Raikiri Blade Core
            fillPaint.shader = RadialGradient(
                hx, hy, baseR * 0.7f,
                intArrayOf(Color.WHITE, Color.rgb(128, 222, 234), Color.rgb(0, 172, 193)),
                floatArrayOf(0f, 0.4f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(hx, hy, baseR * 0.7f, fillPaint)

            // Lightning Cutter Arcs
            skillPaint.color = Color.WHITE
            skillPaint.strokeWidth = 3f
            for (i in 0..3) {
                val a = Random.nextFloat() * 6.28f
                var cx = hx
                var cy = hy
                val path = Path()
                path.moveTo(cx, cy)
                for (s in 0..3) {
                    cx += 25f * cos(a) + (Random.nextFloat() - 0.5f) * 40f
                    cy += 25f * sin(a) + (Random.nextFloat() - 0.5f) * 40f
                    path.lineTo(cx, cy)
                }
                canvas.drawPath(path, skillPaint)
            }
        }
    }

    private class WallpaperParticle(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var radius: Float,
        var alpha: Float,
        var color: Int
    )

    private class TouchSpark(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var radius: Float,
        var alpha: Float,
        var color: Int
    )

    private class LiveCrow(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var wingPhase: Float,
        var size: Float
    )
}
