package bme.spoti.redflags.ui.ingame.screens.single_reveal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.data.model.websocket.PlayerState
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import com.skydoves.landscapist.glide.GlideImage

@Preview
@Composable
fun SinglScreenPreview() {
	val single by remember {
		mutableStateOf(PlayerState(
			username = "Spoti",
			imageURL = "https://placekitten.com/g/500/500",
			roomOwner = false,
			isConnected = true
		))
	}
	RedFlagsTheme() {
		Box(
			Modifier
				.background(Colors.LightRed)
				.fillMaxSize(), contentAlignment = Alignment.TopCenter
		) {
			if (single != null) {
				Column(
					Modifier
						.padding(horizontal = PaddingSizes.loose)
						.padding(top = 120.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Box(
						modifier = Modifier
							.padding(horizontal = PaddingSizes.loosest)
							.fillMaxWidth()
							.aspectRatio(1f)
							.background(Colors.Darker, RoundedCornerShape(40))
							.padding(15.dp)
							.clip(
								RoundedCornerShape(40)
							)
					) {
						/*GlideImage(
							imageModel = single.imageURL
						)*/
					}
					Spacer(modifier = Modifier.height(PaddingSizes.loose))
					Text(
						text = "The single this round is:",
						style = MaterialTheme.typography.body2,
						color = Colors.DarkRed,
						modifier = Modifier.padding(bottom = PaddingSizes.default)
					)
					Text(
						text = single.username,
						style = MaterialTheme.typography.h2,
						color = Colors.PrimaryRed,
						modifier = Modifier.padding(bottom = PaddingSizes.default)
					)
				}
			}
		}
	}
}