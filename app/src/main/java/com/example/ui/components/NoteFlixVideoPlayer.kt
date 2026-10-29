package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoProject
import com.example.model.VideoScene
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.NoteFlixBorder
import com.example.ui.theme.NoteFlixSurfaceCard
import com.example.ui.theme.NoteFlixSurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletSecondary
import java.util.Locale

@Composable
fun NoteFlixVideoPlayer(
    project: VideoProject,
    currentSceneIndex: Int,
    isPlaying: Boolean,
    sceneProgressFraction: Float,
    playbackSpeed: Float,
    isFullscreen: Boolean,
    onTogglePlay: () -> Unit,
    onPrevScene: () -> Unit,
    onNextScene: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onToggleFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentScene: VideoScene? = project.scenes.getOrNull(currentSceneIndex)
    var showControls by remember { mutableStateOf(true) }
    var showSpeedMenu by remember { mutableStateOf(false) }

    val totalDuration = project.totalDurationSeconds
    val elapsedPriorSeconds = project.scenes.take(currentSceneIndex).sumOf { it.durationSeconds }
    val currentSceneElapsed = ((currentScene?.durationSeconds ?: 8) * sceneProgressFraction).toInt()
    val totalElapsed = (elapsedPriorSeconds + currentSceneElapsed).coerceAtMost(totalDuration)

    val elapsedFormatted = String.format(Locale.US, "%02d:%02d", totalElapsed / 60, totalElapsed % 60)
    val totalFormatted = String.format(Locale.US, "%02d:%02d", totalDuration / 60, totalDuration % 60)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isFullscreen) Modifier.fillMaxSize()
                else Modifier.aspectRatio(16f / 9.5f)
            )
            .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 16.dp))
            .border(
                width = if (isFullscreen) 0.dp else 1.5.dp,
                brush = Brush.horizontalGradient(listOf(CrimsonPrimary.copy(alpha = 0.6f), VioletSecondary.copy(alpha = 0.6f))),
                shape = RoundedCornerShape(if (isFullscreen) 0.dp else 16.dp)
            )
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
            .testTag("video_player_container")
    ) {
        // Visual scene animations
        if (currentScene != null) {
            SceneVisualRenderer(
                scene = currentScene,
                progressFraction = sceneProgressFraction,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Overlay Subtitles (Captions)
        if (currentScene != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (showControls) 54.dp else 14.dp, start = 12.dp, end = 12.dp)
                    .background(Color.Black.copy(alpha = 0.82f), RoundedCornerShape(10.dp))
                    .border(0.8.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("closed_captions_box")
            ) {
                Text(
                    text = currentScene.narration,
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }

        // Top Control Bar Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Scene Badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(CrimsonPrimary, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "SCENE ${currentSceneIndex + 1}/${project.scenes.size}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Lo-Fi Beat Badge
                        Box(
                            modifier = Modifier
                                .background(NoteFlixSurfaceCard.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                                .border(0.5.dp, VioletSecondary.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${project.audioSettings.musicTrack.iconEmoji} ${project.audioSettings.musicTrack.trackName}",
                                    color = CyanAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Fullscreen Button
                    IconButton(
                        onClick = onToggleFullscreen,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("fullscreen_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Toggle Fullscreen",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Bottom Control Bar Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.92f))
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                // Continuous Progress Indicator
                val overallProgress = if (totalDuration > 0) totalElapsed.toFloat() / totalDuration else 0f
                LinearProgressIndicator(
                    progress = { overallProgress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.5.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .testTag("video_progress_bar"),
                    color = CrimsonPrimary,
                    trackColor = Color.White.copy(alpha = 0.2f),
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Playback buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onPrevScene,
                            enabled = currentSceneIndex > 0,
                            modifier = Modifier.size(36.dp).testTag("prev_scene_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastRewind,
                                contentDescription = "Previous Scene",
                                tint = if (currentSceneIndex > 0) Color.White else Color.Gray
                            )
                        }

                        // Primary Play / Pause button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CrimsonPrimary)
                                .clickable { onTogglePlay() }
                                .testTag("play_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = onNextScene,
                            enabled = currentSceneIndex < project.scenes.size - 1,
                            modifier = Modifier.size(36.dp).testTag("next_scene_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = "Next Scene",
                                tint = if (currentSceneIndex < project.scenes.size - 1) Color.White else Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Timecode
                        Text(
                            text = "$elapsedFormatted / $totalFormatted",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Right: Speed selector
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .clickable { showSpeedMenu = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("speed_selector_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Playback Speed",
                                tint = AmberAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${playbackSpeed}x",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showSpeedMenu,
                            onDismissRequest = { showSpeedMenu = false },
                            modifier = Modifier.background(NoteFlixSurfaceDark)
                        ) {
                            listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${speed}x",
                                            color = if (playbackSpeed == speed) CrimsonPrimary else TextPrimary,
                                            fontWeight = if (playbackSpeed == speed) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onSpeedSelected(speed)
                                        showSpeedMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
