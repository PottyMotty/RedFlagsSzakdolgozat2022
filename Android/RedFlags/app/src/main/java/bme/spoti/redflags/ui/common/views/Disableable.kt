package bme.spoti.redflags.ui.common.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import bme.spoti.redflags.ui.common.model.ColorScheme
import bme.spoti.redflags.ui.theme.Colors

@Composable
fun Disableable(
	disableCondition: ()->Boolean,
	colorScheme: ColorScheme,
	component: @Composable (Color, Color)->Unit
){
	val buttonBackgroundColor: Color by animateColorAsState(
		if (disableCondition()) colorScheme.disabledBackground else colorScheme.background,
		tween(300, easing = EaseInOutQuad)
	)
	val buttonForegroundColor: Color by animateColorAsState(
		if (disableCondition()) colorScheme.disabledForeground else colorScheme.foreground,
		tween(300, easing = EaseInOutQuad)
	)
	component(buttonBackgroundColor,buttonForegroundColor)
}