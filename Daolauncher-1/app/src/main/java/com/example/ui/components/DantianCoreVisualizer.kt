package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.CultivationRealm
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DantianCoreVisualizer(
    realm: CultivationRealm,
    qiProgress: Float, // 0.0f to 1.0f+
    isBreakthroughReady: Boolean,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 220.dp,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dantian_pulse")
    
    // Core Rotation
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isBreakthroughReady) 8000 else 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Bagua Ring Counter-Rotation
    val baguaRotationAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bagua_rotation"
    )

    // Breathing Pulse
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isBreakthroughReady) 1200 else 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Aura Ripple
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    val primaryColor = realm.primaryColor
    val secondaryColor = realm.secondaryColor
    val runeColor = realm.runeColor
    val accentColor = realm.accentColor

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(sizeDp)
            .testTag("dantian_core_visualizer")
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(sizeDp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.minDimension / 2f) * 0.9f

            // 1. Primordial Dantian Aura Gradient
            val auraBrush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = auraAlpha * 0.45f),
                    secondaryColor.copy(alpha = auraAlpha * 0.2f),
                    Color.Transparent
                ),
                center = center,
                radius = radius * 1.25f * breathingScale
            )
            drawCircle(
                brush = auraBrush,
                radius = radius * 1.25f * breathingScale,
                center = center
            )

            // 2. Outer Heavenly Trigram Array Ring
            rotate(baguaRotationAngle, pivot = center) {
                drawCircle(
                    color = runeColor.copy(alpha = 0.35f),
                    radius = radius * 0.98f,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                drawCircle(
                    color = secondaryColor.copy(alpha = 0.25f),
                    radius = radius * 0.86f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )

                // 8 Trigram Sectors
                val trigrams = listOf(
                    "☰", "☱", "☲", "☳", "☴", "☵", "☶", "☷"
                )
                for (i in 0 until 8) {
                    val angleDeg = i * 45f
                    val rad = Math.toRadians(angleDeg.toDouble())
                    val trigramRadius = radius * 0.92f
                    val posX = (center.x + trigramRadius * cos(rad)).toFloat()
                    val posY = (center.y + trigramRadius * sin(rad)).toFloat()

                    // Trigram marker line
                    val tickLen = 6.dp.toPx()
                    val startX = (center.x + (trigramRadius - tickLen / 2) * cos(rad)).toFloat()
                    val startY = (center.y + (trigramRadius - tickLen / 2) * sin(rad)).toFloat()
                    val endX = (center.x + (trigramRadius + tickLen / 2) * cos(rad)).toFloat()
                    val endY = (center.y + (trigramRadius + tickLen / 2) * sin(rad)).toFloat()

                    drawLine(
                        color = if (isBreakthroughReady) accentColor else runeColor.copy(alpha = 0.7f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // 3. Qi Progress Circumference Arc (Accumulated Spiritual Power)
            val sweepProgress = (qiProgress.coerceIn(0f, 1f)) * 360f
            drawArc(
                color = runeColor.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius * 0.78f, center.y - radius * 0.78f),
                size = Size(radius * 1.56f, radius * 1.56f),
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )

            val progressGradient = Brush.sweepGradient(
                colors = listOf(
                    primaryColor,
                    secondaryColor,
                    accentColor,
                    primaryColor
                ),
                center = center
            )
            drawArc(
                brush = progressGradient,
                startAngle = -90f,
                sweepAngle = sweepProgress,
                useCenter = false,
                topLeft = Offset(center.x - radius * 0.78f, center.y - radius * 0.78f),
                size = Size(radius * 1.56f, radius * 1.56f),
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )

            // 4. Rotating Dantian Core (Taiji / Yin Yang & Golden Flame)
            val coreRadius = radius * 0.58f * breathingScale
            rotate(rotationAngle, pivot = center) {
                drawTaijiCore(
                    center = center,
                    radius = coreRadius,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    accentColor = accentColor,
                    isTribulation = isBreakthroughReady
                )
            }

            // 5. Center Golden Singularity Point
            drawCircle(
                color = accentColor,
                radius = 4.dp.toPx() * breathingScale,
                center = center
            )
        }
    }
}

private fun DrawScope.drawTaijiCore(
    center: Offset,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color,
    isTribulation: Boolean
) {
    // Left / Yang Half Arc
    val pathYang = Path().apply {
        // Outer arc from top to bottom (clockwise right side)
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                center.x - radius,
                center.y - radius,
                center.x + radius,
                center.y + radius
            ),
            startAngleDegrees = -90f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false
        )
        // Bottom inner curve towards center
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                center.x - radius / 2f,
                center.y,
                center.x + radius / 2f,
                center.y + radius
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false
        )
        // Top inner curve from center to top
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                center.x - radius / 2f,
                center.y - radius,
                center.x + radius / 2f,
                center.y
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false
        )
        close()
    }

    // Right / Yin Half Arc
    val pathYin = Path().apply {
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                center.x - radius,
                center.y - radius,
                center.x + radius,
                center.y + radius
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false
        )
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                center.x - radius / 2f,
                center.y - radius,
                center.x + radius / 2f,
                center.y
            ),
            startAngleDegrees = -90f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false
        )
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                center.x - radius / 2f,
                center.y,
                center.x + radius / 2f,
                center.y + radius
            ),
            startAngleDegrees = -90f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false
        )
        close()
    }

    drawPath(
        path = pathYang,
        brush = Brush.linearGradient(
            colors = listOf(primaryColor, accentColor),
            start = Offset(center.x, center.y - radius),
            end = Offset(center.x, center.y + radius)
        )
    )

    drawPath(
        path = pathYin,
        brush = Brush.linearGradient(
            colors = listOf(secondaryColor, Color(0xFF0F172A)),
            start = Offset(center.x, center.y - radius),
            end = Offset(center.x, center.y + radius)
        )
    )

    // Yang Dot (Top Center Eye)
    drawCircle(
        color = secondaryColor,
        radius = radius * 0.16f,
        center = Offset(center.x, center.y - radius / 2f)
    )

    // Yin Dot (Bottom Center Eye)
    drawCircle(
        color = accentColor,
        radius = radius * 0.16f,
        center = Offset(center.x, center.y + radius / 2f)
    )

    // Tribulation Sparks
    if (isTribulation) {
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = radius * 1.05f,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
