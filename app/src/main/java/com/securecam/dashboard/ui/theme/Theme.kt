package com.securecam.dashboard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme()

@Composable
fun SecureCamTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val useDark = darkTheme || isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
