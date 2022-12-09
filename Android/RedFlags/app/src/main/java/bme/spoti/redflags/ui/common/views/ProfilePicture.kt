package bme.spoti.redflags.ui.common.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.R
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun SmallProfilePicture(url: String?){
	Box(
		modifier = Modifier
			.padding(vertical = PaddingSizes.default)
			.size(60.dp)
			.background(Colors.Darker, RoundedCornerShape(40))
			.padding(6.dp)
			.clip(
				RoundedCornerShape(40)
			)
	) {
		GlideImage(
			imageModel = url,
			placeHolder = painterResource(id = R.drawable.cat_default)
		)
	}
}