package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.VideoProject
import com.example.ui.screens.InputNotesScreen
import com.example.ui.screens.SavedProjectsSheet
import com.example.ui.screens.StudioDashboardScreen
import com.example.ui.theme.NoteFlixBgDark
import com.example.ui.theme.NoteFlixTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.NoteFlixViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: NoteFlixViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase App Check contract wiring
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            NoteFlixTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = NoteFlixBgDark,
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    NoteFlixAppRoot(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NoteFlixAppRoot(
    viewModel: NoteFlixViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentProject by viewModel.currentProject.collectAsStateWithLifecycle()
    val savedProjects by viewModel.savedProjects.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentSceneIndex by viewModel.currentSceneIndex.collectAsStateWithLifecycle()
    val sceneProgressFraction by viewModel.sceneProgressFraction.collectAsStateWithLifecycle()
    val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()

    var showSavedProjectsSheet by remember { mutableStateOf(false) }

    if (showSavedProjectsSheet) {
        SavedProjectsSheet(
            projects = savedProjects,
            onSelectProject = { proj ->
                viewModel.selectProject(proj)
            },
            onDeleteProject = { id ->
                viewModel.deleteProject(id)
            },
            onDismiss = { showSavedProjectsSheet = false }
        )
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition",
        modifier = modifier.fillMaxSize()
    ) { screen ->
        when (screen) {
            AppScreen.INPUT -> {
                InputNotesScreen(
                    isGenerating = isGenerating,
                    onGenerate = { title, content ->
                        viewModel.generateProject(title, content)
                    },
                    onOpenLibrary = {
                        showSavedProjectsSheet = true
                    }
                )
            }

            AppScreen.STUDIO -> {
                val project = currentProject
                if (project != null) {
                    StudioDashboardScreen(
                        project = project,
                        currentSceneIndex = currentSceneIndex,
                        isPlaying = isPlaying,
                        sceneProgressFraction = sceneProgressFraction,
                        playbackSpeed = playbackSpeed,
                        onTogglePlay = { viewModel.togglePlay() },
                        onSelectScene = { idx -> viewModel.selectScene(idx) },
                        onPrevScene = { viewModel.prevScene() },
                        onNextScene = { viewModel.nextScene() },
                        onSpeedSelected = { speed -> viewModel.setPlaybackSpeed(speed) },
                        onUpdateScene = { scene -> viewModel.updateActiveScene(scene) },
                        onUpdateAudio = { audio -> viewModel.updateAudioSettings(audio) },
                        onAddScene = { viewModel.addScene() },
                        onPreviewVoice = { voice -> viewModel.previewVoice(voice) },
                        onAiEnhanceScript = { viewModel.aiEnhanceActiveScene() },
                        onSaveProject = { viewModel.saveProject() },
                        onBackToInput = { viewModel.navigateToInput() }
                    )
                } else {
                    // Fallback to Input if no project loaded
                    viewModel.navigateToInput()
                }
            }
        }
    }
}
