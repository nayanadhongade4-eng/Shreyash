package com.example.engine

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.AudioMixSettings
import com.example.model.VoiceActor
import java.util.Locale

class AudioNarrationManager(
    private val context: Context,
    private val onUtteranceStarted: (String) -> Unit = {},
    private val onUtteranceCompleted: (String) -> Unit = {},
    private val onError: (String) -> Unit = {}
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val loFiSynthesizer = LoFiBeatSynthesizer()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let { engine ->
                    val result = engine.setLanguage(Locale.US)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.w("AudioNarration", "US English missing or not supported, using default locale")
                        engine.language = Locale.getDefault()
                    }
                    isInitialized = true
                    setupUtteranceListener()
                }
            } else {
                Log.e("AudioNarration", "TTS initialization failed: $status")
                onError("TTS initialization failed: $status")
            }
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                utteranceId?.let { onUtteranceStarted(it) }
            }

            override fun onDone(utteranceId: String?) {
                utteranceId?.let { onUtteranceCompleted(it) }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                onError("Speech error in utterance: $utteranceId")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                onError("Speech error in utterance $utteranceId (code: $errorCode)")
            }
        })
    }

    fun speakSceneNarration(
        utteranceId: String,
        narration: String,
        settings: AudioMixSettings
    ) {
        if (!isInitialized || tts == null) {
            Log.w("AudioNarration", "TTS not initialized yet")
            return
        }

        applySettings(settings)

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, settings.voiceVolume.coerceIn(0f, 1f))
        }

        tts?.speak(narration, TextToSpeech.QUEUE_FLUSH, params, utteranceId)

        // Ensure background music is running with current settings
        loFiSynthesizer.start(settings.musicTrack, settings.musicVolume)
    }

    fun applySettings(settings: AudioMixSettings) {
        tts?.let { engine ->
            val voice = settings.selectedVoice
            val finalPitch = (voice.pitch * settings.pitchModifier).coerceIn(0.5f, 2.0f)
            val finalRate = (voice.speechRate * settings.speechRateModifier).coerceIn(0.5f, 2.0f)
            engine.setPitch(finalPitch)
            engine.setSpeechRate(finalRate)
        }
        loFiSynthesizer.setVolume(settings.musicVolume)
    }

    fun updateMusic(settings: AudioMixSettings) {
        loFiSynthesizer.start(settings.musicTrack, settings.musicVolume)
    }

    fun pauseOrStopSpeech() {
        tts?.stop()
    }

    fun stopAll() {
        tts?.stop()
        loFiSynthesizer.stop()
    }

    fun release() {
        try {
            stopAll()
            tts?.shutdown()
            tts = null
        } catch (e: Exception) {
            Log.e("AudioNarration", "Error releasing AudioNarrationManager", e)
        }
    }
}
