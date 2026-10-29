package com.example.engine

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import com.example.model.MusicTrack
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class LoFiBeatSynthesizer {

    private var audioTrack: AudioTrack? = null
    private var synthThread: Thread? = null
    private val isRunning = AtomicBoolean(false)
    private var currentTrack = MusicTrack.MIDNIGHT_LOFI
    private var currentVolume = 0.35f

    fun start(track: MusicTrack = MusicTrack.MIDNIGHT_LOFI, volume: Float = 0.35f) {
        if (track == MusicTrack.MUTE || volume <= 0.01f) {
            stop()
            currentTrack = track
            currentVolume = volume
            return
        }

        if (isRunning.get() && currentTrack == track) {
            setVolume(volume)
            return
        }

        stop()
        currentTrack = track
        currentVolume = volume
        isRunning.set(true)

        synthThread = Thread({
            generateAudioLoop()
        }, "LoFiBeatSynthThread").apply {
            priority = Thread.NORM_PRIORITY
            start()
        }
    }

    fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
        try {
            audioTrack?.setVolume(currentVolume)
        } catch (e: Exception) {
            Log.e("LoFiSynth", "Error setting volume", e)
        }
    }

    fun stop() {
        isRunning.set(false)
        try {
            synthThread?.interrupt()
            synthThread = null
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            Log.e("LoFiSynth", "Error stopping track", e)
        }
    }

    private fun generateAudioLoop() {
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(4096)

        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track
            track.setVolume(currentVolume)
            track.play()

            val pcmBuffer = ShortArray(bufferSize / 2)
            var sampleIndex = 0L

            // Chord frequencies for tracks
            // Midnight Lo-Fi: Cmaj7 (C4, E4, G4, B4) -> Am7 (A3, C4, E4, G4) -> Dm7 (D4, F4, A4, C5) -> G7 (G3, B3, D4, F4)
            val chordProgressions = when (currentTrack) {
                MusicTrack.MIDNIGHT_LOFI -> listOf(
                    doubleArrayOf(261.63, 329.63, 392.00, 493.88), // Cmaj7
                    doubleArrayOf(220.00, 261.63, 329.63, 392.00), // Am7
                    doubleArrayOf(293.66, 349.23, 440.00, 523.25), // Dm7
                    doubleArrayOf(196.00, 246.94, 293.66, 349.23)  // G7
                )
                MusicTrack.CHILL_STUDY -> listOf(
                    doubleArrayOf(261.63, 329.63, 392.00), // C
                    doubleArrayOf(329.63, 392.00, 493.88), // Em
                    doubleArrayOf(349.23, 440.00, 523.25), // F
                    doubleArrayOf(392.00, 493.88, 587.33)  // G
                )
                MusicTrack.SYNTHWAVE_FOCUS -> listOf(
                    doubleArrayOf(130.81, 196.00, 261.63, 329.63), // C3 bass + pad
                    doubleArrayOf(146.83, 220.00, 293.66, 349.23), // D3
                    doubleArrayOf(110.00, 164.81, 220.00, 261.63), // A2
                    doubleArrayOf(174.61, 261.63, 349.23, 440.00)  // F3
                )
                MusicTrack.ACOUSTIC_ZEN -> listOf(
                    doubleArrayOf(216.00, 432.00, 648.00), // Zen tuning 432Hz
                    doubleArrayOf(256.87, 513.74, 770.61),
                    doubleArrayOf(288.00, 576.00, 864.00),
                    doubleArrayOf(324.00, 648.00, 972.00)
                )
                MusicTrack.MUTE -> emptyList()
            }

            val samplesPerChord = (sampleRate * 2.5).toLong() // 2.5 seconds per chord

            while (isRunning.get() && !Thread.currentThread().isInterrupted) {
                if (chordProgressions.isEmpty()) {
                    Thread.sleep(100)
                    continue
                }

                for (i in pcmBuffer.indices) {
                    val chordIdx = ((sampleIndex / samplesPerChord) % chordProgressions.size).toInt()
                    val chord = chordProgressions[chordIdx]
                    val t = sampleIndex.toDouble() / sampleRate

                    // Synthesize warm harmonics with subtle tremolo & warmth
                    var sampleVal = 0.0
                    for (freq in chord) {
                        val tremolo = 0.85 + 0.15 * sin(2.0 * PI * 2.5 * t)
                        val fundamental = sin(2.0 * PI * freq * t)
                        val overtone1 = 0.3 * sin(2.0 * PI * freq * 2.0 * t)
                        val overtone2 = 0.1 * sin(2.0 * PI * freq * 3.0 * t)
                        sampleVal += (fundamental + overtone1 + overtone2) * tremolo
                    }
                    sampleVal /= chord.size

                    // Add soft vinyl crackle / texture
                    if (currentTrack == MusicTrack.MIDNIGHT_LOFI) {
                        val crackle = if (Random.nextFloat() < 0.008f) (Random.nextDouble() - 0.5) * 0.15 else 0.0
                        sampleVal += crackle
                    }

                    // Soft Lo-Fi beat rhythmic pulse every 0.8 sec (75 BPM)
                    val beatPeriod = (sampleRate * 0.8).toLong()
                    val beatOffset = sampleIndex % beatPeriod
                    if (beatOffset < sampleRate * 0.06 && currentTrack != MusicTrack.ACOUSTIC_ZEN) {
                        val kickEnv = 1.0 - (beatOffset.toDouble() / (sampleRate * 0.06))
                        val kickTone = sin(2.0 * PI * 65.0 * (beatOffset.toDouble() / sampleRate))
                        sampleVal += kickTone * kickEnv * 0.35
                    }

                    val clamped = (sampleVal * 10000.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
                    pcmBuffer[i] = clamped
                    sampleIndex++
                }

                track.write(pcmBuffer, 0, pcmBuffer.size)
            }
        } catch (e: Exception) {
            Log.e("LoFiSynth", "Error in synth loop", e)
        } finally {
            try {
                audioTrack?.stop()
                audioTrack?.release()
                audioTrack = null
            } catch (_: Exception) {}
        }
    }
}
