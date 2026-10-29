package com.example.model

enum class VoiceActor(
    val id: String,
    val actorName: String,
    val style: String,
    val pitch: Float,
    val speechRate: Float,
    val avatarEmoji: String
) {
    NOVA("nova", "Nova", "Warm & Clear (Default)", 1.05f, 1.0f, "🎙️"),
    ECHO("echo", "Echo", "Deep & Cinematic Narrator", 0.78f, 0.95f, "🎬"),
    ALLOY("alloy", "Alloy", "Energetic & Fast-Paced", 1.15f, 1.10f, "⚡"),
    FABLE("fable", "Fable", "Imaginative & Storyteller", 0.95f, 0.92f, "📖"),
    ONYX("onyx", "Onyx", "Executive & Authoritative", 0.82f, 1.05f, "💼")
}

enum class MusicTrack(
    val id: String,
    val trackName: String,
    val genre: String,
    val tempoBpm: Int,
    val iconEmoji: String
) {
    MIDNIGHT_LOFI("midnight_lofi", "Midnight Lo-Fi", "Warm Rhodes & Vinyl Chill", 72, "☕"),
    CHILL_STUDY("chill_study", "Chill Study Beats", "Soft Melodic Dream", 80, "🎧"),
    SYNTHWAVE_FOCUS("synthwave_focus", "Synthwave Focus", "Analog Space Pad", 95, "🌌"),
    ACOUSTIC_ZEN("acoustic_zen", "Acoustic Zen", "Harmonic Ambient Resonance", 65, "🌿"),
    MUTE("mute", "Mute (Voice Only)", "No Background Audio", 0, "🔇")
}

data class AudioMixSettings(
    val selectedVoice: VoiceActor = VoiceActor.NOVA,
    val voiceVolume: Float = 0.95f,
    val musicTrack: MusicTrack = MusicTrack.MIDNIGHT_LOFI,
    val musicVolume: Float = 0.35f,
    val pitchModifier: Float = 1.0f,
    val speechRateModifier: Float = 1.0f,
    val focusBoostEnabled: Boolean = true
)
