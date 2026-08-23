package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.CultivationRealm

@Composable
fun CultivationTheme(
    realm: CultivationRealm = CultivationRealm.QI_CONDENSATION,
    content: @Composable () -> Unit
) {
    val realmColorScheme = darkColorScheme(
        primary = realm.primaryColor,
        onPrimary = Color.Black,
        primaryContainer = realm.auraGlowColor,
        onPrimaryContainer = realm.runeColor,
        secondary = realm.secondaryColor,
        onSecondary = Color.Black,
        secondaryContainer = realm.auraGlowColor.copy(alpha = 0.3f),
        onSecondaryContainer = realm.accentColor,
        tertiary = realm.accentColor,
        onTertiary = Color.Black,
        background = VoidDark,
        onBackground = TextPrimary,
        surface = VoidSurface,
        onSurface = TextPrimary,
        surfaceVariant = VoidCard,
        onSurfaceVariant = TextSecondary,
        outline = VoidBorder,
        outlineVariant = realm.primaryColor.copy(alpha = 0.3f)
    )

    MaterialTheme(
        colorScheme = realmColorScheme,
        typography = Typography,
        content = content
    )
}
