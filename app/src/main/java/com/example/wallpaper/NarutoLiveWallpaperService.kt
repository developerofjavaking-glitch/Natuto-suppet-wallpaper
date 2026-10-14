package com.example.wallpaper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.example.R
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

        private val particles = mutableListOf<WallpaperParticle>()
        private val touchSparks = mutableListOf<TouchSpark>()
        private var frameCount = 0L

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
                    // Scale & center crop to fill screen
                    val scale = maxOf(
                        surfaceWidth.toFloat() / original.width,
                        surfaceHeight.toFloat() / original.height
                    )
                    val scaledW = (original.width * scale).toInt()
                    val scaledH = (original.height * scale).toInt()
                    val scaled = Bitmap.createScaledBitmap(original, scaledW, scaledH, true)

                    // Crop center
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
            for (i in 0 until 12) {
                val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                val speed = Random.nextFloat() * 6f + 2f
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

                    // 1. Draw character background
                    val bmp = characterBitmap
                    if (bmp != null && !bmp.isRecycled) {
                        canvas.drawBitmap(bmp, 0f, 0f, backgroundPaint)
                    } else {
                        canvas.drawColor(Color.rgb(11, 14, 20))
                    }

                    // 2. Dynamic chakra radial aura glow
                    val pulse = (sin(frameCount * 0.05f) * 0.5f + 0.5f)
                    val glowColor = getParticleColorForCurrentChar()
                    val glowAlpha = (30 + (pulse * 30)).toInt()
                    glowPaint.shader = RadialGradient(
                        surfaceWidth / 2f,
                        surfaceHeight * 0.65f,
                        surfaceWidth * 0.6f,
                        Color.argb(glowAlpha, Color.red(glowColor), Color.green(glowColor), Color.blue(glowColor)),
                        Color.TRANSPARENT,
                        Shader.TileMode.CLAMP
                    )
                    canvas.drawRect(0f, 0f, surfaceWidth.toFloat(), surfaceHeight.toFloat(), glowPaint)

                    // 3. Floating chakra particles
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

                    // 4. Touch interaction sparks
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
}
