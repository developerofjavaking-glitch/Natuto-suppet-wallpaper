package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.JutsuSoundType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

object AnimeSoundEngine {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private const val SAMPLE_RATE = 22050

    var isMuted: Boolean = false
    var volume: Float = 0.85f

    fun playJutsu(type: JutsuSoundType) {
        if (isMuted) return
        when (type) {
            JutsuSoundType.RASENGAN -> playRasengan()
            JutsuSoundType.CHIDORI -> playChidori()
            JutsuSoundType.SHARINGAN -> playSharingan()
            JutsuSoundType.RAIKIRI -> playRaikiri()
            JutsuSoundType.FLYING_RAIJIN -> playFlyingRaijin()
            JutsuSoundType.CHAKRA_SURGE -> playChakraSurge()
        }
    }

    /**
     * Swirling high-velocity chakra sphere with rising frequency and vortex harmonics.
     */
    fun playRasengan() {
        if (isMuted) return
        scope.launch {
            val durationMs = 600
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                // Frequency sweeps upwards from 140Hz to 680Hz
                val freq = 140.0 + 540.0 * (progress * progress)
                // Whirl pulsation
                val whirl = 1.0 + 0.35 * sin(2.0 * PI * 22.0 * t)
                // Envelope
                val envelope = sin(PI * progress) * (1.0 - 0.2 * progress)
                // Air vortex noise component
                val noise = (Random.nextDouble() * 2.0 - 1.0) * 0.25

                val sample = ((sin(2.0 * PI * freq * t) * whirl + noise) * envelope * Short.MAX_VALUE * volume).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Thousand Birds electric crackle with erratic high-voltage sparks.
     */
    fun playChidori() {
        if (isMuted) return
        scope.launch {
            val durationMs = 650
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                // High voltage carrier
                val carrier = sin(2.0 * PI * 1800.0 * t) * 0.4 + sin(2.0 * PI * 3200.0 * t) * 0.25
                // Random micro spark discharges
                val spark = if (Random.nextFloat() > 0.88f) (Random.nextDouble() * 2.0 - 1.0) * 0.85 else 0.0
                // Envelope with stutter
                val envelope = (1.0 - progress) * (0.6 + 0.4 * sin(2.0 * PI * 40.0 * t))
                val sample = ((carrier + spark) * envelope * Short.MAX_VALUE * volume).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Mystical Sharingan awakening bell chime with eerie lingering phaser resonance.
     */
    fun playSharingan() {
        if (isMuted) return
        scope.launch {
            val durationMs = 700
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                // Dual harmonics
                val tone1 = sin(2.0 * PI * 880.0 * t)
                val tone2 = sin(2.0 * PI * 1320.0 * t) * 0.5
                val decay = exp(-4.5 * progress)
                val tremolo = 1.0 + 0.25 * sin(2.0 * PI * 8.0 * t)
                val sample = ((tone1 + tone2) * decay * tremolo * Short.MAX_VALUE * volume).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Sharp lightning cutter blade with high-frequency electric slice.
     */
    fun playRaikiri() {
        if (isMuted) return
        scope.launch {
            val durationMs = 500
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                val freq = 2400.0 - 1200.0 * progress
                val tone = sin(2.0 * PI * freq * t)
                val noise = (Random.nextDouble() * 2.0 - 1.0) * 0.4
                val envelope = exp(-3.0 * progress) * sin(PI * progress.coerceAtMost(0.3) / 0.3)
                val sample = ((tone + noise) * envelope * Short.MAX_VALUE * volume).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Flying Raijin teleportation zip and metallic kunai flash.
     */
    fun playFlyingRaijin() {
        if (isMuted) return
        scope.launch {
            val durationMs = 350
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                val freq = 120.0 + 2800.0 * progress * (1.0 - progress) * 4.0
                val tone = sin(2.0 * PI * freq * t)
                val metallic = sin(2.0 * PI * 4400.0 * t) * exp(-12.0 * progress) * 0.6
                val envelope = sin(PI * progress)
                val sample = ((tone + metallic) * envelope * Short.MAX_VALUE * volume).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Deep resonant chakra bass pulse.
     */
    fun playChakraSurge() {
        if (isMuted) return
        scope.launch {
            val durationMs = 550
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                val freq = 65.0 + 120.0 * sin(PI * progress)
                val subBass = sin(2.0 * PI * freq * t)
                val harmonic = sin(2.0 * PI * (freq * 2.0) * t) * 0.35
                val envelope = sin(PI * progress) * exp(-1.2 * progress)
                val sample = ((subBass + harmonic) * envelope * Short.MAX_VALUE * volume).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Plug-in charging connected sound: Rising 4-tone chakra restoration melody (C5, E5, G5, C6).
     */
    fun playChargingConnected() {
        if (isMuted) return
        scope.launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            val noteDurationMs = 120
            val totalDurationMs = noteDurationMs * notes.size + 150
            val numSamples = (SAMPLE_RATE * (totalDurationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            notes.forEachIndexed { index, freq ->
                val startSample = (SAMPLE_RATE * (index * noteDurationMs / 1000.0)).toInt()
                val noteSamples = (SAMPLE_RATE * (noteDurationMs / 1000.0)).toInt() + (SAMPLE_RATE * 0.15).toInt()
                for (s in 0 until noteSamples) {
                    val idx = startSample + s
                    if (idx < numSamples) {
                        val t = s.toDouble() / SAMPLE_RATE
                        val env = exp(-6.0 * (s.toDouble() / noteSamples))
                        val tone = sin(2.0 * PI * freq * t) + sin(2.0 * PI * (freq * 2.0) * t) * 0.25
                        val current = buffer[idx].toInt()
                        val added = (tone * env * Short.MAX_VALUE * 0.45 * volume).toInt()
                        buffer[idx] = (current + added).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                }
            }
            playPcm(buffer)
        }
    }

    /**
     * Charger disconnected sound: Descending 2-tone pitch decay.
     */
    fun playChargingDisconnected() {
        if (isMuted) return
        scope.launch {
            val notes = listOf(783.99, 523.25) // G5, C5
            val noteDurationMs = 140
            val totalDurationMs = noteDurationMs * notes.size + 100
            val numSamples = (SAMPLE_RATE * (totalDurationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            notes.forEachIndexed { index, freq ->
                val startSample = (SAMPLE_RATE * (index * noteDurationMs / 1000.0)).toInt()
                val noteSamples = (SAMPLE_RATE * (noteDurationMs / 1000.0)).toInt()
                for (s in 0 until noteSamples) {
                    val idx = startSample + s
                    if (idx < numSamples) {
                        val t = s.toDouble() / SAMPLE_RATE
                        val env = exp(-8.0 * (s.toDouble() / noteSamples))
                        val tone = sin(2.0 * PI * freq * t)
                        val current = buffer[idx].toInt()
                        val added = (tone * env * Short.MAX_VALUE * 0.45 * volume).toInt()
                        buffer[idx] = (current + added).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                }
            }
            playPcm(buffer)
        }
    }

    /**
     * Chakra Full (100% Battery) Fanfare!
     */
    fun playFullyCharged() {
        if (isMuted) return
        scope.launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50, 1318.51) // C5, E5, G5, C6, E6
            val noteDurationMs = 130
            val totalDurationMs = noteDurationMs * notes.size + 400
            val numSamples = (SAMPLE_RATE * (totalDurationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            notes.forEachIndexed { index, freq ->
                val startSample = (SAMPLE_RATE * (index * noteDurationMs / 1000.0)).toInt()
                val noteSamples = (SAMPLE_RATE * (noteDurationMs / 1000.0)).toInt() + (SAMPLE_RATE * 0.3).toInt()
                for (s in 0 until noteSamples) {
                    val idx = startSample + s
                    if (idx < numSamples) {
                        val t = s.toDouble() / SAMPLE_RATE
                        val env = exp(-4.0 * (s.toDouble() / noteSamples))
                        val tone = sin(2.0 * PI * freq * t) + sin(2.0 * PI * (freq * 2.0) * t) * 0.2
                        val current = buffer[idx].toInt()
                        val added = (tone * env * Short.MAX_VALUE * 0.4 * volume).toInt()
                        buffer[idx] = (current + added).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                }
            }
            playPcm(buffer)
        }
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            // Release track after playback finished
            scope.launch {
                val delayMs = (buffer.size.toDouble() / SAMPLE_RATE * 1000).toLong() + 100
                kotlinx.coroutines.delay(delayMs)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }
}
