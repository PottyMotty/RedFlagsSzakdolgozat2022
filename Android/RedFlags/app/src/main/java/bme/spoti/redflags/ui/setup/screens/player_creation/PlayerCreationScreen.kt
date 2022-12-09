package bme.spoti.redflags.ui.setup.screens.player_creation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.R
import bme.spoti.redflags.ui.common.views.BaseFormCell
import bme.spoti.redflags.ui.common.views.RedFlagsInputField
import bme.spoti.redflags.ui.common.views.TextInputWithHeader
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage


@Composable
fun ProfilePictureSelector(imageUrl: String, onClicked: () -> Unit) {
	Row(
		modifier = Modifier.height(IntrinsicSize.Min),
		horizontalArrangement = Arrangement.spacedBy(PaddingSizes.default)
	) {
		Box(
			modifier = Modifier
				.weight(3f)
				.aspectRatio(1f)
				.background(Colors.Sand, MaterialTheme.shapes.large)
				.padding(PaddingSizes.loose)
		) {
			GlideImage(
				imageModel = imageUrl,
				loading = {
					Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
					CircularProgressIndicator(
						color = Colors.DarkRed
					)
				}
				},
				circularReveal = CircularReveal(400),
				modifier = Modifier
					.clip(MaterialTheme.shapes.medium)
					.clickable {
						onClicked()
					}
			)
		}
		Column(
			modifier = Modifier
				.weight(1f)
				.fillMaxHeight()
				.background(Colors.Darker, MaterialTheme.shapes.large)
				.padding(top = PaddingSizes.loose),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = "YOU?",
				style = MaterialTheme.typography.h4,
				color = Colors.DarkRed
			)
			Icon(
				painter = painterResource(id = R.drawable.ic_arrow),
				contentDescription = "arrow",
				tint = Colors.DarkRed,
				modifier = Modifier
					.rotate(180f)
					.padding(top = PaddingSizes.tight)
			)
		}
	}
}

