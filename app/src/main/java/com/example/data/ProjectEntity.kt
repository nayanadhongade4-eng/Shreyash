package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.model.AudioMixSettings
import com.example.model.MascotPose
import com.example.model.MusicTrack
import com.example.model.VideoProject
import com.example.model.VideoScene
import com.example.model.VisualType
import com.example.model.VoiceActor
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "video_projects")
@TypeConverters(ProjectConverters::class)
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val sourceNoteTitle: String,
    val originalNoteText: String,
    val scenesJson: String,
    val audioSettingsJson: String,
    val createdAtEpoch: Long,
    val lastModifiedEpoch: Long
)

class ProjectConverters {

    @TypeConverter
    fun fromScenesList(scenes: List<VideoScene>): String {
        val array = JSONArray()
        scenes.forEach { scene ->
            val obj = JSONObject().apply {
                put("id", scene.id)
                put("sceneNumber", scene.sceneNumber)
                put("title", scene.title)
                put("narration", scene.narration)
                put("durationSeconds", scene.durationSeconds)
                put("visualType", scene.visualType.name)
                put("mascotPose", scene.mascotPose.name)
                put("highlightKeyword", scene.highlightKeyword)
                put("codeSnippet", scene.codeSnippet)

                val pointsArr = JSONArray()
                scene.keyPoints.forEach { pointsArr.put(it) }
                put("keyPoints", pointsArr)

                val valsArr = JSONArray()
                scene.graphValues.forEach { valsArr.put(it.toDouble()) }
                put("graphValues", valsArr)

                val labelsArr = JSONArray()
                scene.graphLabels.forEach { labelsArr.put(it) }
                put("graphLabels", labelsArr)
            }
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toScenesList(jsonStr: String): List<VideoScene> {
        if (jsonStr.isBlank()) return emptyList()
        val list = mutableListOf<VideoScene>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val keyPoints = mutableListOf<String>()
                val pointsArr = obj.optJSONArray("keyPoints")
                if (pointsArr != null) {
                    for (p in 0 until pointsArr.length()) {
                        keyPoints.add(pointsArr.getString(p))
                    }
                }

                val graphValues = mutableListOf<Float>()
                val valsArr = obj.optJSONArray("graphValues")
                if (valsArr != null) {
                    for (v in 0 until valsArr.length()) {
                        graphValues.add(valsArr.getDouble(v).toFloat())
                    }
                }

                val graphLabels = mutableListOf<String>()
                val labelsArr = obj.optJSONArray("graphLabels")
                if (labelsArr != null) {
                    for (l in 0 until labelsArr.length()) {
                        graphLabels.add(labelsArr.getString(l))
                    }
                }

                list.add(
                    VideoScene(
                        id = obj.optString("id", i.toString()),
                        sceneNumber = obj.optInt("sceneNumber", i + 1),
                        title = obj.optString("title", "Scene ${i + 1}"),
                        narration = obj.optString("narration", ""),
                        durationSeconds = obj.optInt("durationSeconds", 8),
                        visualType = try {
                            VisualType.valueOf(obj.optString("visualType", VisualType.ANIMATED_CARTOON.name))
                        } catch (_: Exception) {
                            VisualType.ANIMATED_CARTOON
                        },
                        mascotPose = try {
                            MascotPose.valueOf(obj.optString("mascotPose", MascotPose.EXPLAINING.name))
                        } catch (_: Exception) {
                            MascotPose.EXPLAINING
                        },
                        keyPoints = keyPoints,
                        graphValues = graphValues.ifEmpty { listOf(25f, 50f, 75f, 100f) },
                        graphLabels = graphLabels.ifEmpty { listOf("1", "2", "3", "4") },
                        codeSnippet = obj.optString("codeSnippet", ""),
                        highlightKeyword = obj.optString("highlightKeyword", "Key Insight")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    @TypeConverter
    fun fromAudioSettings(settings: AudioMixSettings): String {
        return JSONObject().apply {
            put("selectedVoice", settings.selectedVoice.name)
            put("voiceVolume", settings.voiceVolume.toDouble())
            put("musicTrack", settings.musicTrack.name)
            put("musicVolume", settings.musicVolume.toDouble())
            put("pitchModifier", settings.pitchModifier.toDouble())
            put("speechRateModifier", settings.speechRateModifier.toDouble())
            put("focusBoostEnabled", settings.focusBoostEnabled)
        }.toString()
    }

    @TypeConverter
    fun toAudioSettings(jsonStr: String): AudioMixSettings {
        if (jsonStr.isBlank()) return AudioMixSettings()
        return try {
            val obj = JSONObject(jsonStr)
            AudioMixSettings(
                selectedVoice = try {
                    VoiceActor.valueOf(obj.optString("selectedVoice", VoiceActor.NOVA.name))
                } catch (_: Exception) {
                    VoiceActor.NOVA
                },
                voiceVolume = obj.optDouble("voiceVolume", 0.95).toFloat(),
                musicTrack = try {
                    MusicTrack.valueOf(obj.optString("musicTrack", MusicTrack.MIDNIGHT_LOFI.name))
                } catch (_: Exception) {
                    MusicTrack.MIDNIGHT_LOFI
                },
                musicVolume = obj.optDouble("musicVolume", 0.35).toFloat(),
                pitchModifier = obj.optDouble("pitchModifier", 1.0).toFloat(),
                speechRateModifier = obj.optDouble("speechRateModifier", 1.0).toFloat(),
                focusBoostEnabled = obj.optBoolean("focusBoostEnabled", true)
            )
        } catch (_: Exception) {
            AudioMixSettings()
        }
    }
}

fun VideoProject.toEntity(): ProjectEntity {
    val conv = ProjectConverters()
    return ProjectEntity(
        id = id,
        title = title,
        sourceNoteTitle = sourceNoteTitle,
        originalNoteText = originalNoteText,
        scenesJson = conv.fromScenesList(scenes),
        audioSettingsJson = conv.fromAudioSettings(audioSettings),
        createdAtEpoch = createdAtEpoch,
        lastModifiedEpoch = lastModifiedEpoch
    )
}

fun ProjectEntity.toProject(): VideoProject {
    val conv = ProjectConverters()
    return VideoProject(
        id = id,
        title = title,
        sourceNoteTitle = sourceNoteTitle,
        originalNoteText = originalNoteText,
        scenes = conv.toScenesList(scenesJson),
        audioSettings = conv.toAudioSettings(audioSettingsJson),
        createdAtEpoch = createdAtEpoch,
        lastModifiedEpoch = lastModifiedEpoch
    )
}
