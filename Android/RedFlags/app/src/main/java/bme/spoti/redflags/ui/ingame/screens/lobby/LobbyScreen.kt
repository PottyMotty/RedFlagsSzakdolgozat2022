package bme.spoti.redflags.ui.ingame.screens.lobby

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import bme.spoti.redflags.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.data.Game
import bme.spoti.redflags.data.model.websocket.PlayerState
import bme.spoti.redflags.events.UIEvent
import bme.spoti.redflags.ui.common.model.ColorScheme
import bme.spoti.redflags.ui.common.views.Disableable
import bme.spoti.redflags.ui.common.views.SmallProfilePicture
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.MutableSharedFlow

@Preview
@Composable
fun LobbyScreenPreview() {
	RedFlagsTheme() {
		val playersInLobby = rememberSaveable {
			mutableStateOf(
				listOf(
					PlayerState(
						"Spoti",
						"https://placekitten.com/g/200/300",
						true,
						true
					),
					PlayerState(
						"Masik",
						"https://placekitten.com/g/300/300",
						false,
						true
					)
				)
			)
		}
		Box(
			Modifier
				.background(Colors.LightRed)
				.fillMaxSize(), contentAlignment = Alignment.TopStart
		) {
			Column(
				Modifier
					.padding(horizontal = PaddingSizes.loose)
					.padding(top = PaddingSizes.looser),
				verticalArrangement = Arrangement.spacedBy(PaddingSizes.default)
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.padding(vertical = PaddingSizes.loose)
				) {
					Text(
						text = "lobby",
						style = MaterialTheme.typography.h2,
						color = Colors.PrimaryRed,
						modifier = Modifier.weight(1f)
					)
					Text(
						text = "3/8",
						style = MaterialTheme.typography.h3,
						color = Colors.DarkRed
					)

				}
				LazyColumn(
					modifier = Modifier
						.fillMaxWidth()
						.weight(1f),
					verticalArrangement = Arrangement.spacedBy(PaddingSizes.default)
				) {
					items(playersInLobby.value) { player ->
						LobbyItem(player = player)
					}
				}
				LobbyFooter(
					roomCode = "Z6WYX5",
					phase = Game.Phase.PLAYERS_GATHERED,
					isRoomOwner = true,
					isSharingRoom = true,
					eventOutputFlow = MutableSharedFlow(),
					popUpAction = {},
					sharingClicked = {}
				)
				Spacer(modifier = Modifier.height(PaddingSizes.loose))
			}
		}
	}
}

@Composable
fun LobbyFooter(
	roomCode: String,
	phase: Game.Phase?,
	isRoomOwner: Boolean,
	isSharingRoom: Boolean,
	eventOutputFlow: MutableSharedFlow<UIEvent>,
	popUpAction: ()->Unit,
	sharingClicked : ()->Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight()
			.background(Colors.Sand, MaterialTheme.shapes.large)
			.padding(horizontal = PaddingSizes.looser, vertical = PaddingSizes.loose)
	) {
		Row(verticalAlignment = Alignment.CenterVertically) {
			AnimatedVisibility(visible = isRoomOwner) {
				Disableable(
					disableCondition = { !isSharingRoom },
					colorScheme = ColorScheme(
						background = Colors.OkGreen,
						foreground = Colors.OkGreenDark,
						disabledBackground = Colors.SandDarker,
						disabledForeground = Colors.SandDarkerDarker
					)
				) { background, foreground ->
					Icon(
						painter = painterResource(id = R.drawable.ic_wireless),
						contentDescription = "Wireless share",
						tint = foreground,
						modifier = Modifier
							.padding(end = PaddingSizes.default)
							.height(36.dp)
							.aspectRatio(1f)
							.background(background, CircleShape)
							.clip(CircleShape)
							.clickable {
								sharingClicked()
							}
							.padding(PaddingSizes.tight)
					)
				}
			}
			if(phase!=null) {
				Text(
					text = if (phase == Game.Phase.WAITING_FOR_PLAYERS) "Waiting for players..." else "Players gathered!",
					style = MaterialTheme.typography.body2,
					color = Colors.DarkRed
				)
			}
		}
		Spacer(modifier = Modifier.height(PaddingSizes.default))
		Row(Modifier.height(IntrinsicSize.Min)){
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.background(Colors.Darker, MaterialTheme.shapes.large)
					.clip(MaterialTheme.shapes.large)
					.clickable { popUpAction() }
					.padding(horizontal = PaddingSizes.loose, vertical = PaddingSizes.tighter)

			){
				Text(
					text = roomCode,
					style = MaterialTheme.typography.h2,
					color = Colors.DarkRed,
					 modifier = Modifier.offset(y = (-1).dp)
				)
				if(!isRoomOwner) {
					Spacer(modifier = Modifier.weight(1f))
				}
				Icon(
					painter = painterResource(id = R.drawable.ic_qr),
					contentDescription = "QR",
					tint = Colors.DarkRed,
					modifier = Modifier
						.padding(start = PaddingSizes.default)
						.size(24.dp)
				)
			}
			Spacer(modifier = Modifier.width(PaddingSizes.default))
			Disableable(
				disableCondition = { phase != Game.Phase.PLAYERS_GATHERED },
				colorScheme = ColorScheme(
					background =Colors.OkGreen,
					foreground =Colors.OkGreenDark,
					disabledBackground =Colors.SandDarker,
					disabledForeground =Colors.SandDarkerDarker
				)
			) { background, foreground ->
				Icon(
					painter = painterResource(id = R.drawable.ic_start),
					contentDescription = "Start",
					tint = foreground,
					modifier = Modifier
						.weight(1f)
						.fillMaxHeight()
						.background(background, MaterialTheme.shapes.large)
						.clip(MaterialTheme.shapes.large)
						.clickable(
							phase == Game.Phase.PLAYERS_GATHERED
						) {
							eventOutputFlow.tryEmit(StartGameRequest)
						}
						.padding(vertical = PaddingSizes.default)
				)
			}

		}
	}
}

@Composable
fun LobbyItem(player: PlayerState) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = PaddingSizes.default)
			.wrapContentHeight()
			.background(Colors.Sand, MaterialTheme.shapes.large)
			.padding(start = PaddingSizes.looser, end = PaddingSizes.loose),
		verticalAlignment = Alignment.CenterVertically
	) {
		AnimatedVisibility(visible = player.roomOwner) {
			Icon(
				painter = painterResource(id = R.drawable.ic_crown),
				contentDescription = "Crown",
				tint = Colors.DarkRed,
				modifier = Modifier
					.padding(end = PaddingSizes.default)
					.height(40.dp)
					.aspectRatio(1f)
					.background(Colors.Darker, MaterialTheme.shapes.medium)
					.clip(MaterialTheme.shapes.medium)
					.padding(PaddingSizes.default)
			)
		}
		Text(
			text = player.username,
			style = MaterialTheme.typography.button,
			color = Colors.DarkRed,
			modifier = Modifier.padding(vertical = PaddingSizes.loose)
		)
		Spacer(modifier = Modifier.weight(1f))
		SmallProfilePicture(player.imageURL)

	}
}