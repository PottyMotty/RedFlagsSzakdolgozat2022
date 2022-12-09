package bme.spoti.redflags.ui.ingame.screens.waiting_for_reconnect

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.ui.theme.Colors
import dev.romainguy.kotlin.math.Float2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random


data class MovingObject(var pos: Float2, val velocity: Float2){
	fun move(){
		pos += velocity
	}
}
@Composable
fun Movable(composable : @Composable ()->Unit){
	val configuration = LocalConfiguration.current
	val screenPixelDensity = LocalDensity.current
	val screenHeight = configuration.screenHeightDp.dp
	val screenWidth = configuration.screenWidthDp.dp
	var offsetX by remember {
		mutableStateOf(screenWidth/2f)
	}
	var offsetY by remember {
		mutableStateOf(screenHeight/2f)
	}
	var width by remember {
		mutableStateOf(0)
	}
	var height by remember {
		mutableStateOf(0)
	}
	var visible by remember {
		mutableStateOf(false)
	}
	var speedX by remember {
		mutableStateOf(0f)
	}
	var speedY by remember {
		mutableStateOf(0f)
	}
	var draggedOldSpeedX by remember {
		mutableStateOf(0f)
	}
	var draggedOldSpeedY by remember {
		mutableStateOf(0f)
	}
	val scale by animateFloatAsState(targetValue = if(visible) 1f else 0f,
	animationSpec = tween(500,0, EaseInOutCubic))
	val infiniteTransition = rememberInfiniteTransition()
	val rotation by infiniteTransition.animateFloat(
		initialValue = 0f,
		targetValue = 360f,
		animationSpec = infiniteRepeatable(
			animation = tween(3000, easing = LinearEasing),
			repeatMode = RepeatMode.Restart
		)
	)


	LaunchedEffect(key1 = true){
			speedX =((if(Random.nextBoolean()) 1 else -1) * Random.nextInt(10,15)/10f)
			speedY =((if(Random.nextBoolean()) 1 else -1) * Random.nextInt(10,15)/10f)
		visible =true

		while (true){
				if(offsetX>screenWidth-width.dp){
					speedX *= -1
				}
				if(offsetX<0.dp){
					speedX *= -1
				}
				if(offsetY>screenHeight-height.dp){
					speedY *= -1
				}
				if(offsetY<0.dp){
					speedY *= -1
				}
				offsetX +=speedX.dp
				offsetY +=speedY.dp
				delay(10)
			}
	}

		Box(modifier = Modifier
			.wrapContentHeight()
			.wrapContentWidth()
			.onGloballyPositioned { coords ->
				width = (coords.size.width / screenPixelDensity.density).toInt()
				height = (coords.size.height / screenPixelDensity.density).toInt()
			}
			.offset(offsetX, offsetY)
			.scale(scale)
			.pointerInput(Unit) {
				detectDragGestures(
					onDragStart = {
						draggedOldSpeedX = speedX
						draggedOldSpeedY = speedY
						speedX= 0f
						speedY= 0f
					},
					onDragEnd = {
						speedY = draggedOldSpeedY
						speedX = draggedOldSpeedX
					}
				) { change, dragAmount ->
					change.consume()
					val dpX =(dragAmount.x / screenPixelDensity.density).toInt()
					val dpY =(dragAmount.y / screenPixelDensity.density).toInt()
					offsetX += dpX.dp
					offsetY += dpY.dp
				}
			}			.rotate(rotation)


		) {
			composable()
		}

}
@Composable
fun <T> BouncingItemList(
	modifier: Modifier = Modifier, items: List<T>, itemComposable: @Composable (T) -> Unit
) {
	Box(modifier = modifier) {
		items.forEach{
			Movable() {
				itemComposable(it)
			}
		}
	}
}


@Composable
@Preview
fun BouncingTest() {
	Box(
		Modifier
			.fillMaxSize()
			.background(Colors.LightRed)
	) {
		var lists by remember {
			mutableStateOf(listOf(1,2,3,4))
	}
		BouncingItemList(modifier = Modifier.fillMaxSize(),items = lists) { item ->
			Box(
				Modifier
					.size(100.dp)
					.background(Colors.Sand, CircleShape), contentAlignment = Alignment.Center) {
				Text(text = item.toString())
			}
		}
		Button(onClick = {lists = lists.plus(lists.size)}){
			Text(text="press")
		}
	}
}