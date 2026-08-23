package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

@Composable
fun DaoCalculatorVaultScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    var isVaultUnlocked by remember { mutableStateOf(false) }
    var expression by remember { mutableStateOf("") }
    var displayResult by remember { mutableStateOf("0") }
    var showPasscodeDialog by remember { mutableStateOf(false) }
    var showAppPickerSheet by remember { mutableStateOf(false) }

    val currentPasscode = viewModel.calculatorPasscode.value
    val allApps = viewModel.installedApps.value
    val hiddenApps = allApps.filter { it.isHidden }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidDark)
    ) {
        if (!isVaultUnlocked) {
            // Working Calculator Interface
            CalculatorView(
                expression = expression,
                displayResult = displayResult,
                onNumberClick = { num ->
                    if (displayResult == "0" || displayResult == "Error") {
                        displayResult = num
                    } else {
                        displayResult += num
                    }
                },
                onOperatorClick = { op ->
                    if (displayResult != "Error") {
                        expression = if (expression.isEmpty()) {
                            "$displayResult $op "
                        } else {
                            "$expression$displayResult $op "
                        }
                        displayResult = "0"
                    }
                },
                onClearClick = {
                    expression = ""
                    displayResult = "0"
                },
                onBackspaceClick = {
                    if (displayResult.length > 1 && displayResult != "0") {
                        displayResult = displayResult.dropLast(1)
                    } else {
                        displayResult = "0"
                    }
                },
                onToggleSign = {
                    if (displayResult != "0" && displayResult != "Error") {
                        displayResult = if (displayResult.startsWith("-")) {
                            displayResult.substring(1)
                        } else {
                            "-$displayResult"
                        }
                    }
                },
                onPercentage = {
                    try {
                        val num = displayResult.toDouble()
                        displayResult = (num / 100.0).toString().removeSuffix(".0")
                    } catch (e: Exception) {
                        displayResult = "Error"
                    }
                },
                onDotClick = {
                    if (!displayResult.contains(".")) {
                        displayResult += "."
                    }
                },
                onEqualsClick = {
                    // Check if secret passcode was entered!
                    val trimmedInput = displayResult.trim()
                    if (trimmedInput == currentPasscode || (expression + displayResult).replace(" ", "") == currentPasscode) {
                        isVaultUnlocked = true
                        expression = ""
                        displayResult = "0"
                    } else {
                        // Calculate real arithmetic
                        val fullExpr = (expression + displayResult).trim()
                        val result = evaluateMathExpression(fullExpr)
                        expression = ""
                        displayResult = result
                    }
                }
            )
        } else {
            // Unlocked Secret Vault (Dao Sanctum)
            HiddenSanctumVaultView(
                hiddenApps = hiddenApps,
                currentPasscode = currentPasscode,
                onLaunchApp = { app ->
                    viewModel.launchApp(app)
                },
                onLockVault = {
                    isVaultUnlocked = false
                    expression = ""
                    displayResult = "0"
                },
                onOpenAppPicker = {
                    showAppPickerSheet = true
                },
                onChangePasscodeClick = {
                    showPasscodeDialog = true
                }
            )
        }

        // Change Passcode Dialog
        if (showPasscodeDialog) {
            ChangePasscodeDialog(
                currentPasscode = currentPasscode,
                onSavePasscode = { newPin ->
                    viewModel.updateCalculatorPasscode(newPin)
                    showPasscodeDialog = false
                },
                onDismiss = { showPasscodeDialog = false }
            )
        }

        // Manage / Toggle Hidden Apps Sheet
        if (showAppPickerSheet) {
            ConcealAppsManagerDialog(
                allApps = allApps,
                onToggleHidden = { app, isHidden ->
                    viewModel.setAppHiddenStatus(app.packageName, isHidden)
                },
                onDismiss = { showAppPickerSheet = false }
            )
        }
    }
}

