package com.securecam.dashboard.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColors = darkColorScheme(
    primary = Color(0xFF80CBC4),
    onPrimary = Color(0xFF003732),
    primaryContainer = Color(0xFF004D47),
    onPrimaryContainer = Color(0xFFA4F2EA),

    secondary = Color(0xFFB0BEC5),
    onSecondary = Color(0xFF1B1F22),
    secondaryContainer = Color(0xFF2C3136),
    onSecondaryContainer = Color(0xFFDEE5EA),

    tertiary = Color(0xFFCE93D8),
    onTertiary = Color(0xFF371A3C),
    tertiaryContainer = Color(0xFF4A2A4F),
    onTertiaryContainer = Color(0xFFF5D9FA),

    background = Color(0xFF0F1214),
    onBackground = Color(0xFFE2E6EA),
    surface = Color(0xFF121417),
    onSurface = Color(0xFFE2E6EA),
    surfaceVariant = Color(0xFF1A1F24),
    onSurfaceVariant = Color(0xFFB7C2CC),

    outline = Color(0xFF3C444B),
    outlineVariant = Color(0xFF2A3238),
    scrim = Color(0x99000000),
    inverseSurface = Color(0xFFE2E6EA),
    inverseOnSurface = Color(0xFF111416),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val AppTypography = Typography(
    // Rely on defaults but tweak weights where needed
    titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.Medium),
    bodyMedium = Typography().bodyMedium.copy(color = Color(0xFFCBD5DF))
)

private val AppShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
)

@Composable
fun SecureCamTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color(0xFF0F1214).toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
    }

    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
