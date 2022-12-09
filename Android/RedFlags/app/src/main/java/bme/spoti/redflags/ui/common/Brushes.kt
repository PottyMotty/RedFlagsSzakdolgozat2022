package bme.spoti.redflags.ui.common

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import bme.spoti.redflags.ui.theme.Colors

val shortCursorBrush = Brush.verticalGradient(
0.00f to Color.Transparent,
0.35f to Color.Transparent,
0.35f to Colors.DarkRed,
0.90f to Colors.DarkRed,
0.90f to Color.Transparent,
1.00f to Color.Transparent
)