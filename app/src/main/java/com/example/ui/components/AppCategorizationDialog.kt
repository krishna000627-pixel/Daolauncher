package com.example.ui.components

import android.graphics.Bitmap
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
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.drawable.toBitmap
import com.example.data.model.AppInfo
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.JadeQiGreen
import com.example.ui.theme.SpiritualCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark

enum class AppRoleOption(
    val title: String,
    val description: String,
    val color: Color,
    val icon: ImageVector
) {
    NORMAL(
        "Standard App",
        "Normal launcher behavior without restriction",
        TextSecondary,
        Icons.Default.Apps
    ),
    DISTRACTION(
        "Distraction App (Cost 10 Coins/5m)",
        "Dao Heart Ward asks for Spirit Stones or willpower to open",
        Color(0xFFEF4444),
        Icons.Default.Block
    ),
    STUDY(
        "Dao Study App (+10 Coins & +25 Qi)",
        "Awards Spirit Stones & Qi every time you open to study",
        JadeQiGreen,
        Icons.Default.MenuBook
    ),
    HIDDEN(
        "Hidden Sanctuary App (Vault Only)",
        "Concealed from launcher & drawer. Open via Dao Calculator only",
        SpiritualCyan,
        Icons.Default.VisibilityOff
    )
}

@Composable
fun AppCategorizationDialog(
    app: AppInfo,
    onSelectRole: (AppRoleOption) -> Unit,
    onDismiss: () -> Unit
) {
    val currentRole = when {
        app.isHidden -> AppRoleOption.HIDDEN
        app.isDistraction -> AppRoleOption.DISTRACTION
        app.isStudy -> AppRoleOption.STUDY
        else -> AppRoleOption.NORMAL
    }

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
                .border(1.dp, VoidBorder, RoundedCornerShape(24.dp))
                .testTag("app_categorization_dialog"),
            colors = CardDefaults.cardColors(containerColor = VoidDark),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (iconBitmap != null) {
                            Image(
                                bitmap = iconBitmap,
                                contentDescription = app.label,
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CelestialGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(app.label.take(1), fontWeight = FontWeight.Bold, color = CelestialGold)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = app.label,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "App Cultivation Classification",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppRoleOption.values().forEach { option ->
                        val isSelected = currentRole == option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) option.color.copy(alpha = 0.15f) else VoidCard)
                                .border(
                                    1.dp,
                                    if (isSelected) option.color else VoidBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    onSelectRole(option)
                                    onDismiss()
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(option.color.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = null,
                                    tint = option.color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) option.color else TextPrimary
                                )
                                Text(
                                    text = option.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 14.sp
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onSelectRole(option)
                                    onDismiss()
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = option.color,
                                    unselectedColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
