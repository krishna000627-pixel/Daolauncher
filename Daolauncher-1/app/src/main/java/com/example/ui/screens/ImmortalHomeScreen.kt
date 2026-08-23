package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppInfo
import com.example.data.model.CultivationProfile
import com.example.data.model.CultivationTask
import com.example.ui.components.DantianCoreVisualizer
import com.example.ui.components.RealmBadge
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.JadeQiGreen
import com.example.ui.theme.SpiritualCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark
import com.example.ui.viewmodel.LauncherTab
import com.example.ui.viewmodel.LauncherViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ImmortalHomeScreen(
    viewModel: LauncherViewModel,
    profile: CultivationProfile,
    tasks: List<CultivationTask>,
    dockApps: List<AppInfo>,
    modifier: Modifier = Modifier
) {
    val realm = profile.currentRealm
    val isReady = profile.isReadyForBreakthrough
    val progress = (profile.currentQi.toFloat() / profile.maxQi.toFloat()).coerceIn(0f, 1f)

    var currentTimeStr by remember { mutableStateOf("") }
    var currentDateStr by remember { mutableStateOf("") }

    // Live clock ticker
    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTimeStr = timeFormat.format(now)
            currentDateStr = dateFormat.format(now)
            delay(1000)
        }
    }

    val wisdomQuote = viewModel.daoWisdomQuote.value

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Clock & Heavenly Cycle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = currentTimeStr.ifEmpty { "12:00" },
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Light,
                    color = TextPrimary,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "$currentDateStr • Heavenly Era Cycle",
                    fontSize = 13.sp,
                    color = realm.runeColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Realm Badge Card
        item {
            RealmBadge(
                profile = profile,
                onBreakthroughClick = { viewModel.openBreakthroughDialog() },
                onProfileDetailsClick = { viewModel.setTab(LauncherTab.PAVILION) }
            )
        }

        // Default Launcher Setup Prompt (if not default)
        if (!viewModel.isDefaultLauncher.value) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(VoidCard)
                        .border(1.dp, CelestialGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .clickable { viewModel.openDefaultLauncherSettings() }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("set_default_launcher_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home Launcher",
                                tint = CelestialGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Set as Default Android Launcher",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Cultivate every time you return to home screen",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CelestialGold.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Set Home", fontSize = 11.sp, color = CelestialGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Central Dantian Core
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                DantianCoreVisualizer(
                    realm = realm,
                    qiProgress = progress,
                    isBreakthroughReady = isReady,
                    sizeDp = 210.dp,
                    onClick = {
                        viewModel.cycleWisdomQuote()
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Daoist Maxim of the day
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VoidCard.copy(alpha = 0.7f))
                        .clickable { viewModel.cycleWisdomQuote() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📜 \"$wisdomQuote\"",
                        fontSize = 12.sp,
                        color = realm.runeColor,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Quick Quest Mandates Section
        item {
            val activeTasks = tasks.filter { !it.isCompleted }.take(3)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VoidCard)
                    .border(1.dp, VoidBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp)
                    .testTag("home_quick_quests_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = CelestialGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Today's Sect Mandates",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "View All (${tasks.count { !it.isCompleted }})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = realm.runeColor,
                            modifier = Modifier.clickable { viewModel.setTab(LauncherTab.QUESTS) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (activeTasks.isEmpty()) {
                        Text(
                            text = "All sect quests fulfilled for today! Dantian is at peace.",
                            fontSize = 12.sp,
                            color = JadeQiGreen,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    } else {
                        activeTasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(VoidDark.copy(alpha = 0.5f))
                                    .clickable { viewModel.completeTask(task) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "Complete task",
                                    tint = task.grade.color,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "+${task.grade.qiReward} Qi • ${task.category.displayName.substringBefore(" &")}",
                                        fontSize = 11.sp,
                                        color = task.grade.color
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    // Inscribe Quest Quick Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openInscribeQuestDialog() }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Inscribe New Cultivation Quest", fontSize = 12.sp, color = CelestialGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Focus Cultivation Chamber Quick Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                realm.primaryColor.copy(alpha = 0.25f),
                                SpiritualCyan.copy(alpha = 0.25f)
                            )
                        )
                    )
                    .border(1.dp, realm.primaryColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable { viewModel.setTab(LauncherTab.MEDITATION) }
                    .padding(14.dp)
                    .testTag("quick_meditation_banner")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(realm.primaryColor.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = "Meditation",
                                tint = realm.accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Mind Clarity Focus Chamber",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Deep Dantian breathing & pomodoro cultivation",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CelestialGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
