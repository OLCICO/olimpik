package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ModernLightColorScheme = lightColorScheme(
    primary = VibrantPurplePrimary,
    onPrimary = White,
    primaryContainer = VibrantPurpleSoft,
    onPrimaryContainer = VibrantPurpleDark,
    secondary = VibrantCoral,
    onSecondary = White,
    secondaryContainer = VibrantCoralSoft,
    onSecondaryContainer = VibrantCoralOrange,
    tertiary = VibrantCoralOrange,
    onTertiary = White,
    background = PorcelainBg,
    onBackground = PorcelainTextDark,
    surface = PorcelainCard,
    onSurface = PorcelainTextDark,
    surfaceVariant = PorcelainBg,
    onSurfaceVariant = PorcelainTextMuted,
    outline = PorcelainBorder
)

@Composable
fun OlimpikSporTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    sportBranch: String = "ALL", // "BASKETBALL", "VOLLEYBALL", "ALL"
    content: @Composable () -> Unit
) {
    val branchAccent = when (sportBranch) {
        "BASKETBALL" -> VibrantCoralOrange
        "VOLLEYBALL" -> VibrantPurplePrimary
        else -> VibrantPurpleLight
    }

    val dynamicScheme = ModernLightColorScheme.copy(
        tertiary = branchAccent
    )

    MaterialTheme(
        colorScheme = dynamicScheme,
        typography = Typography,
        content = content
    )
}
