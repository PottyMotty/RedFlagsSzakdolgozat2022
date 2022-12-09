package bme.spoti.redflags.ui.ingame.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.R
import bme.spoti.redflags.ui.theme.Colors

@Composable
fun TimeLeft(maximumTime: Long?, currentTime: Long?, modifier: Modifier) {
    if (maximumTime != null && currentTime != null) {
        Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
            if (maximumTime > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    backgroundColor = Colors.Darker,
                    shape = RoundedCornerShape(50), elevation = 0.dp
                ) {}
                Card(
                    modifier = Modifier
                        .fillMaxWidth((currentTime.toFloat() / maximumTime.toFloat()))
                        .fillMaxHeight(),
                    backgroundColor = Colors.PrimaryRed,
                    shape = RoundedCornerShape(50), elevation = 0.dp
                ) {
                }
            }
        }
    }
}