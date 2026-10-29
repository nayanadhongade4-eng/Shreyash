package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioMixSettings
import com.example.model.MascotPose
import com.example.model.MusicTrack
import com.example.model.VideoScene
import com.example.model.VisualType
import com.example.model.VoiceActor
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.NoteFlixBorder
import com.example.ui.theme.NoteFlixSurfaceCard
import com.example.ui.theme.NoteFlixSurfaceDark
import com.example.ui.theme.NoteFlixSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletSecondary

@Composable
fun StudioEditorTabs(
    activeScene: VideoScene,
    audioSettings: AudioMixSettings,
    onUpdateScene: (VideoScene) -> Unit,
    onUpdateAudio: (AudioMixSettings) -> Unit,
    onPreviewVoice: (VoiceActor) -> Unit,
    onAiEnhanceScript: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Script", "Animation", "Voice Actor", "Lo-Fi Music")
    val tabIcons = listOf(
        Icons.Default.TextFields,
        Icons.Default.MovieFilter,
        Icons.Default.Mic,
        Icons.Default.MusicNote
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NoteFlixSurfaceElevated, RoundedCornerShape(16.dp))
            .border(1.dp, NoteFlixBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
            .testTag("studio_editor_tabs_container")
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = CrimsonPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = CrimsonPrimary,
                    height = 2.5.dp
                )
            },
            divider = {}
        ) {
            tabTitles.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier.testTag("editor_tab_$index"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = tabIcons[index],
                                contentDescription = null,
                                tint = if (isSelected) CrimsonPrimary else TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = title,
                                color = if (isSelected) TextPrimary else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        AnimatedContent(
            targetState = selectedTabIndex,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_content_anim"
        ) { tabIndex ->
            when (tabIndex) {
                0 -> ScriptEditorTab(
                    scene = activeScene,
                    onUpdateScene = onUpdateScene,
                    onAiEnhanceScript = onAiEnhanceScript
                )
                1 -> AnimationEditorTab(
                    scene = activeScene,
                    onUpdateScene = onUpdateScene
                )
                2 -> VoiceActorTab(
                    audioSettings = audioSettings,
                    onUpdateAudio = onUpdateAudio,
                    onPreviewVoice = onPreviewVoice
                )
                3 -> LoFiMusicTab(
                    audioSettings = audioSettings,
                    onUpdateAudio = onUpdateAudio
                )
            }
        }
    }
}