@Composable
private fun CalculatorView(
    expression: String,
    displayResult: String,
    onNumberClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    onToggleSign: () -> Unit,
    onPercentage: () -> Unit,
    onDotClick: () -> Unit,
    onEqualsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(JadeQiGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Dao Calculator",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }

            Text(
                text = "Default PIN: 8888=",
                fontSize = 11.sp,
                color = TextMuted,
                fontFamily = FontFamily.Monospace
            )
        }

        // Display Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = expression,
                fontSize = 20.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = displayResult,
                fontSize = 50.sp,
                fontWeight = FontWeight.Light,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calculator_display")
            )
        }

        // Keypad Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: C, +/-, %, ÷
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalcButton(text = "C", color = Color(0xFFEF4444), bg = VoidCard, modifier = Modifier.weight(1f), onClick = onClearClick)
                CalcButton(text = "+/-", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = onToggleSign)
                CalcButton(text = "%", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = onPercentage)
                CalcButton(text = "÷", color = CelestialGold, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onOperatorClick("÷") })
            }

            // Row 2: 7, 8, 9, ×
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalcButton(text = "7", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onNumberClick("7") })
                CalcButton(text = "8", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onNumberClick("8") })
                CalcButton(text = "9", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onNumberClick("9") })
                CalcButton(text = "×", color = CelestialGold, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onOperatorClick("×") })
            }

            // Row 3: 4, 5, 6, -
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalcButton(text = "4", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onNumberClick("4") })
                CalcButton(text = "5", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onNumberClick("5") })
                CalcButton(text = "6", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onNumberClick("6") })
                CalcButton(text = "-", color = CelestialGold, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onOperatorClick("-") })
            }

            // Row 4: 1, 2, 3, +
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalcButton(text = "1", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onNumberClick("1") })
                CalcButton(text = "2", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onNumberClick("2") })
                CalcButton(text = "3", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onNumberClick("3") })
                CalcButton(text = "+", color = CelestialGold, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onOperatorClick("+") })
            }

            // Row 5: 0, ., ⌫, =
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CalcButton(text = "0", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = { onNumberClick("0") })
                CalcButton(text = ".", color = TextPrimary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = onDotClick)
                CalcIconButton(icon = Icons.Default.Backspace, color = TextSecondary, bg = VoidCard, modifier = Modifier.weight(1f), onClick = onBackspaceClick)
                CalcButton(
                    text = "=",
                    color = Color.Black,
                    bg = CelestialGold,
                    modifier = Modifier.weight(1f),
                    onClick = onEqualsClick,
                    testTag = "calc_equals_button"
                )
            }
        }
    }
}

@Composable
private fun CalcButton(
    text: String,
    color: Color,
    bg: Color,
    modifier: Modifier = Modifier,
    testTag: String? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1.25f)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, VoidBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun CalcIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    bg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1.25f)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, VoidBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun HiddenSanctumVaultView(
    hiddenApps: List<AppInfo>,
    currentPasscode: String,
    onLaunchApp: (AppInfo) -> Unit,
    onLockVault: () -> Unit,
    onOpenAppPicker: () -> Unit,
    onChangePasscodeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("hidden_sanctum_vault_screen")
    ) {
        // Vault Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SpiritualCyan.copy(alpha = 0.2f))
                        .border(1.dp, SpiritualCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Sanctuary",
                        tint = SpiritualCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Dao Concealment Vault",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Hidden from Launcher & App Drawer",
                        fontSize = 11.sp,
                        color = SpiritualCyan
                    )
                }
            }

            Button(
                onClick = onLockVault,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VoidCard),
                modifier = Modifier.testTag("lock_vault_button")
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Lock", tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lock", fontSize = 12.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Invisibility Array Status Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            SpiritualCyan.copy(alpha = 0.2f),
                            VoidCard
                        )
                    )
                )
                .border(1.dp, SpiritualCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🔒 Heavenly Concealment Array Active",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${hiddenApps.size} apps are sealed inside this vault. Only opening via Calculator with PIN reveals them.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons: Conceal More & Change Passcode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onOpenAppPicker,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CelestialGold, contentColor = Color.Black)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Conceal Apps", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onChangePasscodeClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VoidBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
            ) {
                Icon(Icons.Default.Key, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Change PIN", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Hidden Apps
        Text(
            text = "Concealed Artifacts (${hiddenApps.size})",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (hiddenApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No apps currently concealed",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap 'Conceal Apps' above to hide sensitive apps inside this vault.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 80.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(hiddenApps, key = { it.packageName }) { app ->
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
                            .clickable { onLaunchApp(app) }
                            .padding(4.dp)
                            .testTag("vault_app_${app.packageName}"),
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
                                    .border(1.dp, SpiritualCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (iconBitmap != null) {
                                    Image(
                                        bitmap = iconBitmap,
                                        contentDescription = app.label,
                                        modifier = Modifier.size(42.dp).clip(RoundedCornerShape(10.dp))
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(SpiritualCyan.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = app.label.take(1).uppercase(),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SpiritualCyan
                                        )
                                    }
                                }

                                // Invisibility indicator
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(2.dp)
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(SpiritualCyan),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VisibilityOff,
                                        contentDescription = "Hidden",
                                        tint = Color.Black,
                                        modifier = Modifier.size(9.dp)
                                    )
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
                    }
                }
            }
        }
    }
}

