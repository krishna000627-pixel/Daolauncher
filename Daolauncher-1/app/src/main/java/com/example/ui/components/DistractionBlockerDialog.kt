package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.drawable.toBitmap
import com.example.data.model.AppInfo
import com.example.data.model.CultivationRealm
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.JadeQiGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark

@Composable
fun DistractionBlockerDialog(
    app: AppInfo,
    currentSpiritStones: Long,
    realm: CultivationRealm,
    onUnlockAndLaunch: (minutes: Int, cost: Int) -> Unit,
    onResistTemptation: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDurationIndex by remember { mutableIntStateOf(0) }

    val options = listOf(
        Triple(5, 10, "5 Mins"),
        Triple(15, 25, "15 Mins (Save 5)"),
        Triple(30, 45, "30 Mins (Save 15)")
    )

    val selectedOption = options[selectedDurationIndex]
    val selectedMinutes = selectedOption.first
    val selectedCost = selectedOption.second
    val canAfford = currentSpiritStones >= selectedCost

    val iconBitmap = remember(app.icon) {
        app.icon?.let { drawable ->
            try {
                drawable.toBitmap(width = 96, height = 96, config = Bitmap.Config.ARGB_8888).asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("distraction_blocker_dialog"),
            colors = CardDefaults.cardColors(containerColor = VoidDark),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFF330C0C),
                                VoidDark
                            )
                        )
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFFEF4444), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Dao Heart Ward",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Distraction Blocker Active",
                                fontSize = 11.sp,
                                color = Color(0xFFF87171)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App Icon & Label
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(VoidCard)
                        .border(1.5.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (iconBitmap != null) {
                        Image(
                            bitmap = iconBitmap,
                            contentDescription = app.label,
                            modifier = Modifier.size(54.dp).clip(RoundedCornerShape(14.dp))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = app.label,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Mundane Distraction • Temptation to Dao Focus",
                    fontSize = 12.sp,
                    color = Color(0xFFFCA5A5),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Lore description
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VoidCard.copy(alpha = 0.8f))
                        .border(1.dp, VoidBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "⚡ Unlocking worldly temptations drains your spiritual momentum. Pay Spirit Stones to unlock temporary access, or conquer your impulse to fortify your Dao Heart!",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Available Spirit Stones Balance
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CelestialGold.copy(alpha = 0.12f))
                        .border(1.dp, CelestialGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Spirit Stones (Coins):",
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "🪙 $currentSpiritStones Stones",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Duration Selection Chips
                Text(
                    text = "Select Indulgence Duration:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    options.forEachIndexed { index, option ->
                        val isSelected = selectedDurationIndex == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) CelestialGold.copy(alpha = 0.25f) else VoidCard
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) CelestialGold else VoidBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedDurationIndex = index }
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${option.first}m",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) CelestialGold else TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "🪙 ${option.second}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) CelestialGold else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action 1: Unlock & Launch
                Button(
                    onClick = {
                        if (canAfford) {
                            onUnlockAndLaunch(selectedMinutes, selectedCost)
                        }
                    },
                    enabled = canAfford,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("unlock_distraction_app_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CelestialGold,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                        disabledContentColor = TextMuted
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (canAfford) "Pay $selectedCost Stones • Unlock $selectedMinutes Mins" else "Need $selectedCost Stones (Earn on Quests)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 2: Resist Temptation (Willpower +10 Qi)
                OutlinedButton(
                    onClick = onResistTemptation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("resist_distraction_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = JadeQiGreen
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JadeQiGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = JadeQiGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Resist Temptation & Cultivate (+10 Qi)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = JadeQiGreen
                    )
                }
            }
        }
    }
}
