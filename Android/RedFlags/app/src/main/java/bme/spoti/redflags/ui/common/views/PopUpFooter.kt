package bme.spoti.redflags.ui.common.views

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.IntOffset
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme

@Composable
fun PopUpFooter(
	hiddenOn : () -> Boolean = {false},
	extraVisibilityCondition: () -> Boolean = { true },
	visible: @Composable (() -> Unit) -> Unit,
	hidden: @Composable () -> Unit
) {
	RedFlagsTheme {
		var show by rememberSaveable {
			mutableStateOf(false)
		}
		val backgroundAlpha by animateFloatAsState(
			targetValue = if (show) 0.65f else 0f
		)
		AnimatedVisibility(
			visible = show && extraVisibilityCondition(),
			enter = fadeIn(),
			exit = fadeOut()
		) {
			Box(modifier = Modifier
				.fillMaxSize()
				.alpha(backgroundAlpha)
				.background(Colors.DarkRed)
				.clickable { show = false }
			)
		}
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = PaddingSizes.loose),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Bottom
		) {
			AnimatedVisibility(
				visible = !hiddenOn(),
				enter = slideIn( tween(300, 0, EaseOutCubic), initialOffset = {
					IntOffset(0,it.height)
				}),
				exit = slideOut(tween(300,0, EaseOutCubic), targetOffset = {
					IntOffset(0,(it.height*1.2).toInt())
				})
				,
			) {
				visible { show = !show }
			}
			AnimatedVisibility(visible = show && extraVisibilityCondition()) {
				hidden()
			}
			Spacer(Modifier.height(PaddingSizes.looser))
		}
	}
}