package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.data.model.ThemeMode

val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = EmeraldOnContainer,
    secondary = IndigoSecondary,
    tertiary = AmberTertiary,
    background = DarkBackgroundValue,
    surface = DarkSurfaceValue,
    surfaceVariant = DarkSurfaceVariantValue,
    surfaceContainer = DarkSurfaceValue,
    surfaceContainerHigh = DarkSurfaceVariantValue,
    outline = DarkBorderValue,
    error = RedError,
    onBackground = TextWhiteValue,
    onSurface = TextWhiteValue,
    onSurfaceVariant = TextMutedValue
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF059669), // Emerald 600
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1FAE5), // Emerald 100
    onPrimaryContainer = Color(0xFF065F46), // Emerald 800
    secondary = Color(0xFF4F46E5), // Indigo 600
    tertiary = Color(0xFFD97706),  // Amber 600
    background = LightBackgroundValue,
    surface = LightSurfaceValue,
    surfaceVariant = LightSurfaceVariantValue,
    surfaceContainer = LightSurfaceValue,
    surfaceContainerHigh = Color(0xFFE2E8F0),
    outline = LightBorderValue,
    error = RedError,
    onBackground = LightTextPrimaryValue,
    onSurface = LightTextPrimaryValue,
    onSurfaceVariant = LightTextMutedValue
)

@Composable
fun KoreLifeTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
