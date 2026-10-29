package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NoteFlixSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VioletSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MindMapCanvas(
    centerTopic: String,
    branches: List<String>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mindmap_anim")
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_rot"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    val displayBranches = branches.ifEmpty { listOf("Foundation", "Mechanism", "Application", "Future") }.take(4)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2f, h / 2f)
            val orbitRadius = (w.coerceAtMost(h) * 0.35f)

            // Draw orbit ring guide
            drawCircle(
                color = VioletSecondary.copy(alpha = 0.25f),
                radius = orbitRadius,
                center = center,
                style = Stroke(width = 2f)
            )

            // Central radiant aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CrimsonPrimary.copy(alpha = 0.45f * pulse), Color.Transparent),
                    center = center,
                    radius = orbitRadius * 0.7f
                ),
                radius = orbitRadius * 0.7f,
                center = center
            )

            // Connect central core to orbiting satellite nodes
            val stepAngle = 360f / displayBranches.size
            displayBranches.indices.forEach { i ->
                val angleDeg = (orbitAngle + i * stepAngle) % 360f
                val rad = Math.toRadians(angleDeg.toDouble())
                val nodePos = Offset(
                    (center.x + orbitRadius * cos(rad)).toFloat(),
                    (center.y + orbitRadius * sin(rad)).toFloat()
                )

                // Laser connection line
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(CrimsonPrimary, CyanAccent.copy(alpha = 0.7f)),
                        start = center,
                        end = nodePos
                    ),
                    start = center,
                    end = nodePos,
                    strokeWidth = 3f
                )

                // Satellite node circle
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(CyanAccent, VioletSecondary),
                        start = Offset(nodePos.x - 18f, nodePos.y - 18f),
                        end = Offset(nodePos.x + 18f, nodePos.y + 18f)
                    ),
                    radius = 20f * pulse,
                    center = nodePos
                )
                drawCircle(
                    color = Color.White,
                    radius = 20f * pulse,
                    center = nodePos,
                    style = Stroke(width = 3f)
                )
            }

            // Central Core Circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CrimsonPrimary, Color(0xFF8B002B)),
                    center = center,
                    radius = 48f * pulse
                ),
                radius = 48f * pulse,
                center = center
            )
            drawCircle(
                color = AmberAccent,
                radius = 48f * pulse,
                center = center,
                style = Stroke(width = 4f)
            )
        }

        // Center Title Badge
        Box(
            modifier = Modifier
                .padding(8.dp)
                .background(NoteFlixSurfaceElevated.copy(alpha = 0.92f), RoundedCornerShape(12.dp))
                .border(1.dp, CrimsonPrimary, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = centerTopic.take(18),
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
