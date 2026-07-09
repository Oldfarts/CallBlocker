package com.example.callblocker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ---- Sinun väripaletti (sama kuin colors.xml) ----

val BluePrimary = Color(0xFF1E3A8A)
val BluePrimaryDark = Color(0xFF111827)
val RedAccent = Color(0xFFEF4444)
val BackgroundLight = Color(0xFFF3F4F6)
val CardWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF111827)
val TextSecondary = Color(0xFF4B5563)
val PositiveGreen = Color(0xFF10B981)
val ButtonBlue = Color(0xFF2563EB)

// ---- Light mode ----
private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    secondary = ButtonBlue,
    tertiary = PositiveGreen,

    background = BackgroundLight,
    surface = CardWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

// ---- Dark mode ----
private val DarkColorScheme = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = Color.White,
    secondary = ButtonBlue,
    tertiary = PositiveGreen,

    background = Color(0xFF0F0F0F),
    surface = Color(0xFF1C1C1C),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun CallBlockerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // pois päältä → käytetään sinun värejä
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
