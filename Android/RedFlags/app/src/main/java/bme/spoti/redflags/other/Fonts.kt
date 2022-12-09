package bme.spoti.redflags.other

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import bme.spoti.redflags.R

object Fonts {
    private val gilroyBold = Font(
        R.font.gilroy_extrabold, FontWeight.ExtraBold
    )
    private val gilroyLight = Font(
        R.font.gilroy_light, FontWeight.Light
    )
    private val gilroyItalic = Font(
        R.font.gilroy_lightitalic, FontWeight.Light, FontStyle.Italic
    )
    val gilroy = FontFamily(listOf(gilroyBold, gilroyLight, gilroyItalic))
}