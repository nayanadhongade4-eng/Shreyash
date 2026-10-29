package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoScene
import com.example.model.VisualType
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.NoteFlixSurfaceCard
import com.example.ui.theme.NoteFlixSurfaceDark
import com.example.ui.theme.NoteFlixSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletSecondary

@Composable
fun SceneVisualRenderer(
    scene: VideoScene,
    progressFraction: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NoteFlixSurfaceDark,
                        Color(0xFF130F1F),
                        Color(0xFF0D0A16)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        when (scene.visualType) {
            VisualType.ANIMATED_CARTOON -> {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CartoonMascotCanvas(
                        pose = scene.mascotPose,
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxSize()
                    )

                    Column(
                        modifier = Modifier
                            .weight(1.3f)
                            .padding(start = 8.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(CrimsonPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .border(1.dp, CrimsonPrimary, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "CORE CONCEPT",
                                color = CrimsonPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = scene.title,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        scene.keyPoints.take(3).forEachIndexed { i, point ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = point,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            VisualType.DYNAMIC_GRAPH -> {
                DynamicGraphCanvas(
                    values = scene.graphValues,
                    labels = scene.graphLabels,
                    modifier = Modifier.fillMaxSize()
                )
            }

            VisualType.MIND_MAP -> {
                MindMapCanvas(
                    centerTopic = scene.title,
                    branches = scene.keyPoints,
                    modifier = Modifier.fillMaxSize()
                )
            }

            VisualType.KINETIC_TYPOGRAPHY -> {
                KineticTypographySlide(
                    title = scene.title,
                    keyword = scene.highlightKeyword,
                    keyPoints = scene.keyPoints,
                    progress = progressFraction
                )
            }

            VisualType.CODE_BREAKDOWN -> {
                CodeTerminalSlide(
                    title = scene.title,
                    codeSnippet = scene.codeSnippet,
                    progress = progressFraction
                )
            }

            VisualType.INFOGRAPHIC_CARD -> {
                InfographicCardSlide(
                    title = scene.title,
                    keyword = scene.highlightKeyword,
                    keyPoints = scene.keyPoints,
                    progress = progressFraction
                )
            }
        }
    }
}

@Composable
fun KineticTypographySlide(
    title: String,
    keyword: String,
    keyPoints: List<String>,
    progress: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "kinetic_anim")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .background(VioletSecondary.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                .border(1.dp, VioletSecondary, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AmberAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "KEY TAKEAWAY",
                    color = AmberAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = keyword.uppercase(),
            color = CrimsonPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp,
            modifier = Modifier.scale(scalePulse)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            keyPoints.take(2).forEach { pt ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .background(NoteFlixSurfaceCard, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = pt,
                        color = CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CodeTerminalSlide(
    title: String,
    codeSnippet: String,
    progress: Float
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Window Title Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1B1527), RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                Spacer(modifier = Modifier.width(6.dp))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                Spacer(modifier = Modifier.width(6.dp))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "studio://script.kt",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Code Body
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF0F0B18), RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .border(1.dp, NoteFlixSurfaceCard, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .padding(12.dp)
        ) {
            val lines = codeSnippet.lines()
            val visibleCount = ((progress * lines.size * 1.5f).toInt() + 1).coerceAtMost(lines.size)

            Column {
                lines.take(visibleCount).forEachIndexed { index, line ->
                    Row {
                        Text(
                            text = "${index + 1}  ",
                            color = TextMuted.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        val textColor = when {
                            line.trim().startsWith("//") -> EmeraldAccent
                            line.contains("val ") || line.contains("fun ") -> VioletSecondary
                            line.contains("render") || line.contains("synthesize") -> CrimsonPrimary
                            else -> CyanAccent
                        }
                        Text(
                            text = line,
                            color = textColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfographicCardSlide(
    title: String,
    keyword: String,
    keyPoints: List<String>,
    progress: Float
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Metric Badge Card
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF2E1C3D), Color(0xFF160E21))
                    ),
                    RoundedCornerShape(16.dp)
                )
                .border(1.5.dp, VioletSecondary, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "RETENTION BOOST",
                    color = AmberAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "10x",
                    color = CrimsonPrimary,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Audio-Visual Memory",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1.2f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            keyPoints.take(2).forEach { pt ->
                Text(
                    text = "• $pt",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}
