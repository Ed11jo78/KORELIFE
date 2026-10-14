package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Primary Emerald Branding
val EmeraldPrimary = Color(0xFF10B981)
val EmeraldOnPrimary = Color(0xFF022C22)
val EmeraldContainer = Color(0xFF064E3B)
val EmeraldOnContainer = Color(0xFF6EE7B7)

// Status & Accents
val IndigoSecondary = Color(0xFF6366F1)
val AmberTertiary = Color(0xFFF59E0B)
val AmberWarning = Color(0xFFF59E0B)
val RedError = Color(0xFFEF4444)

// Static Dark Mode Palette
val DarkBackgroundValue = Color(0xFF020617)
val DarkSurfaceValue = Color(0xFF0F172A)
val DarkSurfaceVariantValue = Color(0xFF1E293B)
val DarkBorderValue = Color(0xFF334155)
val TextWhiteValue = Color(0xFFFFFFFF)
val TextMutedValue = Color(0xFF94A3B8)
val TextSubtleValue = Color(0xFF64748B)

// Static Light Mode Palette
val LightBackgroundValue = Color(0xFFF8FAFC)     // Slate-50: limpio y moderno
val LightSurfaceValue = Color(0xFFFFFFFF)        // Blanco puro para tarjetas
val LightSurfaceVariantValue = Color(0xFFF1F5F9)// Slate-100 para chips e inputs
val LightBorderValue = Color(0xFFE2E8F0)        // Slate-200 para bordes nítidos
val LightTextPrimaryValue = Color(0xFF0F172A)    // Slate-900: texto principal legible
val LightTextMutedValue = Color(0xFF64748B)      // Slate-500: texto secundario
val LightTextSubtleValue = Color(0xFF94A3B8)     // Slate-400: subtítulos sutiles

/**
 * Dynamic Material 3 Adaptors:
 * When accessed from any Composable, these properties evaluate to the active
 * MaterialTheme colorScheme, enabling seamless instant switching between Light and Dark mode.
 */
val DarkBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val DarkSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val DarkSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

val DarkBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

val TextWhite: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onBackground

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val TextSubtle: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == LightBackgroundValue) LightTextSubtleValue else TextSubtleValue
