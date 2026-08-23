package com.example.ui.components

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CultivationProfile
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidCard

@Composable
fun RealmBadge(
    profile: CultivationProfile,
    modifier: Modifier = Modifier,
    onBreakthroughClick: () -> Unit = {},
    onProfileDetailsClick: () -> Unit = {}
) {
    val realm = profile.currentRealm
    val progress = (profile.currentQi.toFloat() / profile.maxQi.toFloat()).coerceIn(0f, 1f)
    val isReady = profile.isReadyForBreakthrough

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        VoidCard.copy(alpha = 0.92f),
                        VoidCard.copy(alpha = 0.85f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        realm.primaryColor.copy(alpha = if (isReady) 0.9f else 0.5f),
                        realm.accentColor.copy(alpha = if (isReady) 0.9f else 0.2f),
                        realm.secondaryColor.copy(alpha = if (isReady) 0.9f else 0.5f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onProfileDetailsClick() }
            .padding(16.dp)
            .testTag("realm_badge_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            // Top Row: Title, Chinese Glyphs, Daoist Title, Spirit Stones
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
                            .background(realm.primaryColor.copy(alpha = 0.2f))
                            .border(1.dp, realm.primaryColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = realm.chineseTitle.take(2),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = realm.accentColor
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = realm.title,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(realm.primaryColor.copy(alpha = 0.25f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = profile.stageName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = realm.runeColor
                                )
                            }
                        }
                        Text(
                            text = "${profile.daoTitle} • Realm Lvl ${realm.level}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Currency & Streak pill
                Column(horizontalAlignment = Alignment.End) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CelestialGold.copy(alpha = 0.15f))
                            .border(0.8.dp, CelestialGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "Spirit Stones",
                            tint = CelestialGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${profile.spiritStones}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGold
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = Color(0xFFF97316),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${profile.consecutiveDaoDays}d Dao Streak",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Qi Energy Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Dantian Qi Reservoir",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = realm.runeColor
                    )
                    Text(
                        text = "${profile.currentQi} / ${profile.maxQi} Qi (${(progress * 100).toInt()}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isReady) CelestialGold else TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isReady) CelestialGold else realm.primaryColor,
                    trackColor = Color(0xFF0F172A),
                    strokeCap = StrokeCap.Round
                )
            }

            // Breakthrough Ready Banner Action
            if (isReady) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    realm.primaryColor.copy(alpha = 0.4f),
                                    CelestialGold.copy(alpha = 0.4f)
                                )
                            )
                        )
                        .border(1.dp, CelestialGold, RoundedCornerShape(12.dp))
                        .clickable { onBreakthroughClick() }
                        .padding(vertical = 10.dp, horizontal = 12.dp)
                        .testTag("breakthrough_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Tribulation Ready",
                            tint = CelestialGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "⚡ HEAVENLY TRIBULATION READY - ATTEMPT BREAKTHROUGH ⚡",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CelestialGold
                        )
                    }
                }
            }

            // Active Buff Indicator
            if (profile.activeBuffPill != null && profile.buffExpiresAt > System.currentTimeMillis()) {
                val remainingMin = ((profile.buffExpiresAt - System.currentTimeMillis()) / 60000).coerceAtLeast(1)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x2210B981))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Active Buff",
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Active Pill Effect (${profile.activeBuffPill?.replace("_", " ")?.capitalize()}) - ${remainingMin}m remaining",
                        fontSize = 11.sp,
                        color = Color(0xFF6EE7B7)
                    )
                }
            }
        }
    }
}
