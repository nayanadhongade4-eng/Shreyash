package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.NoteFlixDatabase
import com.example.data.SampleNotesData
import com.example.data.toEntity
import com.example.data.toProject
import com.example.engine.AudioNarrationManager
import com.example.engine.ScriptGeneratorEngine
import com.example.model.AudioMixSettings
import com.example.model.MascotPose
import com.example.model.VideoProject
import com.example.model.VideoScene
import com.example.model.VisualType
import com.example.model.VoiceActor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    INPUT,
    STUDIO
}

class NoteFlixViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NoteFlixDatabase.getDatabase(application)
    private val projectDao = db.projectDao()

    private val _currentScreen = MutableStateFlow(AppScreen.INPUT)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentProject = MutableStateFlow<VideoProject?>(null)
    val currentProject: StateFlow<VideoProject?> = _currentProject.asStateFlow()

    private val _savedProjects = MutableStateFlow<List<VideoProject>>(emptyList())
    val savedProjects: StateFlow<List<VideoProject>> = _savedProjects.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Playback state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSceneIndex = MutableStateFlow(0)
    val currentSceneIndex: StateFlow<Int> = _currentSceneIndex.asStateFlow()

    private val _sceneProgressFraction = MutableStateFlow(0f)
    val sceneProgressFraction: StateFlow<Float> = _sceneProgressFraction.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private var playbackTickerJob: Job? = null

    // Audio Narration Manager
    private val audioManager = AudioNarrationManager(
        context = application,
        onUtteranceStarted = {},
        onUtteranceCompleted = { utteranceId ->
            // If playing, we can advance or let ticker handle completion
        },
        onError = {}
    )

    init {
        // Observe saved projects from Room
        viewModelScope.launch {
            projectDao.getAllProjectsFlow().collectLatest { entities ->
                _savedProjects.value = entities.map { it.toProject() }
            }
        }
    }

    fun generateProject(title: String, rawContent: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            stopPlayback()

            // Realistic synthesis delay
            delay(800)

            val project = ScriptGeneratorEngine.generateVideoFromNotes(title, rawContent)
            _currentProject.value = project
            _currentSceneIndex.value = 0
            _sceneProgressFraction.value = 0f
            _isGenerating.value = false
            _currentScreen.value = AppScreen.STUDIO

            // Automatically persist newly generated project
            projectDao.insertOrUpdate(project.toEntity())

            // Start playing immediately with audio synthesis
            startPlayback()
        }
    }

    fun selectProject(project: VideoProject) {
        stopPlayback()
        _currentProject.value = project
        _currentSceneIndex.value = 0
        _sceneProgressFraction.value = 0f
        _currentScreen.value = AppScreen.STUDIO
    }

    fun navigateToInput() {
        stopPlayback()
        _currentScreen.value = AppScreen.INPUT
    }

    fun togglePlay() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        val proj = _currentProject.value ?: return
        if (proj.scenes.isEmpty()) return

        _isPlaying.value = true
        speakCurrentScene()
        launchTicker()
    }

    private fun pausePlayback() {
        _isPlaying.value = false
        playbackTickerJob?.cancel()
        playbackTickerJob = null
        audioManager.stopAll()
    }

    fun stopPlayback() {
        _isPlaying.value = false
        playbackTickerJob?.cancel()
        playbackTickerJob = null
        _sceneProgressFraction.value = 0f
        audioManager.stopAll()
    }

    private fun speakCurrentScene() {
        val proj = _currentProject.value ?: return
        val scene = proj.scenes.getOrNull(_currentSceneIndex.value) ?: return
        val settings = proj.audioSettings

        audioManager.speakSceneNarration(
            utteranceId = "scene_${scene.id}",
            narration = scene.narration,
            settings = settings
        )
    }

    private fun launchTicker() {
        playbackTickerJob?.cancel()
        playbackTickerJob = viewModelScope.launch {
            val updateIntervalMs = 50L
            while (_isPlaying.value) {
                delay(updateIntervalMs)
                val proj = _currentProject.value ?: break
                val scene = proj.scenes.getOrNull(_currentSceneIndex.value) ?: break
                val durationMs = (scene.durationSeconds * 1000f) / _playbackSpeed.value

                val step = updateIntervalMs / durationMs
                val nextProgress = _sceneProgressFraction.value + step

                if (nextProgress >= 1.0f) {
                    // Advance to next scene or loop/finish
                    if (_currentSceneIndex.value < proj.scenes.size - 1) {
                        _currentSceneIndex.value += 1
                        _sceneProgressFraction.value = 0f
                        speakCurrentScene()
                    } else {
                        // End of reel: loop back or stop
                        _currentSceneIndex.value = 0
                        _sceneProgressFraction.value = 0f
                        _isPlaying.value = false
                        audioManager.stopAll()
                        break
                    }
                } else {
                    _sceneProgressFraction.value = nextProgress
                }
            }
        }
    }

    fun selectScene(index: Int) {
        val proj = _currentProject.value ?: return
        if (index in proj.scenes.indices) {
            _currentSceneIndex.value = index
            _sceneProgressFraction.value = 0f
            if (_isPlaying.value) {
                speakCurrentScene()
            }
        }
    }

    fun nextScene() {
        val proj = _currentProject.value ?: return
        if (_currentSceneIndex.value < proj.scenes.size - 1) {
            selectScene(_currentSceneIndex.value + 1)
        }
    }

    fun prevScene() {
        if (_currentSceneIndex.value > 0) {
            selectScene(_currentSceneIndex.value - 1)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        val proj = _currentProject.value
        if (proj != null) {
            audioManager.applySettings(proj.audioSettings.copy(speechRateModifier = speed))
        }
    }

    fun updateActiveScene(updatedScene: VideoScene) {
        val proj = _currentProject.value ?: return
        val currentIdx = _currentSceneIndex.value
        if (currentIdx in proj.scenes.indices) {
            val updatedScenes = proj.scenes.toMutableList()
            updatedScenes[currentIdx] = updatedScene
            val updatedProject = proj.copy(
                scenes = updatedScenes,
                lastModifiedEpoch = System.currentTimeMillis()
            )
            _currentProject.value = updatedProject
            saveProject(updatedProject)

            if (_isPlaying.value) {
                speakCurrentScene()
            }
        }
    }

    fun updateAudioSettings(settings: AudioMixSettings) {
        val proj = _currentProject.value ?: return
        val updatedProject = proj.copy(
            audioSettings = settings,
            lastModifiedEpoch = System.currentTimeMillis()
        )
        _currentProject.value = updatedProject
        audioManager.applySettings(settings)
        audioManager.updateMusic(settings)
        saveProject(updatedProject)
    }

    fun addScene() {
        val proj = _currentProject.value ?: return
        val nextNum = proj.scenes.size + 1
        val newScene = VideoScene(
            id = UUID.randomUUID().toString(),
            sceneNumber = nextNum,
            title = "Key Insight $nextNum",
            narration = "In this section, we dive deeper into practical execution and measurable impact.",
            durationSeconds = 9,
            visualType = VisualType.ANIMATED_CARTOON,
            mascotPose = MascotPose.EXPLAINING,
            keyPoints = listOf("Strategic Insight", "Action Plan", "Outcome Verification"),
            highlightKeyword = "Insight $nextNum"
        )
        val updatedScenes = proj.scenes + newScene
        val updatedProject = proj.copy(
            scenes = updatedScenes,
            lastModifiedEpoch = System.currentTimeMillis()
        )
        _currentProject.value = updatedProject
        saveProject(updatedProject)
        _currentSceneIndex.value = updatedScenes.size - 1
        _sceneProgressFraction.value = 0f
    }

    fun previewVoice(actor: VoiceActor) {
        val proj = _currentProject.value ?: return
        val tempSettings = proj.audioSettings.copy(selectedVoice = actor)
        audioManager.speakSceneNarration(
            utteranceId = "preview_${actor.id}",
            narration = "Hello! I am ${actor.actorName}. Ready to narrate your NoteFlix video.",
            settings = tempSettings
        )
    }

    fun aiEnhanceActiveScene() {
        val proj = _currentProject.value ?: return
        val currentIdx = _currentSceneIndex.value
        val scene = proj.scenes.getOrNull(currentIdx) ?: return

        // Smart script enhancement: punchier hooks and clear takeaways
        val enhancedNarration = when {
            scene.narration.startsWith("Notice that") -> scene.narration.replace("Notice that", "Crucially,")
            !scene.narration.contains("specifically") -> "${scene.narration.removeSuffix(".")} — specifically driving high retention and clarity."
            else -> "In summary: ${scene.narration}"
        }
        val enhancedPoints = if (scene.keyPoints.isEmpty()) {
            listOf("Foundation", "Core Mechanism", "Takeaway")
        } else {
            scene.keyPoints.map { "$it ★" }
        }

        updateActiveScene(
            scene.copy(
                narration = enhancedNarration,
                keyPoints = enhancedPoints,
                mascotPose = MascotPose.EUREKA
            )
        )
    }

    fun saveProject(project: VideoProject? = null) {
        val projToSave = project ?: _currentProject.value ?: return
        viewModelScope.launch {
            projectDao.insertOrUpdate(projToSave.toEntity())
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            projectDao.deleteById(projectId)
            if (_currentProject.value?.id == projectId) {
                stopPlayback()
                _currentProject.value = null
                _currentScreen.value = AppScreen.INPUT
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
        audioManager.release()
    }
}
