package bme.spoti.redflags.ui.common.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TextInputWithHeader(headerText: String, value: String, onTextChanged: (String) -> Unit) {
	BaseFormCell(headerText = headerText) {
		RedFlagsInputField(modifier = Modifier
			.fillMaxWidth(),
			value = value,
			onTextChange = { onTextChanged(it) })
	}
}