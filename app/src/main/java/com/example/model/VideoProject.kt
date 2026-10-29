package com.example.model

data class VideoProject(
    val id: String,
    val title: String,
    val sourceNoteTitle: String,
    val originalNoteText: String,
    val scenes: List<VideoScene>,
    val audioSettings: AudioMixSettings = AudioMixSettings(),
    val createdAtEpoch: Long = System.currentTimeMillis(),
    val lastModifiedEpoch: Long = System.currentTimeMillis()
) {
    val totalDurationSeconds: Int
        get() = scenes.sumOf { it.durationSeconds }
}
