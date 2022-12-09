package bme.spoti.redflags.ui.setup.screens.new_game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.data.model.PackMetaInfo
import bme.spoti.redflags.ui.common.views.BaseFormCell
import bme.spoti.redflags.ui.common.views.CallToActionButton
import bme.spoti.redflags.ui.common.views.TextInputWithHeader
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import bme.spoti.redflags.utils.asJson
import kotlin.math.round

@Composable
@Preview
fun NewGameScreenPreview() {
	RedFlagsTheme() {
		var roundAmount by rememberSaveable {
			mutableStateOf<Int?>(null)
		}
		var password by rememberSaveable {
			mutableStateOf("")
		}
		var packs by rememberSaveable {
			mutableStateOf<List<PackMetaInfo>>(listOf(
				PackMetaInfo(
					packId = 0,
					name = "basic",
					positiveCount = 50,
					negativeCount = 20
				),
				PackMetaInfo(
					packId = 1,
					name = "spicy",
					positiveCount = 84,
					negativeCount =43
				),
				PackMetaInfo(
					packId = 3,
					name = "balaton",
					positiveCount = 56,
					negativeCount = 25
				),
				PackMetaInfo(
					packId = 4,
					name = "dabas",
					positiveCount = 24,
					negativeCount =41
				),
			))
		}
		var selectedPacks by rememberSaveable {
			mutableStateOf<List<Int>>(emptyList())
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
				Text(
					text = "new game",
					style = MaterialTheme.typography.h2,
					color = Colors.PrimaryRed
				)
				BaseFormCell(headerText = "Number of rounds") {
					RoundNumChooser(roundAmount = roundAmount, onChange = {roundAmount = it})
				}
				BaseFormCell(headerText = "Packs") {
					PackChooser(packs = packs, selected = selectedPacks, onItemClicked = {
						selectedPacks = if(selectedPacks.contains(it))
							selectedPacks.minus(it)
						else
							selectedPacks.plus(it)
					})
				}
				TextInputWithHeader(headerText = "Password", value = password, onTextChanged = {password = it})
				CallToActionButton(
					callToActionText = "create",
					backgroundColor = Colors.OkGreen,
					foregroundColor = Colors.OkGreenDark,
					isPressable = selectedPacks.isNotEmpty() && roundAmount != null
				) {

				}
			}
		}
	}
}
@Composable
fun PackChooser(packs: List<PackMetaInfo>, selected: List<Int>, onItemClicked: (Int)->Unit){
	LazyRow(
		modifier = Modifier.fillMaxWidth(),
	){
		items(packs){pack->
			PackInfo(
				packMetaInfo = pack,
				isSelected = selected.contains(pack.packId),
				onClicked = onItemClicked
			)
		}
	}
}
@Composable
fun PackInfo(packMetaInfo: PackMetaInfo, isSelected: Boolean, onClicked: (Int)->Unit){
	Column(
		modifier = Modifier
			.padding(end = PaddingSizes.default)
			.width(140.dp)
			.aspectRatio(0.75f)
			.background(
				if (isSelected) Colors.PrimaryRed else Colors.Darker,
				MaterialTheme.shapes.medium
			)
			.clip(MaterialTheme.shapes.medium)
			.clickable {
				onClicked(packMetaInfo.packId)
			}
	) {
		Text(
			text= packMetaInfo.name,
			style = MaterialTheme.typography.body1,
			color = Colors.DarkRed,
			modifier = Modifier.padding(horizontal = PaddingSizes.default, vertical = PaddingSizes.default)
		)
		Spacer(modifier = Modifier.weight(1f))
		Text(
			text= (packMetaInfo.negativeCount+packMetaInfo.positiveCount).toString(),
			style = MaterialTheme.typography.h3,
			color = Colors.DarkRed,
			modifier = Modifier
				.padding(horizontal = PaddingSizes.default, vertical = PaddingSizes.default)
				.fillMaxWidth(),
			textAlign = TextAlign.End
		)
	}
}
@Composable
fun RoundNumChooser(roundAmount: Int?, onChange: (Int)->Unit){
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		repeat(9) {
			Box(
				modifier = Modifier
					.size(30.dp)
					.background(
						if (roundAmount == it+1) Colors.PrimaryRed else Colors.Darker,
						CircleShape
					)
					.clip(CircleShape)
					.clickable { onChange(it+1) },
				contentAlignment = Alignment.Center
			) {
				if(roundAmount==it +1) {
					Text(
						modifier = Modifier.offset(y = (-1).dp),
						text = (it + 1).toString(),
						style = MaterialTheme.typography.subtitle2,
						color = Colors.DarkRed
					)
				}
			}
		}
	}
}