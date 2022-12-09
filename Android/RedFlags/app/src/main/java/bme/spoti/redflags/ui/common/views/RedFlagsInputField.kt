package bme.spoti.redflags.ui.common.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardCapitalization
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes

@Composable
fun RedFlagsInputField(
	modifier: Modifier,
	maxChar: Int = 25,
	value: String,
	onTextChange: (String) -> Unit,
	isCapitalized: Boolean = false,
	placeholder: @Composable () -> Unit ={}
) {
	BasicTextField(
		modifier = modifier.wrapContentHeight()
			.background(Colors.Darker, shape = MaterialTheme.shapes.medium),
		textStyle = MaterialTheme.typography.h2.copy(color = Colors.DarkRed),
		cursorBrush = SolidColor(Color.Transparent),
		value = value,
		onValueChange = {
			if (it.count() <= maxChar) {
				onTextChange(it)
			}
		},
		keyboardOptions = KeyboardOptions(capitalization = if (isCapitalized) KeyboardCapitalization.Characters else KeyboardCapitalization.Sentences),
		singleLine = true,
		decorationBox = { innerTextField ->
			Box(
				Modifier
					.fillMaxWidth()
					.padding(vertical = PaddingSizes.tight)
					.padding(horizontal = PaddingSizes.default)
			) {

				if (value.isEmpty()) {
					placeholder()
				}
				innerTextField()
			}
		},
	)
}
