package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VioletSecondary

@Composable
fun DynamicGraphCanvas(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "graph_anim")
    val growAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "graph_grow"
    )

    val maxVal = (values.maxOrNull() ?: 100f).coerceAtLeast(10f)

    Column(
        modifier = modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw neon grid lines
                val gridSteps = 4
                for (i in 0..gridSteps) {
                    val y = h - (h / gridSteps * i)
                    drawLine(
                        color = Color.White.copy(alpha = 0.08f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 2f
                    )
                }

                if (values.isEmpty()) return@Canvas

                val barWidth = (w / (values.size * 2f)).coerceIn(24f, 64f)
                val spacing = w / (values.size + 1f)

                // Draw connected trend line
                val trendPath = Path()
                val points = mutableListOf<Offset>()

                values.forEachIndexed { index, value ->
                    val x = spacing * (index + 1)
                    val barH = (value / maxVal) * (h * 0.78f) * growAnim
                    val y = h - barH
                    points.add(Offset(x, y))

                    if (index == 0) {
                        trendPath.moveTo(x, y)
                    } else {
                        trendPath.lineTo(x, y)
                    }

                    // Bar with gradient
                    val barBrush = Brush.verticalGradient(
                        colors = listOf(
                            if (index % 2 == 0) CrimsonPrimary else VioletSecondary,
                            CyanAccent.copy(alpha = 0.6f)
                        )
                    )

                    drawRoundRect(
                        brush = barBrush,
                        topLeft = Offset(x - barWidth / 2f, y),
                        size = Size(barWidth, barH),
                        cornerRadius = CornerRadius(12f, 12f)
                    )

                    // Glowing bar cap
                    drawCircle(
                        color = AmberAccent,
                        radius = 6f,
                        center = Offset(x, y)
                    )
                }

                // Draw luminous curve stroke
                drawPath(
                    path = trendPath,
                    color = CyanAccent.copy(alpha = 0.9f),
                    style = Stroke(width = 4f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Labels row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            labels.take(values.size).forEachIndexed { i, label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(values.getOrElse(i) { 0f } * growAnim).toInt()}%",
                        color = AmberAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = label,
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
