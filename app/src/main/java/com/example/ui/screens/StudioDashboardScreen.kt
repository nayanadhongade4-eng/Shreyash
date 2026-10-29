package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioMixSettings
import com.example.model.VideoProject
import com.example.model.VideoScene
import com.example.model.VoiceActor
import com.example.ui.components.NoteFlixVideoPlayer
import com.example.ui.components.StudioEditorTabs
import com.example.ui.components.TimelineTrack
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NoteFlixBgDark
import com.example.ui.theme.NoteFlixBorder
import com.example.ui.theme.NoteFlixSurfaceDark
import com.example.ui.theme.NoteFlixSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletSecondary
import kotlinx.coroutines.launch

@Composable
fun StudioDashboardScreen(
    project: VideoProject,
    currentSceneIndex: Int,
    isPlaying: Boolean,
    sceneProgressFraction: Float,
    playbackSpeed: Float,
    onTogglePlay: () -> Unit,
    onSelectScene: (Int) -> Unit,
    onPrevScene: () -> Unit,
    onNextScene: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onUpdateScene: (VideoScene) -> Unit,
    onUpdateAudio: (AudioMixSettings) -> Unit,
    onAddScene: () -> Unit,
    onPreviewVoice: (VoiceActor) -> Unit,
    onAiEnhanceScript: () -> Unit,
    onSaveProject: () -> Unit,
    onBackToInput: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFullscreen by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val activeScene = project.scenes.getOrElse(currentSceneIndex) {
        project.scenes.firstOrNull() ?: VideoScene("temp", 1, "Default", "Default")
    }

    BackHandler {
        if (isFullscreen) {
            isFullscreen = false
        } else {
            onBackToInput()
        }
    }

    if (showExportDialog) {
        StoryboardExportDialog(
            project = project,
            onDismiss = { showExportDialog = false }
        )
    }

    // Fullscreen View
    if (isFullscreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("fullscreen_player_view")
        ) {
            NoteFlixVideoPlayer(
                project = project,
                currentSceneIndex = currentSceneIndex,
                isPlaying = isPlaying,
                sceneProgressFraction = sceneProgressFraction,
                playbackSpeed = playbackSpeed,
                isFullscreen = true,
                onTogglePlay = onTogglePlay,
                onPrevScene = onPrevScene,
                onNextScene = onNextScene,
                onSpeedSelected = onSpeedSelected,
                onToggleFullscreen = { isFullscreen = false }
            )

            IconButton(
                onClick = { isFullscreen = false },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .testTag("exit_fullscreen_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit Fullscreen",
                    tint = Color.White
                )
            }
        }
        return
    }

    // Adaptive All-Device Layout using BoxWithConstraints
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(NoteFlixBgDark)
    ) {
        val isWideScreen = maxWidth >= 720.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Studio Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NoteFlixSurfaceDark)
                    .border(0.5.dp, NoteFlixBorder)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackToInput,
                        modifier = Modifier.size(36.dp).testTag("back_to_notes_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Notes",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column {
                        Text(
                            text = project.title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Studio Mode • ${project.scenes.size} Scenes",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Export / Share Storyboard
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF281838))
                            .border(1.dp, VioletSecondary.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                            .clickable { showExportDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("open_export_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Export",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Save Project Button
                    IconButton(
                        onClick = {
                            onSaveProject()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Project saved successfully!")
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CrimsonPrimary)
                            .testTag("save_project_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Project",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Dual-Pane for Tablets / Foldables / Wide Screens
            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left Pane: Video Player
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                    ) {
                        NoteFlixVideoPlayer(
                            project = project,
                            currentSceneIndex = currentSceneIndex,
                            isPlaying = isPlaying,
                            sceneProgressFraction = sceneProgressFraction,
                            playbackSpeed = playbackSpeed,
                            isFullscreen = false,
                            onTogglePlay = onTogglePlay,
                            onPrevScene = onPrevScene,
                            onNextScene = onNextScene,
                            onSpeedSelected = onSpeedSelected,
                            onToggleFullscreen = { isFullscreen = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        TimelineTrack(
                            scenes = project.scenes,
                            activeSceneIndex = currentSceneIndex,
                            activeSceneProgress = sceneProgressFraction,
                            onSelectScene = onSelectScene,
                            onAddScene = onAddScene
                        )
                    }

                    // Right Pane: Studio Editor Tabs
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        StudioEditorTabs(
                            activeScene = activeScene,
                            audioSettings = project.audioSettings,
                            onUpdateScene = onUpdateScene,
                            onUpdateAudio = onUpdateAudio,
                            onPreviewVoice = onPreviewVoice,
                            onAiEnhanceScript = onAiEnhanceScript
                        )
                    }
                }
            } else {
                // Single Column Layout for Phones / Vertical Screens
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NoteFlixVideoPlayer(
                        project = project,
                        currentSceneIndex = currentSceneIndex,
                        isPlaying = isPlaying,
                        sceneProgressFraction = sceneProgressFraction,
                        playbackSpeed = playbackSpeed,
                        isFullscreen = false,
                        onTogglePlay = onTogglePlay,
                        onPrevScene = onPrevScene,
                        onNextScene = onNextScene,
                        onSpeedSelected = onSpeedSelected,
                        onToggleFullscreen = { isFullscreen = true }
                    )

                    TimelineTrack(
                        scenes = project.scenes,
                        activeSceneIndex = currentSceneIndex,
                        activeSceneProgress = sceneProgressFraction,
                        onSelectScene = onSelectScene,
                        onAddScene = onAddScene
                    )

                    StudioEditorTabs(
                        activeScene = activeScene,
                        audioSettings = project.audioSettings,
                        onUpdateScene = onUpdateScene,
                        onUpdateAudio = onUpdateAudio,
                        onPreviewVoice = onPreviewVoice,
                        onAiEnhanceScript = onAiEnhanceScript
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}
