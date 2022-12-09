package bme.spoti.redflags.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import bme.spoti.redflags.R

val workSansFont = FontFamily(
	Font(R.font.worksans_black, FontWeight.Black),
	Font(R.font.worksans_bold, FontWeight.Bold),
	Font(R.font.worksans_extra_bold, FontWeight.ExtraBold),
	Font(R.font.worksans_light, FontWeight.Light),
	Font(R.font.worksans_medium, FontWeight.Medium),
	Font(R.font.worksans_regular, FontWeight.Normal),
	Font(R.font.worksans_semi_bold, FontWeight.SemiBold),
)


val RedFlagsTypography = Typography(
	defaultFontFamily = workSansFont,
	h1 = TextStyle(
		fontWeight = FontWeight.Black,
		fontSize = 50.sp
	),
	subtitle1 = TextStyle(
		fontWeight = FontWeight.Light,
		fontSize = 20.sp,
	),
	button = TextStyle(
		fontWeight = FontWeight.Bold,
		fontSize = 25.sp,
	),
	h2 = TextStyle(
		fontWeight = FontWeight.ExtraBold,
		fontSize = 30.sp
	),
	h5 = TextStyle(
		fontWeight = FontWeight.SemiBold,
		fontSize = 20.sp
	),
	body2 = TextStyle(
		fontWeight = FontWeight.Bold,
		fontSize = 20.sp
	),
	body1 = TextStyle(
		fontWeight = FontWeight.Bold,
		fontSize = 23.sp
	),
	h3 = TextStyle(
		fontWeight = FontWeight.Normal,
		fontSize = 30.sp
	),
	h4 = TextStyle(
		fontWeight = FontWeight.Black,
		fontSize = 20.sp
	),
	subtitle2 = TextStyle(
		fontWeight = FontWeight.Normal,
		fontSize = 15.sp
	),
	h6 = TextStyle(
		fontWeight = FontWeight.Normal,
		fontSize =20.sp
	)

)