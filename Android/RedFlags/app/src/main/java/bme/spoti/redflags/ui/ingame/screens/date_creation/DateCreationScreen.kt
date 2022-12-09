package bme.spoti.redflags.ui.ingame.screens.date_creation

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.data.model.DoneIndicatorInfo
import bme.spoti.redflags.ui.ingame.component.TimeLeft
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme

@Composable
fun DateCreationTobBar(
	title: String,
	maxTime: Long?,
	remainingTime: Long?,
	doneInfo: DoneIndicatorInfo
) {
	RedFlagsTheme() {
		Column(
			modifier = Modifier
				.padding(top = PaddingSizes.loose)
				.fillMaxWidth()
				.padding(horizontal = PaddingSizes.loose)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = PaddingSizes.tighter)
			) {
				Text(
					text = title,
					color = Colors.PrimaryRed,
					style = MaterialTheme.typography.h2,
					modifier = Modifier.weight(1f)
				)
				Text(
					text = "${doneInfo.done}/${doneInfo.all}",
					style = MaterialTheme.typography.h3,
					color = Colors.DarkRed
				)
			}
			TimeLeft(
				maximumTime = maxTime, currentTime = remainingTime, modifier = Modifier
					.height(10.dp)
					.fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(50.dp))
		}
	}
}