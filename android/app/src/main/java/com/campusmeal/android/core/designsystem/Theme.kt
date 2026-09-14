package com.campusmeal.android.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = CampusMealGreen,
    onPrimary = Color.White,
    secondary = CampusMealOrange,
    onSecondary = Color.White,
    background = CampusMealCream,
    onBackground = CampusMealInk,
    surface = CampusMealCream,
    onSurface = CampusMealInk,
    surfaceVariant = CampusMealMist,
    error = CampusMealErrorLight,
)

private val DarkColors = darkColorScheme(
    primary = CampusMealGreenLight,
    onPrimary = CampusMealGreenDeep,
    secondary = CampusMealOrangeLight,
    onSecondary = CampusMealOrangeDeep,
    background = CampusMealNight,
    onBackground = CampusMealMist,
    surface = CampusMealNight,
    onSurface = CampusMealMist,
    error = CampusMealErrorDark,
)

/** Temporary CampusMeal theme. Dynamic color is disabled so the brand palette stays consistent. */
@Composable
fun CampusMealTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = CampusMealTypography,
        content = content,
    )
}
