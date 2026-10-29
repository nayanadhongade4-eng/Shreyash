package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoScene
import com.example.model.VisualType
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NoteFlixBorder
import com.example.ui.theme.NoteFlixSurfaceCard
import com.example.ui.theme.NoteFlixSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletSecondary

@Composable
fun TimelineTrack(
    scenes: List<VideoScene>,
    activeSceneIndex: Int,
    activeSceneProgress: Float,
    onSelectScene: (Int) -> Unit,
    onAddScene: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(activeSceneIndex) {
        if (activeSceneIndex in scenes.indices) {
            listState.animateScrollToItem(activeSceneIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NoteFlixSurfaceElevated, RoundedCornerShape(14.dp))
            .border(1.dp, NoteFlixBorder, RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 10.dp)
            .testTag("timeline_track_container")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CrimsonPrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "STUDIO TIMELINE",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = "${scenes.size} Scenes • ${scenes.sumOf { it.durationSeconds }}s total",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(scenes) { index, scene ->
                val isActive = index == activeSceneIndex
                val borderColor by animateColorAsState(
                    targetValue = if (isActive) CrimsonPrimary else NoteFlixBorder,
                    label = "border_color"
                )

                Box(
                    modifier = Modifier
                        .width(138.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isActive) Color(0xFF2C1935) else NoteFlixSurfaceCard)
                        .border(if (isActive) 2.dp else 1.dp, borderColor, RoundedCornerShape(10.dp))
                        .clickable { onSelectScene(index) }
                        .padding(8.dp)
                        .testTag("timeline_scene_item_$index")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Scene number badge
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isActive) CrimsonPrimary else Color.White.copy(alpha = 0.12f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "#${index + 1}",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Visual type icon
                            val icon = when (scene.visualType) {
                                VisualType.ANIMATED_CARTOON -> Icons.Default.SmartToy
                                VisualType.DYNAMIC_GRAPH -> Icons.Default.BarChart
                                VisualType.MIND_MAP -> Icons.Default.Hub
                                VisualType.KINETIC_TYPOGRAPHY -> Icons.Default.TextFields
                                VisualType.CODE_BREAKDOWN -> Icons.Default.Terminal
                                VisualType.INFOGRAPHIC_CARD -> Icons.Default.ViewCarousel
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isActive) AmberAccent else CyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = scene.title,
                            color = if (isActive) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${scene.durationSeconds}s",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = scene.visualType.displayName.take(10),
                                color = VioletSecondary,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }

                        // Progress scrubber inside active card
                        if (isActive) {
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { activeSceneProgress.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.5.dp)
                                    .clip(RoundedCornerShape(1.dp)),
                                color = CrimsonPrimary,
                                trackColor = Color.White.copy(alpha = 0.15f)
                            )
                        }
                    }
                }
            }

            // Add Scene Card
            item {
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(78.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NoteFlixSurfaceCard.copy(alpha = 0.6f))
                        .border(1.dp, NoteFlixBorder, RoundedCornerShape(10.dp))
                        .clickable { onAddScene() }
                        .padding(8.dp)
                        .testTag("add_scene_timeline_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Scene",
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Add Scene",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
