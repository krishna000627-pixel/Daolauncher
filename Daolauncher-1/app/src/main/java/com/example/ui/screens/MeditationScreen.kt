package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CultivationProfile
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.JadeQiGreen
import com.example.ui.theme.SpiritualCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark
import com.example.ui.viewmodel.BreathingPhase
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun MeditationScreen(
    viewModel: LauncherViewModel,
    profile: CultivationProfile,
    modifier: Modifier = Modifier
) {
    val isMeditating = viewModel.isMeditating.value
    val durationMin = viewModel.meditationDurationMin.value
    val remainingSec = viewModel.meditationRemainingSeconds.value
    val breathingPhase = viewModel.breathingPhase.value
    val phaseSecLeft = viewModel.breathingPhaseSecondsLeft.value
    val completedReward = viewModel.meditationCompletedReward.value

    val realm = profile.currentRealm
    val minutes = remainingSec / 60
    val seconds = remainingSec % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val durationPresets = listOf(10, 15, 25, 45, 60)

    val infiniteTransition = rememberInfiniteTransition(label = "breathing_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = if (breathingPhase == BreathingPhase.INHALE) 0.75f else 1.0f,
        targetValue = if (breathingPhase == BreathingPhase.INHALE) 1.25f else 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Mind Clarity Cultivation",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Refine spirit, purge distractions, and accumulate Qi",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Central Breathing Chamber
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .testTag("meditation_breathing_visualizer"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val baseRadius = size.minDimension / 3f

                    // Pulsing Qi Atmosphere
                    val auraColor = when (breathingPhase) {
                        BreathingPhase.INHALE -> SpiritualCyan
                        BreathingPhase.HOLD -> CelestialGold
                        BreathingPhase.EXHALE -> JadeQiGreen
                        BreathingPhase.STILLNESS -> realm.accentColor
                    }

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                auraColor.copy(alpha = if (isMeditating) 0.4f else 0.15f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = baseRadius * (if (isMeditating) pulseScale * 1.3f else 1.1f)
                        ),
                        radius = baseRadius * (if (isMeditating) pulseScale * 1.3f else 1.1f),
                        center = center
                    )

                    // Outer Sacred Ring
                    drawCircle(
                        color = auraColor.copy(alpha = 0.4f),
                        radius = baseRadius * 1.15f,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Core Breathing Disc
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                auraColor,
                                auraColor.copy(alpha = 0.5f),
                                VoidDark
                            ),
                            center = center,
                            radius = baseRadius * (if (isMeditating) pulseScale else 0.95f)
                        ),
                        radius = baseRadius * (if (isMeditating) pulseScale else 0.95f),
                        center = center
                    )
                }

                // Digital Timer & Phase Display inside Core
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = timeFormatted,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White,
                        letterSpacing = (-1).sp
                    )

                    if (isMeditating) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = breathingPhase.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${phaseSecLeft}s",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    } else {
                        Text(
                            text = "Estimated Yield: +${durationMin * 4} Qi",
                            fontSize = 11.sp,
                            color = SpiritualCyan
                        )
                    }
                }
            }

            // Duration Selection (When not meditating)
            if (!isMeditating) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Select Cultivation Duration (Minutes)",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        durationPresets.forEach { mins ->
                            val isSelected = durationMin == mins
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) CelestialGold.copy(alpha = 0.25f) else VoidCard)
                                    .border(
                                        1.dp,
                                        if (isSelected) CelestialGold else VoidBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setMeditationDuration(mins) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .testTag("duration_${mins}_min")
                            ) {
                                Text(
                                    text = "$mins m",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) CelestialGold else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Controls (Start / Stop)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isMeditating) {
                    Button(
                        onClick = { viewModel.startMeditation() },
                        colors = ButtonDefaults.buttonColors(containerColor = CelestialGold),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(52.dp)
                            .testTag("start_meditation_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Enter Daoist Stillness",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.pauseOrCancelMeditation() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(50.dp)
                            .testTag("stop_meditation_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stop, contentDescription = null, tint = Color(0xFFEF4444))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Conclude Meditation Early", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Completion Dialog
        if (completedReward != null) {
            Dialog(onDismissRequest = { viewModel.dismissMeditationReward() }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.5.dp, CelestialGold, RoundedCornerShape(24.dp))
                        .testTag("meditation_reward_dialog"),
                    colors = CardDefaults.cardColors(containerColor = VoidDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SpiritualCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = null,
                                tint = SpiritualCyan,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "DAO MEDITATION COMPLETE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your mortal mind has been purified. Pure spiritual energy settles into your Dantian.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SpiritualCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+${completedReward.first} Qi", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SpiritualCyan)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Diamond, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+${completedReward.second} Stones", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CelestialGold)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.dismissMeditationReward() },
                            colors = ButtonDefaults.buttonColors(containerColor = CelestialGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Consolidate Qi", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
