package com.taller.parcial.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.taller.parcial.view.theme.GolfBackground
import com.taller.parcial.view.theme.GolfGreen
import com.taller.parcial.view.theme.GolfGreenAccent
import com.taller.parcial.view.theme.GolfGreenLight
import com.taller.parcial.view.theme.GolfOnSurface
import com.taller.parcial.view.theme.GolfSurface
import com.taller.parcial.view.theme.GolfWhite

private val LightColorScheme = lightColorScheme(
    primary          = GolfGreen,
    onPrimary        = GolfWhite,
    primaryContainer = GolfGreenAccent,
    secondary        = GolfGreenLight,
    background       = GolfBackground,
    surface          = GolfSurface,
    onSurface        = GolfOnSurface
)

@Composable
fun GolfClubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = GolfTypography,
        content     = content
    )
}