package bme.spoti.redflags.ui.common.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.R
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes

@Composable
fun CallToActionButton(callToActionText: String, isPressable: Boolean,backgroundColor: Color,foregroundColor: Color, callToAction: () -> Unit) {
	val buttonBackgroundColor: Color by animateColorAsState(
		if (isPressable) backgroundColor else Colors.LightRedDarker,
		tween(300, easing = EaseInOutQuad)
	)
	val buttonForegroundColor: Color by animateColorAsState(
		if (isPressable) foregroundColor else Colors.LightRedDarkerDarker,
		tween(300, easing = EaseInOutQuad)
	)
	val infiniteTransition = rememberInfiniteTransition()
	val xOffset by infiniteTransition.animateFloat(
		initialValue = if (isPressable) -4f else 0f,
		targetValue = if (isPressable) 4f else 0f,
		animationSpec = infiniteRepeatable(
			animation = tween(400, easing = EaseInOutQuad),
			repeatMode = RepeatMode.Reverse
		)
	)
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight()
			.clip(MaterialTheme.shapes.large)
			.clickable(
				enabled = isPressable
			) {
				callToAction()
			}
			.background(buttonBackgroundColor, MaterialTheme.shapes.large),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = callToActionText,
			modifier = Modifier.padding(
				horizontal = PaddingSizes.looser,
				vertical = PaddingSizes.loose
			),
			style = MaterialTheme.typography.button,
			color = buttonForegroundColor
		)
		Spacer(modifier = Modifier.weight(1f))
		Icon(
			painter = painterResource(id = R.drawable.ic_arrow),
			contentDescription = "arrow",
			tint = buttonForegroundColor,
			modifier = Modifier
				.padding(end = PaddingSizes.loosest)
				.offset(x =  xOffset.dp)
		)
	}
}
