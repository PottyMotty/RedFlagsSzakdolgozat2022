package bme.spoti.redflags.ui.ingame.screens.sabotage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.R
import bme.spoti.redflags.data.model.DoneIndicatorInfo
import bme.spoti.redflags.data.model.websocket.DateInfo
import bme.spoti.redflags.data.model.websocket.PlayerState
import bme.spoti.redflags.data.model.websocket.`in`.DateToSabotage
import bme.spoti.redflags.ui.common.views.PopUpFooter
import bme.spoti.redflags.ui.common.views.SmallProfilePicture
import bme.spoti.redflags.ui.ingame.component.TimeLeft
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme




@Composable
fun DateToSabotageFooter(
	personToSabotage: PlayerState?,
	dateToSabotage: DateInfo?
) {
	PopUpFooter(
		extraVisibilityCondition = { dateToSabotage != null },
		visible = { showAction ->
			Row(
				Modifier
					.wrapContentHeight()
					.padding(bottom = PaddingSizes.default)
					.fillMaxWidth()
					.background(Colors.Sand, MaterialTheme.shapes.large)
					.clip(MaterialTheme.shapes.large)
					.clickable {
						showAction()
					}
					.padding(start = PaddingSizes.loose),
				verticalAlignment = Alignment.CenterVertically
			) {
				SmallProfilePicture(url = personToSabotage?.imageURL)
				personToSabotage?.username?.let { name ->
					Text(
						text = name,
						style = MaterialTheme.typography.h2,
						color = Colors.DarkRed,
						modifier = Modifier.padding(start = 10.dp)
					)
				}
			}
		},
		hidden = {
			Column(
				modifier = Modifier
					.wrapContentHeight()
					.fillMaxWidth()
					.background(Colors.AlmostWhite, MaterialTheme.shapes.large)
					.padding(
						horizontal = PaddingSizes.loose,
						vertical = PaddingSizes.looser
					),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				if (dateToSabotage?.positiveAttributes?.isNotEmpty() == true) {
					Text(
						text = dateToSabotage.positiveAttributes[0],
						textAlign = TextAlign.Center,
						color = Colors.DarkRed,
						style = MaterialTheme.typography.body1
					)
					Text(
						text = "&",
						color = Colors.PrimaryRed,
						style = MaterialTheme.typography.h2,
						modifier = Modifier.padding(vertical = 2.dp)
					)
					Text(
						text = dateToSabotage.positiveAttributes[1],
						textAlign = TextAlign.Center,
						color = colorResource(id = R.color.dark_red),
						style = MaterialTheme.typography.body1
					)
				} else {
					Text(
						text = "This player did not hand in anything!\uD83E\uDD28",
						textAlign = TextAlign.Center,
						color = Colors.DarkRed,
						style = MaterialTheme.typography.body1
					)
				}
			}
		})
}