@Composable
private fun ChangePasscodeDialog(
    currentPasscode: String,
    onSavePasscode: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, VoidBorder, RoundedCornerShape(20.dp))
                .testTag("change_passcode_dialog"),
            colors = CardDefaults.cardColors(containerColor = VoidDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Secret Vault Passcode",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Enter a 4-digit numeric passcode to unlock the vault from calculator.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 8) newPin = it.filter { char -> char.isDigit() } },
                    label = { Text("New PIN (e.g. 8888)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_pin_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CelestialGold,
                        unfocusedBorderColor = VoidBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = VoidCard,
                        unfocusedContainerColor = VoidCard
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { if (it.length <= 8) confirmPin = it.filter { char -> char.isDigit() } },
                    label = { Text("Confirm New PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("confirm_pin_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CelestialGold,
                        unfocusedBorderColor = VoidBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = VoidCard,
                        unfocusedContainerColor = VoidCard
                    )
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        fontSize = 11.sp,
                        color = Color(0xFFEF4444),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newPin.length < 3) {
                                errorMessage = "PIN must be at least 3 digits"
                            } else if (newPin != confirmPin) {
                                errorMessage = "PINs do not match"
                            } else {
                                onSavePasscode(newPin)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CelestialGold, contentColor = Color.Black)
                    ) {
                        Text("Save PIN", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConcealAppsManagerDialog(
    allApps: List<AppInfo>,
    onToggleHidden: (AppInfo, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = allApps.filter { it.label.contains(searchQuery, ignoreCase = true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(550.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, VoidBorder, RoundedCornerShape(20.dp))
                .testTag("conceal_apps_manager_dialog"),
            colors = CardDefaults.cardColors(containerColor = VoidDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Conceal / Reveal Apps",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Toggle apps to hide them from the main launcher",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search apps...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CelestialGold,
                        unfocusedBorderColor = VoidBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = VoidCard,
                        unfocusedContainerColor = VoidCard
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered, key = { it.packageName }) { app ->
                        val iconBitmap = remember(app.icon) {
                            app.icon?.let { drawable ->
                                try {
                                    drawable.toBitmap(width = 72, height = 72, config = Bitmap.Config.ARGB_8888).asImageBitmap()
                                } catch (e: Exception) {
                                    null
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (app.isHidden) SpiritualCyan.copy(alpha = 0.15f) else VoidCard)
                                .border(1.dp, if (app.isHidden) SpiritualCyan.copy(alpha = 0.5f) else VoidBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (iconBitmap != null) {
                                Image(
                                    bitmap = iconBitmap,
                                    contentDescription = app.label,
                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                                )
                            } else {
                                Box(
                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(CelestialGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(app.label.take(1), fontWeight = FontWeight.Bold, color = CelestialGold)
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (app.isHidden) "Concealed in Secret Vault" else "Visible in Launcher",
                                    fontSize = 11.sp,
                                    color = if (app.isHidden) SpiritualCyan else TextSecondary
                                )
                            }

                            Switch(
                                checked = app.isHidden,
                                onCheckedChange = { isChecked ->
                                    onToggleHidden(app, isChecked)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = SpiritualCyan,
                                    checkedTrackColor = SpiritualCyan.copy(alpha = 0.4f),
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = VoidBorder
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun evaluateMathExpression(expr: String): String {
    if (expr.isEmpty()) return "0"
    return try {
        val clean = expr.replace("×", "*").replace("÷", "/")
        val tokens = clean.split(" ").filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return "0"

        var total = tokens[0].toDoubleOrNull() ?: return "0"
        var i = 1
        while (i < tokens.size) {
            val op = tokens[i]
            val nextVal = tokens.getOrNull(i + 1)?.toDoubleOrNull() ?: break
            when (op) {
                "+" -> total += nextVal
                "-" -> total -= nextVal
                "*" -> total *= nextVal
                "/" -> {
                    if (nextVal == 0.0) return "Error"
                    total /= nextVal
                }
            }
            i += 2
        }
        val formatted = String.format("%.4f", total).trimEnd('0').trimEnd('.')
        formatted.ifEmpty { "0" }
    } catch (e: Exception) {
        "Error"
    }
}
