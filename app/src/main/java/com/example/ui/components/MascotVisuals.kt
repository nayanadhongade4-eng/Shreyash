package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.example.model.MascotPose
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonLight
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.VioletSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CartoonMascotCanvas(
    pose: MascotPose,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_anim")

    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_float"
    )

    val blinkAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_blink"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val rotationArm by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arm_wave"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f
            val centerY = canvasH / 2f + floatAnim

            // Background soft ambient aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        VioletSecondary.copy(alpha = 0.35f * pulseGlow),
                        CrimsonPrimary.copy(alpha = 0.15f * pulseGlow),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = canvasW * 0.45f
                ),
                radius = canvasW * 0.45f,
                center = Offset(centerX, centerY)
            )

            val headRadius = canvasW * 0.22f
            val bodyWidth = canvasW * 0.36f
            val bodyHeight = canvasH * 0.32f

            // Antenna & Tech Halo
            val antennaTop = centerY - headRadius - (canvasH * 0.09f)
            drawLine(
                color = VioletSecondary,
                start = Offset(centerX, centerY - headRadius),
                end = Offset(centerX, antennaTop),
                strokeWidth = 6f
            )

            // Lightbulb / Beacon at top of antenna
            val beaconColor = if (pose == MascotPose.EUREKA) AmberAccent else CyanAccent
            drawCircle(
                color = beaconColor,
                radius = 16f * pulseGlow,
                center = Offset(centerX, antennaTop)
            )
            if (pose == MascotPose.EUREKA) {
                // Light rays
                for (angle in 0 until 360 step 45) {
                    val rad = Math.toRadians(angle.toDouble())
                    val rayStart = Offset(
                        centerX + (22f * cos(rad)).toFloat(),
                        antennaTop + (22f * sin(rad)).toFloat()
                    )
                    val rayEnd = Offset(
                        centerX + (38f * cos(rad)).toFloat(),
                        antennaTop + (38f * sin(rad)).toFloat()
                    )
                    drawLine(
                        color = AmberAccent.copy(alpha = pulseGlow),
                        start = rayStart,
                        end = rayEnd,
                        strokeWidth = 4f
                    )
                }
            }

            // Mascot Body (Rounded futuristic robot chassis)
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2E2448), Color(0xFF1E1732))
                ),
                topLeft = Offset(centerX - bodyWidth / 2f, centerY + headRadius * 0.7f),
                size = Size(bodyWidth, bodyHeight),
                cornerRadius = CornerRadius(24f, 24f)
            )
            // Body Outline
            drawRoundRect(
                color = VioletSecondary.copy(alpha = 0.8f),
                topLeft = Offset(centerX - bodyWidth / 2f, centerY + headRadius * 0.7f),
                size = Size(bodyWidth, bodyHeight),
                cornerRadius = CornerRadius(24f, 24f),
                style = Stroke(width = 4f)
            )

            // Chest screen badge (Play triangle or heart)
            val chestCenter = Offset(centerX, centerY + headRadius * 0.7f + bodyHeight * 0.45f)
            val badgeSize = 22f
            val path = Path().apply {
                moveTo(chestCenter.x - badgeSize * 0.6f, chestCenter.y - badgeSize)
                lineTo(chestCenter.x + badgeSize, chestCenter.y)
                lineTo(chestCenter.x - badgeSize * 0.6f, chestCenter.y + badgeSize)
                close()
            }
            drawPath(path, color = CrimsonPrimary)

            // Mascot Head (Sleek dark visor monitor)
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1F1A2E), Color(0xFF120E1C))
                ),
                radius = headRadius,
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = CrimsonPrimary.copy(alpha = 0.9f),
                radius = headRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 5f)
            )

            // Visor Glass
            val visorWidth = headRadius * 1.55f
            val visorHeight = headRadius * 0.85f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F0B18), Color(0xFF1B142B))
                ),
                topLeft = Offset(centerX - visorWidth / 2f, centerY - visorHeight / 2f),
                size = Size(visorWidth, visorHeight),
                cornerRadius = CornerRadius(18f, 18f)
            )

            // Glowing Eyes (Anime robot digital eyes)
            val eyeSpacing = visorWidth * 0.28f
            val eyeWidth = 14f
            val eyeHeight = if (blinkAnim > 0.3f) 22f else 3f
            val eyeColor = when (pose) {
                MascotPose.EUREKA -> AmberAccent
                MascotPose.CELEBRATION -> EmeraldAccent
                MascotPose.THINKING -> CyanAccent
                else -> Color.White
            }

            // Left Eye
            drawRoundRect(
                color = eyeColor,
                topLeft = Offset(centerX - eyeSpacing - eyeWidth / 2f, centerY - eyeHeight / 2f),
                size = Size(eyeWidth, eyeHeight),
                cornerRadius = CornerRadius(6f, 6f)
            )
            // Right Eye
            drawRoundRect(
                color = eyeColor,
                topLeft = Offset(centerX + eyeSpacing - eyeWidth / 2f, centerY - eyeHeight / 2f),
                size = Size(eyeWidth, eyeHeight),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // Cute Blush marks
            drawCircle(
                color = CrimsonLight.copy(alpha = 0.6f),
                radius = 8f,
                center = Offset(centerX - eyeSpacing - eyeWidth / 2f, centerY + 18f)
            )
            drawCircle(
                color = CrimsonLight.copy(alpha = 0.6f),
                radius = 8f,
                center = Offset(centerX + eyeSpacing + eyeWidth / 2f, centerY + 18f)
            )

            // Floating Waving Arms
            val armY = centerY + headRadius * 0.9f
            // Left Arm
            drawLine(
                color = VioletSecondary,
                start = Offset(centerX - bodyWidth / 2f, armY),
                end = Offset(centerX - bodyWidth * 0.85f, armY - 10f + rotationArm),
                strokeWidth = 8f
            )
            drawCircle(
                color = CyanAccent,
                radius = 10f,
                center = Offset(centerX - bodyWidth * 0.85f, armY - 10f + rotationArm)
            )

            // Right Arm (Waving or holding note)
            val rightArmEnd = if (pose == MascotPose.EXPLAINING || pose == MascotPose.CELEBRATION) {
                Offset(centerX + bodyWidth * 0.85f, armY - 30f - rotationArm)
            } else {
                Offset(centerX + bodyWidth * 0.85f, armY + 15f + rotationArm)
            }
            drawLine(
                color = VioletSecondary,
                start = Offset(centerX + bodyWidth / 2f, armY),
                end = rightArmEnd,
                strokeWidth = 8f
            )
            drawCircle(
                color = CrimsonPrimary,
                radius = 10f,
                center = rightArmEnd
            )
        }
    }
}
