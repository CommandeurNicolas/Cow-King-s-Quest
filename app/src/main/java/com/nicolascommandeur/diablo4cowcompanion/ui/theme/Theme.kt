package com.nicolascommandeur.diablo4cowcompanion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun DiabloCowCounterTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = HuntColors, typography = Typography, content = content)
}
