package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlchemyPill
import com.example.data.model.CultivationProfile
import com.example.data.model.CultivationRealm
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.JadeQiGreen
import com.example.ui.theme.SpiritualCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun PavilionScreen(
    viewModel: LauncherViewModel,
    profile: CultivationProfile,
    pills: List<AlchemyPill>,
    modifier: Modifier = Modifier
) {
    val currentRealm = profile.currentRealm

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            // Header
            Text(
                text = "Daoist Pavilion & Chronicle",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Track your spiritual ascent across the 9 Immortal Realms",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Stats Overview Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(VoidCard)
                    .border(1.dp, VoidBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .testTag("stats_overview_card")
            ) {
                Column {
                    Text(
                        text = "Cultivation Milestones",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            icon = Icons.Default.TaskAlt,
                            iconTint = JadeQiGreen,
                            title = "Quests Fulfilled",
                            value = "${profile.totalTasksCompleted}"
                        )
                        StatItem(
                            icon = Icons.Default.HourglassTop,
                            iconTint = SpiritualCyan,
                            title = "Dao Meditation",
                            value = "${profile.totalFocusMinutes} min"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            icon = Icons.Default.ElectricBolt,
                            iconTint = Color(0xFFA855F7),
                            title = "Breakthroughs",
                            value = "${profile.tribulationBreakthroughCount}"
                        )
                        StatItem(
                            icon = Icons.Default.LocalFireDepartment,
                            iconTint = Color(0xFFF97316),
                            title = "Consecutive Streak",
                            value = "${profile.consecutiveDaoDays} Days"
                        )
                    }
                }
            }
        }

        // Alchemy Cauldron Quick Action Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                CelestialGold.copy(alpha = 0.2f),
                                VoidCard
                            )
                        )
                    )
                    .border(1.2.dp, CelestialGold.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .clickable { viewModel.openAlchemyDialog() }
                    .padding(16.dp)
                    .testTag("open_alchemy_pavilion_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CelestialGold.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = CelestialGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Alchemy Pavilion (Pill Refining)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Brew Qi pills, elixirs & breakthrough catalysts",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.openAlchemyDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = CelestialGold)
                    ) {
                        Text("Enter", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Realms Atlas / Progression Roadmap
        item {
            Text(
                text = "The Nine Heavenly Realms Atlas",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(CultivationRealm.entries.size) { index ->
            val realmEntry = CultivationRealm.fromLevel(index)
            val isUnlocked = index <= profile.realmLevel
            val isCurrent = index == profile.realmLevel

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isCurrent) realmEntry.primaryColor.copy(alpha = 0.2f) else VoidCard)
                    .border(
                        1.dp,
                        if (isCurrent) realmEntry.primaryColor else if (isUnlocked) VoidBorder else Color(0x22FFFFFF),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Realm Status Icon
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCurrent) realmEntry.primaryColor
                                else if (isUnlocked) JadeQiGreen.copy(alpha = 0.25f)
                                else VoidBorder
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCurrent) Icons.Default.AutoAwesome else if (isUnlocked) Icons.Default.Check else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isCurrent) Color.Black else if (isUnlocked) JadeQiGreen else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Realm ${realmEntry.level}: ${realmEntry.title}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) TextPrimary else TextMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = realmEntry.chineseTitle,
                                fontSize = 12.sp,
                                color = if (isUnlocked) realmEntry.accentColor else TextMuted
                            )
                        }
                        Text(
                            text = realmEntry.description,
                            fontSize = 11.sp,
                            color = if (isUnlocked) TextSecondary else TextMuted
                        )
                    }

                    if (isCurrent) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(realmEntry.primaryColor.copy(alpha = 0.3f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "CURRENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = realmEntry.runeColor
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontSize = 11.sp, color = TextSecondary)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}
