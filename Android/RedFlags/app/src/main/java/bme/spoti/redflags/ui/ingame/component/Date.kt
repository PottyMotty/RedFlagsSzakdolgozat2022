package bme.spoti.redflags.ui.ingame.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bme.spoti.redflags.R
import bme.spoti.redflags.data.model.websocket.DateInfo
import bme.spoti.redflags.other.Fonts
import bme.spoti.redflags.ui.common.views.SmallProfilePicture
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun Date(dateInfo: DateInfo, creatorImage: String?, sabotagorImage: String? = null) {
	Column(verticalArrangement = Arrangement.spacedBy(PaddingSizes.default)) {
		Row(
			Modifier
				.wrapContentHeight()
				.fillMaxWidth()
				.background(Colors.Sand, MaterialTheme.shapes.large)
				.padding(start = PaddingSizes.loose),
			verticalAlignment = Alignment.CenterVertically
		) {
			SmallProfilePicture(url = creatorImage)
			Text(
				text = dateInfo.createdBy,
				style = MaterialTheme.typography.h2,
				color = Colors.DarkRed,
				modifier = Modifier.padding(start = 10.dp)
			)
		}
		Column(
			modifier = Modifier
				.wrapContentHeight()
				.fillMaxWidth()
				.background(Colors.AlmostWhite, MaterialTheme.shapes.large)
				.padding(horizontal = PaddingSizes.loose, vertical = PaddingSizes.looser),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			if (dateInfo.positiveAttributes.isNotEmpty()) {
				Text(
					text = dateInfo.positiveAttributes[0],
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
					text = dateInfo.positiveAttributes[1],
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
		if (dateInfo.sabotagedBy != null) {
			Row(
				Modifier
					.wrapContentHeight()
					.fillMaxWidth()
					.background(Colors.Sand, MaterialTheme.shapes.large)
					.padding(start = PaddingSizes.loose),
				verticalAlignment = Alignment.CenterVertically
			) {
				SmallProfilePicture(url = sabotagorImage)
				Text(
					text = dateInfo.sabotagedBy,
					style = MaterialTheme.typography.h2,
					color = Colors.DarkRed,
					modifier = Modifier.padding(start = 10.dp)
				)
			}
			Column(
				modifier = Modifier
					.wrapContentHeight()
					.fillMaxWidth()
					.background(Colors.PrimaryRed, MaterialTheme.shapes.large)
					.padding(horizontal = PaddingSizes.loose, vertical = PaddingSizes.looser),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = dateInfo.negativeAttribute
						?: "This player did not hand in anything!\uD83E\uDD28",
					textAlign = TextAlign.Center,
					color = Colors.DarkRed,
					style = MaterialTheme.typography.body1
				)
			}
		}
	}
}


