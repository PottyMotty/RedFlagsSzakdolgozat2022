package bme.spoti.redflags.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun RedFlagsTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
	MaterialTheme(
		colors = lightColors(
			primary = Colors.PrimaryRed,
			background = Colors.LightRed,
			secondary = Colors.Sand
		),
		typography = RedFlagsTypography,
		shapes = Shapes,
		content = content
	)
}