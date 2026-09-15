package com.rankinenvironments.realms.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RealmsColors = darkColorScheme(
    primary = Color(0xFFE5D7B5),
    onPrimary = Color(0xFF17130B),
    background = Color(0xFF080B0C),
    onBackground = Color(0xFFE9ECEA),
    surface = Color(0xFF101516),
    onSurface = Color(0xFFE9ECEA),
    surfaceVariant = Color(0xFF192021),
    onSurfaceVariant = Color(0xFFBCC7C5)
)

@Composable
fun RankinRealmsTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = RealmsColors, content = content)
}
