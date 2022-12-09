package bme.spoti.redflags.ui.setup.screens.landing

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import bme.spoti.redflags.ui.theme.Colors
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import bme.spoti.redflags.R
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.ui.common.shortCursorBrush
import bme.spoti.redflags.ui.common.views.RedFlagsInputField
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme


@Composable
fun RoomCodeInput(codeInput: String, onCodeChange:(String)->Unit, onStartClicked:()->Unit) {
	val isPressable = codeInput.length == 6
	val buttonBackgroundColor: Color by animateColorAsState(
		if (isPressable) Colors.OkGreen else Colors.SandDarker,
		tween(200, easing = EaseIn)
	)
	val buttonIconColor: Color by animateColorAsState(
		if (isPressable) Colors.OkGreenDark else Colors.SandDarkerDarker,
		tween(200, easing = EaseIn)
	)
	Column(
		modifier = Modifier
			.background(Colors.Sand, shape = MaterialTheme.shapes.large)
			.fillMaxWidth()
	) {
		Text(
			text = "Red Flags",
			modifier = Modifier
				.padding(horizontal = PaddingSizes.looser)
				.padding(top = PaddingSizes.default),
			color = Colors.PrimaryRed,
			style = MaterialTheme.typography.h1
		)
		Text(
			text = "The game of less than ideal dates",
			modifier = Modifier
				.padding(horizontal = PaddingSizes.looser)
				.offset(y = -PaddingSizes.tight),
			color = Colors.DarkRed,
			style = MaterialTheme.typography.subtitle1
		)
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = PaddingSizes.default)
				.padding(bottom = PaddingSizes.loose)
				.height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically
		) {
			RedFlagsInputField(
				maxChar = 6,
				isCapitalized = true,
				modifier = Modifier
					.weight(1f)
					.padding(start = PaddingSizes.looser, end = PaddingSizes.default),
				value = codeInput,
				onTextChange = { onCodeChange(it) },
				placeholder = {
					Text(
						text = "room code",
						style = MaterialTheme.typography.h2,
						color = Colors.Darker
					)
				},
			)
			Icon(
				modifier = Modifier
					.padding(end = PaddingSizes.looser)
					.fillMaxHeight()
					.aspectRatio(1f)
					.background(buttonBackgroundColor, MaterialTheme.shapes.medium)
					.clip(MaterialTheme.shapes.medium)
					.clickable(
						enabled = isPressable
					) {
						onStartClicked()
					}
					.padding(PaddingSizes.loose),
				painter = painterResource(id = R.drawable.ic_start),
				contentDescription = "start icon",
				tint = buttonIconColor
			)
		}
	}
}



@Composable
fun FooterIcon(iconResourceId: Int, onClick: ()-> Unit){
	Icon(
		modifier = Modifier
			.height(62.dp)
			.aspectRatio(1f)
			.background(Colors.Darker, MaterialTheme.shapes.large)
			.clip(MaterialTheme.shapes.large)
			.clickable {
				onClick()
			}
			.padding(PaddingSizes.loose),
		painter = painterResource(id = iconResourceId),
		contentDescription = "QR code",
		tint = Colors.DarkRed
	)
}




