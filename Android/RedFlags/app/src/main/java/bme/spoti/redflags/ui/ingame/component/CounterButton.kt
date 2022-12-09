package bme.spoti.redflags.ui.ingame.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bme.spoti.redflags.R
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.RedFlagsTheme


enum class ViewState() {
	COUNTER, CHECKMARK, DONEWORKING
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationGraphicsApi::class)
@Composable
fun CounterButton(
	count: Int,
	maxCount: Int,
	doneCreating: Boolean,
	onClickCheckmark: () -> Unit,
	onClickResume: () -> Unit,
) {
	var viewState by remember { mutableStateOf(ViewState.COUNTER) }
	LaunchedEffect(count) {
		viewState = if (count == maxCount) {
			ViewState.CHECKMARK
		} else {
			ViewState.COUNTER
		}
	}
	var atEnd by remember { mutableStateOf(false) }
	LaunchedEffect(doneCreating) {
		if(doneCreating){
			viewState = ViewState.DONEWORKING
			atEnd = true
		}
	}

	val isCounter = count < maxCount
	val w: Dp by animateDpAsState(
		targetValue = when (viewState) {
			ViewState.COUNTER -> 150.dp
			ViewState.CHECKMARK -> 70.dp
			ViewState.DONEWORKING -> 80.dp
		},
		tween(400)
	)
	val h: Dp by animateDpAsState(
		targetValue = when (viewState) {
			ViewState.COUNTER -> 70.dp
			ViewState.CHECKMARK -> 70.dp
			ViewState.DONEWORKING -> 80.dp
		},
		tween(400)
	)
	val color: Color by animateColorAsState(
		targetValue = when (viewState) {
			ViewState.COUNTER -> Colors.LightRedDarker
			ViewState.CHECKMARK -> Colors.OkGreen
			ViewState.DONEWORKING -> colorResource(id = R.color.weird_yellow)
		},
		tween(400)
	)
	val alpha: Float by animateFloatAsState(
		targetValue = when (isCounter) {
			true -> 0f
			false -> 1f
		},
		tween(100)
	)
	RedFlagsTheme() {
		Box(
			Modifier
				.fillMaxWidth()
				.padding(bottom = 5.dp), contentAlignment = Alignment.Center
		) {
			val image = AnimatedImageVector.animatedVectorResource(R.drawable.avd_check_to_close)
			Card(
				modifier = Modifier
					.width(w)
					.height(h),
				backgroundColor = color,
				shape = RoundedCornerShape(50),
				onClick = {
					if (viewState == ViewState.CHECKMARK) {
						onClickCheckmark()
						viewState = ViewState.DONEWORKING
						atEnd = true
					} else if (viewState == ViewState.DONEWORKING) {
						onClickResume()
						viewState = ViewState.CHECKMARK
						atEnd = false
					}
				}
			) {}

			if (isCounter) {
				Text(
					text = "${count}/${maxCount}",
					style = MaterialTheme.typography.button,
					modifier = Modifier
						.fillMaxWidth()
						.alpha(1f - alpha),
					textAlign = TextAlign.Center,
					color = Colors.DarkRed
				)
			} else {
				Icon(
					painter = rememberAnimatedVectorPainter(image, atEnd),
					contentDescription = null,
					modifier = Modifier
						.size(40.dp)
						.alpha(alpha),
					tint = Colors.OkGreenDark
				)
			}
		}
	}
}