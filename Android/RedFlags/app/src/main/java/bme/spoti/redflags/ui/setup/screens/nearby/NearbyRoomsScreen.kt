package bme.spoti.redflags.ui.setup.screens.nearby

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bme.spoti.redflags.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import bme.spoti.redflags.data.model.NearbyRoomInfo
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes

@Composable
fun NearbyRoom(info: NearbyRoomInfo, onClick : ()->Unit){
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(Colors.Sand, MaterialTheme.shapes.large)
			.clip(MaterialTheme.shapes.large)
			.clickable { onClick() }
			.padding(horizontal = PaddingSizes.looser, vertical = PaddingSizes.default),
		verticalAlignment = Alignment.CenterVertically
	){
		Column(modifier = Modifier.weight(1f),verticalArrangement = Arrangement.Center) {
			Text(
				text = info.roomCode,
				style = MaterialTheme.typography.h2,
				color = Colors.DarkRed
			)
			Text(
				text ="Players: ${info.playersInRoom}/8",
				style = MaterialTheme.typography.h6,
				color = Colors.DarkRed,
				modifier = Modifier.offset(y = -PaddingSizes.tight)
			)
		}
		if(info.isPasswordProtected) {
			Icon(
				painter = painterResource(id = R.drawable.ic_lock),
				contentDescription = "Lock",
				tint = Colors.DarkRed,
				modifier = Modifier.padding(PaddingSizes.tight)
			)
		}
	}
}