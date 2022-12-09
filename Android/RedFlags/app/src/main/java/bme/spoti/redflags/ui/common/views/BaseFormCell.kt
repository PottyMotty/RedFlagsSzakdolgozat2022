package bme.spoti.redflags.ui.common.views

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes

@Composable
fun BaseFormCell(headerText: String, component: @Composable ()->Unit){
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.background(Colors.Sand, MaterialTheme.shapes.large)
	) {
		Column(
			Modifier
				.padding(PaddingSizes.loose)
				.animateContentSize(animationSpec = tween(300, easing = EaseInOutCubic))) {
			Text(
				modifier =Modifier.padding(bottom = PaddingSizes.tight),
				text = headerText,
				color = Colors.DarkRed,
				style = MaterialTheme.typography.body2
			)
			Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.Center) {
				component()
			}
		}
	}
}