@Composable
fun ScriptEditorTab(
    scene: VideoScene,
    onUpdateScene: (VideoScene) -> Unit,
    onAiEnhanceScript: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Scene Headline",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            // AI Enhance Script Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2C1438))
                    .border(1.dp, VioletSecondary.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .clickable { onAiEnhanceScript() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("ai_enhance_script_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AI Enhance Script",
                        color = AmberAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = scene.title,
            onValueChange = { onUpdateScene(scene.copy(title = it)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("scene_title_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = CrimsonPrimary,
                unfocusedBorderColor = NoteFlixBorder,
                focusedContainerColor = NoteFlixSurfaceDark,
                unfocusedContainerColor = NoteFlixSurfaceDark
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Narration Voiceover",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            val wordCount = scene.narration.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
            Text(
                text = "$wordCount words • est. ${(wordCount / 2.5f).toInt().coerceAtLeast(4)}s",
                color = CyanAccent,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = scene.narration,
            onValueChange = {
                val words = it.split("\\s+".toRegex()).filter { w -> w.isNotBlank() }.size
                val dur = (words / 2.5f).toInt().coerceIn(5, 20)
                onUpdateScene(scene.copy(narration = it, durationSeconds = dur))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .testTag("scene_narration_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = CrimsonPrimary,
                unfocusedBorderColor = NoteFlixBorder,
                focusedContainerColor = NoteFlixSurfaceDark,
                unfocusedContainerColor = NoteFlixSurfaceDark
            ),
            maxLines = 4
        )
    }
}

@Composable
fun AnimationEditorTab(
    scene: VideoScene,
    onUpdateScene: (VideoScene) -> Unit
) {
    Column {
        Text(
            text = "Swap Animation Style",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Visual Types Grid
        val visualTypes = VisualType.entries
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            visualTypes.chunked(2).forEach { rowTypes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rowTypes.forEach { type ->
                        val isSelected = scene.visualType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF38153A) else NoteFlixSurfaceDark)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) CrimsonPrimary else NoteFlixBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onUpdateScene(scene.copy(visualType = type)) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("visual_type_${type.name}")
                        ) {
                            Column {
                                Text(
                                    text = type.displayName,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                Text(
                                    text = type.description,
                                    color = TextMuted,
                                    fontSize = 9.sp,
                                    lineHeight = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Mascot pose or keyword
        if (scene.visualType == VisualType.ANIMATED_CARTOON) {
            Text(
                text = "Mascot Expression & Pose",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MascotPose.entries.forEach { pose ->
                    val isPoseSelected = scene.mascotPose == pose
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isPoseSelected) VioletSecondary.copy(alpha = 0.35f) else NoteFlixSurfaceDark)
                            .border(
                                1.dp,
                                if (isPoseSelected) VioletSecondary else NoteFlixBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { onUpdateScene(scene.copy(mascotPose = pose)) }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pose.name.take(4),
                            color = if (isPoseSelected) AmberAccent else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Key Highlight Phrase",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = scene.highlightKeyword,
                onValueChange = { onUpdateScene(scene.copy(highlightKeyword = it)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = CrimsonPrimary,
                    unfocusedBorderColor = NoteFlixBorder,
                    focusedContainerColor = NoteFlixSurfaceDark,
                    unfocusedContainerColor = NoteFlixSurfaceDark
                ),
                singleLine = true
            )
        }
    }
}

@Composable
fun VoiceActorTab(
    audioSettings: AudioMixSettings,
    onUpdateAudio: (AudioMixSettings) -> Unit,
    onPreviewVoice: (VoiceActor) -> Unit
) {
    Column {
        Text(
            text = "Select AI Voice Actor",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        VoiceActor.entries.forEach { actor ->
            val isSelected = audioSettings.selectedVoice == actor
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color(0xFF33163C) else NoteFlixSurfaceDark)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) CrimsonPrimary else NoteFlixBorder,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onUpdateAudio(audioSettings.copy(selectedVoice = actor)) }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
                    .testTag("voice_actor_${actor.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = actor.avatarEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = actor.actorName,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = actor.style,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Preview Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { onPreviewVoice(actor) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("preview_voice_${actor.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Preview Voice",
                                tint = CyanAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "Test", color = CyanAccent, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Speech Rate Modifier
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Speech Pacing: ${(audioSettings.speechRateModifier * 100).toInt()}%",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Text(
                text = "Pitch: ${(audioSettings.pitchModifier * 100).toInt()}%",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        Slider(
            value = audioSettings.speechRateModifier,
            onValueChange = { onUpdateAudio(audioSettings.copy(speechRateModifier = it)) },
            valueRange = 0.75f..1.35f,
            colors = SliderDefaults.colors(
                thumbColor = CrimsonPrimary,
                activeTrackColor = CrimsonPrimary,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            ),
            modifier = Modifier.testTag("speech_rate_slider")
        )
    }
}

@Composable
fun LoFiMusicTab(
    audioSettings: AudioMixSettings,
    onUpdateAudio: (AudioMixSettings) -> Unit
) {
    Column {
        Text(
            text = "Background Focus Music",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        MusicTrack.entries.forEach { track ->
            val isSelected = audioSettings.musicTrack == track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color(0xFF231838) else NoteFlixSurfaceDark)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) CyanAccent else NoteFlixBorder,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onUpdateAudio(audioSettings.copy(musicTrack = track)) }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
                    .testTag("music_track_${track.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = track.iconEmoji, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = track.trackName,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${track.genre} • ${if (track.tempoBpm > 0) "${track.tempoBpm} BPM" else "Silent"}",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    if (isSelected && track != MusicTrack.MUTE) {
                        Box(
                            modifier = Modifier
                                .background(EmeraldAccent.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = EmeraldAccent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Music Volume Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Lo-Fi Music Volume: ${(audioSettings.musicVolume * 100).toInt()}%",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        Slider(
            value = audioSettings.musicVolume,
            onValueChange = { onUpdateAudio(audioSettings.copy(musicVolume = it)) },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = CyanAccent,
                activeTrackColor = CyanAccent,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            ),
            modifier = Modifier.testTag("music_volume_slider")
        )

        // Voice Volume Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Voice Narration Volume: ${(audioSettings.voiceVolume * 100).toInt()}%",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        Slider(
            value = audioSettings.voiceVolume,
            onValueChange = { onUpdateAudio(audioSettings.copy(voiceVolume = it)) },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = CrimsonPrimary,
                activeTrackColor = CrimsonPrimary,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            ),
            modifier = Modifier.testTag("voice_volume_slider")
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Focus Boost EQ toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = AmberAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Alpha Wave Focus EQ",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Enhances 70-80 BPM binaural rhythm",
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }
            }

            Switch(
                checked = audioSettings.focusBoostEnabled,
                onCheckedChange = { onUpdateAudio(audioSettings.copy(focusBoostEnabled = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AmberAccent,
                    checkedTrackColor = AmberAccent.copy(alpha = 0.3f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.DarkGray
                ),
                modifier = Modifier.testTag("focus_boost_switch")
            )
        }
    }
}
