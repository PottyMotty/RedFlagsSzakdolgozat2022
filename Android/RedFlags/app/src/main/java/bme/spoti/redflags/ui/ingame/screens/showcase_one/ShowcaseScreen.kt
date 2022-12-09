package bme.spoti.redflags.ui.ingame.screens.showcase_one

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.google.accompanist.pager.calculateCurrentOffsetForPage

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.gestures.verticalDrag
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange

import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.util.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import bme.spoti.redflags.R
import bme.spoti.redflags.data.model.websocket.DateInfo
import bme.spoti.redflags.ui.ingame.component.Date
import com.google.accompanist.pager.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor

@OptIn(ExperimentalPagerApi::class, ExperimentalFoundationApi::class)
@Preview
@Composable
fun ShowcaseOneScreenPreview() {
	RedFlagsTheme {
		Box(modifier = Modifier
			.fillMaxSize()
			.background(Colors.LightRed), contentAlignment = Alignment.Center)
		{
		}
	}
}

fun dipFunction(dipRadius: Float = 1f, dipPoint: Float, at: Float): Float {
	val dipValue = abs(dipPoint - at)
	return (dipValue / dipRadius).coerceIn(0f, 1f)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HeartPageIndicator(pagerState: PagerState) {
	val currentIndex = pagerState.currentPage + pagerState.currentPageOffset
	var lastIndexValue by rememberSaveable {
		mutableStateOf<Float>(currentIndex)
	}
	var positions by remember {
		mutableStateOf<List<Float>>(emptyList())
	}
	var positionMeasured by remember {
		mutableStateOf(false)
	}

	lastIndexValue = currentIndex
	val context = LocalContext.current
	val screenPixelDensity = context.resources.displayMetrics.density
	val ceil: Float = if (positions.isNotEmpty()) positions[ceil(currentIndex).toInt()] else 0f
	val floor: Float = if (positions.isNotEmpty()) positions[floor(currentIndex).toInt()] else 0f
	val fraction = (currentIndex % 1)
	val currentValue = floor + (ceil - floor) * if (fraction > 0f) fraction else 1f

	Box(contentAlignment = Alignment.CenterStart) {
		Row(Modifier, horizontalArrangement = Arrangement.spacedBy(PaddingSizes.loose)) {
			repeat(pagerState.pageCount) {
				val padding = dipFunction(
					dipRadius = 32f,
					dipPoint = currentValue,
					if (positions.isNotEmpty()) positions[it] else 0f
				)
				Box(
					modifier = Modifier
						.size(18.dp)
						.onGloballyPositioned { coords ->
							if (!positionMeasured) {
								val dpVal = coords.positionInParent().x / screenPixelDensity
								positions = positions.plus(dpVal)
								println(dpVal)
							}
							if (positions.size == pagerState.pageCount) {
								positionMeasured = true
								positions = positions.sorted()
							}
						}
						.padding((18 - (padding * 18)).dp)
						.background(Colors.LightRedDarker, CircleShape)

				)
			}
		}
		if (positionMeasured) {
			Icon(
				painter = painterResource(id = R.drawable.ic_heart),
				contentDescription = "heart",
				modifier = Modifier
					.size(26.dp)
					.offset(x = currentValue.dp - 4.dp),
				tint = Colors.PrimaryRed
			)
		}
	}
}

