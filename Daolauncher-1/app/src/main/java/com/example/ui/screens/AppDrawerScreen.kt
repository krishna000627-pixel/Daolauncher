package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.data.model.AppInfo
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppDrawerScreen(
    viewModel: LauncherViewModel,
    apps: List<AppInfo>,
    searchQuery: String,
    selectedCategory: String,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Favorites", "Study", "Distraction", "Communication", "Browser", "Productivity", "Media", "System")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search installed Dao artifacts & apps...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = CelestialGold
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = TextSecondary
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CelestialGold,
                unfocusedBorderColor = VoidBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = VoidCard,
                unfocusedContainerColor = VoidCard
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CelestialGold.copy(alpha = 0.25f) else VoidCard)
                        .border(
                            1.dp,
                            if (isSelected) CelestialGold else VoidBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.setSelectedAppCategory(cat) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) CelestialGold else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // App Grid
        if (apps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No apps found matching \"$searchQuery\"",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 78.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("app_grid"),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(apps, key = { it.packageName }) { app ->
                    AppGridItem(
                        app = app,
                        onLaunch = { viewModel.launchApp(app) },
                        onTogglePin = { viewModel.togglePinApp(app) },
                        onConfigureApp = { viewModel.openAppCategorizationDialog(app) },
                        onOpenDetails = { viewModel.openAppDetails(app.packageName) }
                    )
                }
            }
        }
    }
}

@Composable
fun AppGridItem(
    app: AppInfo,
    onLaunch: () -> Unit,
    onTogglePin: () -> Unit,
    onConfigureApp: () -> Unit,
    onOpenDetails: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val iconBitmap = remember(app.icon) {
        app.icon?.let { drawable ->
            try {
                drawable.toBitmap(width = 96, height = 96, config = Bitmap.Config.ARGB_8888).asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onLaunch() }
            .padding(4.dp)
            .testTag("app_item_${app.packageName}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(VoidCard)
                    .border(
                        0.8.dp,
                        when {
                            app.isStudy -> JadeQiGreen.copy(alpha = 0.8f)
                            app.isDistraction && !app.isCurrentlyUnlocked -> Color(0xFFEF4444).copy(alpha = 0.8f)
                            app.isDistraction && app.isCurrentlyUnlocked -> CelestialGold.copy(alpha = 0.8f)
                            app.isPinned -> CelestialGold.copy(alpha = 0.6f)
                            else -> VoidBorder
                        },
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = app.label,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CelestialGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = app.label.take(1).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGold
                        )
                    }
                }

                // Badges
                // 1. Study App Badge (+10 Coins)
                if (app.isStudy) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(JadeQiGreen)
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "+10🪙",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
                // 2. Distraction App Badge (Locked shield or unlock timer)
                else if (app.isDistraction) {
                    if (app.isCurrentlyUnlocked) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(CelestialGold)
                                .padding(horizontal = 3.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${app.remainingUnlockMinutes}m",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Distraction Blocked",
                                tint = Color.White,
                                modifier = Modifier.size(9.dp)
                            )
                        }
                    }
                }
                // 3. Pinned Badge
                else if (app.isPinned) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(CelestialGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = Color.Black,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = app.label,
                fontSize = 11.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Long-press or option button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 14.dp)
        ) {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(VoidDark)
            ) {
                DropdownMenuItem(
                    text = { Text(if (app.isPinned) "Unpin from Dock" else "Pin to Dao Dock", color = TextPrimary) },
                    onClick = {
                        onTogglePin()
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(Icons.Default.PushPin, contentDescription = null, tint = CelestialGold)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Set Role / Wards", color = TextPrimary) },
                    onClick = {
                        onConfigureApp()
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = SpiritualCyan)
                    }
                )
                DropdownMenuItem(
                    text = { Text("App Details", color = TextPrimary) },
                    onClick = {
                        onOpenDetails()
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Info, contentDescription = null, tint = TextSecondary)
                    }
                )
            }
        }
    }
}
