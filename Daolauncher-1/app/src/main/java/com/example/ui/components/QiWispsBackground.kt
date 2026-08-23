package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.data.model.CultivationRealm
import kotlin.random.Random

private data class QiParticle(
    val initialXRatio: Float,
    val initialYRatio: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val horizontalWobble: Float
)

@Composable
fun QiWispsBackground(
    realm: CultivationRealm,
    modifier: Modifier = Modifier
) {
    val particles = remember {
        List(28) {
            QiParticle(
                initialXRatio = Random.nextFloat(),
                initialYRatio = Random.nextFloat(),
                speed = 0.08f + Random.nextFloat() * 0.14f,
                size = 2f + Random.nextFloat() * 4.5f,
                alpha = 0.25f + Random.nextFloat() * 0.55f,
                horizontalWobble = 15f + Random.nextFloat() * 25f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "qi_particles")
    val timeProgression by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val bgStart = realm.bgStart
    val bgEnd = realm.bgEnd
    val primaryColor = realm.primaryColor
    val runeColor = realm.runeColor

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Background Gradient
        val bgBrush = Brush.verticalGradient(
            colors = listOf(
                bgStart,
                bgEnd,
                Color(0xFF070B14)
            ),
            startY = 0f,
            endY = height
        )
        drawRect(brush = bgBrush)

        // Subtle Radial Nebula at Top-Center
        val nebulaBrush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.18f),
                Color.Transparent
            ),
            center = Offset(width * 0.5f, height * 0.28f),
            radius = width * 0.75f
        )
        drawCircle(
            brush = nebulaBrush,
            center = Offset(width * 0.5f, height * 0.28f),
            radius = width * 0.75f
        )

        // Draw Rising Qi Sparks
        particles.forEach { p ->
            val currentYProg = (p.initialYRatio - (timeProgression * p.speed * 4f)) % 1f
            val actualY = (if (currentYProg < 0f) 1f + currentYProg else currentYProg) * height
            val wobbleX = Math.sin((actualY / height * 6.28) + p.initialXRatio * 10).toFloat() * p.horizontalWobble
            val actualX = (p.initialXRatio * width) + wobbleX

            drawCircle(
                color = runeColor.copy(alpha = p.alpha * (1f - (actualY / height) * 0.4f)),
                radius = p.size,
                center = Offset(actualX.coerceIn(0f, width), actualY)
            )
        }
    }
}
