package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.AlchemyPill
import com.example.data.model.CultivationProfile
import com.example.data.model.CultivationRealm
import com.example.data.repository.BreakthroughResult
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TribulationPurple
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark

@Composable
fun BreakthroughDialog(
    profile: CultivationProfile,
    breakthroughPillCount: Int,
    isCultivatingBreakthrough: Boolean,
    lastBreakthroughResult: BreakthroughResult?,
    onDismiss: () -> Unit,
    onAttemptBreakthrough: (usePill: Boolean) -> Unit
) {
    var usePill by remember { mutableStateOf(breakthroughPillCount > 0) }
    val currentRealm = profile.currentRealm

    val nextSubStage = if (profile.realmSubStage >= 4) 1 else profile.realmSubStage + 1
    val nextRealmLevel = if (profile.realmSubStage >= 4) (profile.realmLevel + 1).coerceAtMost(9) else profile.realmLevel
    val targetRealm = CultivationRealm.fromLevel(nextRealmLevel)

    val baseChance = when (profile.realmSubStage) {
        1 -> 95
        2 -> 85
        3 -> 75
        else -> 65
    }
    val effectiveChance = if (usePill && breakthroughPillCount > 0) 100 else baseChance

    val infiniteTransition = rememberInfiniteTransition(label = "lightning_aura")
    val lightningAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lightning"
    )

    Dialog(onDismissRequest = { if (!isCultivatingBreakthrough) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    2.dp,
                    Brush.verticalGradient(
                        listOf(
                            TribulationPurple.copy(alpha = lightningAlpha),
                            CelestialGold,
                            TribulationPurple.copy(alpha = lightningAlpha)
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
                .testTag("breakthrough_dialog"),
            colors = CardDefaults.cardColors(containerColor = VoidDark),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (lastBreakthroughResult != null) {
                    // Result View
                    when (lastBreakthroughResult) {
                        is BreakthroughResult.Success -> {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(CelestialGold.copy(alpha = 0.2f))
                                    .border(2.dp, CelestialGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = CelestialGold,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (lastBreakthroughResult.isMajorAscension) "⚡ CELESTIAL ASCENSION! ⚡" else "TRIBULATION OVERCOME!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CelestialGold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your spiritual sea expands! You have broken through to ${lastBreakthroughResult.newRealm.title} (${lastBreakthroughResult.newSubStage.let { when(it){1->"Early";2->"Mid";3->"Late";else->"Peak"} }} Stage)!",
                                fontSize = 14.sp,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "+100 Spirit Stones bestowed by the Heavenly Dao",
                                fontSize = 12.sp,
                                color = Color(0xFF34D399),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = CelestialGold),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("breakthrough_claim_button")
                            ) {
                                Text(
                                    text = "Embrace New Realm",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        is BreakthroughResult.Failed -> {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33EF4444))
                                    .border(2.dp, Color(0xFFEF4444), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Failed",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "TRIBULATION RETREAT",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Spiritual Qi fluctuated violently in your meridians. You lost ${lastBreakthroughResult.lostQi} Qi, but your core remains unharmed.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = VoidCard),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Consolidate & Train Again", color = TextPrimary)
                            }
                        }
                        is BreakthroughResult.NotEnoughQi -> {
                            Text("Not enough Qi gathered yet.", color = TextPrimary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = onDismiss) { Text("Close") }
                        }
                    }
                } else if (isCultivatingBreakthrough) {
                    // Ongoing animation
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = CelestialGold,
                        strokeWidth = 5.dp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "ENDURING HEAVENLY TRIBULATION...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TribulationPurple,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nine Heavenly Thunderbolts are striking your Dantian Core!",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Initial Breakthrough Form
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Thunder",
                            tint = TribulationPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HEAVENLY TRIBULATION",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CelestialGold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Realm transition preview card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(VoidCard)
                            .padding(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentRealm.title} (${profile.stageName})",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = "Ascending",
                                tint = CelestialGold,
                                modifier = Modifier
                                    .padding(vertical = 6.dp)
                                    .size(20.dp)
                            )
                            Text(
                                text = "${targetRealm.title} (${when(nextSubStage){1->"Early";2->"Mid";3->"Late";else->"Peak"}} Stage)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = targetRealm.accentColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Success Rate & Pill Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tribulation Success Rate:",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "$effectiveChance%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (effectiveChance >= 90) Color(0xFF34D399) else CelestialGold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pill booster check
                    if (breakthroughPillCount > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x2210B981))
                                .clickable { usePill = !usePill }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = usePill,
                                onCheckedChange = { usePill = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                            )
                            Column {
                                Text(
                                    text = "Use Foundation Solidifying Pill (Have: $breakthroughPillCount)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6EE7B7)
                                )
                                Text(
                                    text = "Guarantees 100% breakthrough success",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Tip: Refine 'Foundation Solidifying Pill' in Alchemy Pavilion for 100% chance.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Meditate More", color = TextSecondary)
                        }

                        Button(
                            onClick = { onAttemptBreakthrough(usePill && breakthroughPillCount > 0) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TribulationPurple
                            ),
                            modifier = Modifier
                                .weight(1.4f)
                                .testTag("confirm_breakthrough_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Summon Lightning",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
