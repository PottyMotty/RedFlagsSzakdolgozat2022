package bme.spoti.redflags.ui.ingame.screens.showcase_one

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCirc
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.fragment.app.Fragment
import bme.spoti.redflags.R
import bme.spoti.redflags.data.model.websocket.out.WinnerChoosen
import bme.spoti.redflags.databinding.FragmentFullscreenComposeBinding
import bme.spoti.redflags.databinding.FragmentShowcaseOneByOneBinding
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.common.views.CallToActionButton
import bme.spoti.redflags.ui.ingame.component.Date
import bme.spoti.redflags.ui.ingame.component.TimeLeft
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import kotlin.math.absoluteValue


class ShowcaseOneFragment : BaseBindingFragment<FragmentFullscreenComposeBinding>() {

	private val viewModel: ShowcaseOneViewModel by viewModel()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setComposeContent()
	}

	@OptIn(ExperimentalPagerApi::class)
	private fun setComposeContent() {
		binding.composeView.setContent {
			val dates = viewModel.dates.collectAsState(initial = emptyList()).value
			val currentTimeLeft = viewModel.remainingTime.observeAsState().value
			val maxTime = viewModel.maxTime.observeAsState().value?.toLong()
			val showcaseState = viewModel.showcaseState.observeAsState(initial = null).value
			val dateNumber = viewModel.dateToShowcase.collectAsState(initial = 0).value
			val iAmSingle = viewModel.iAmSingle.collectAsState(initial = false).value
			RedFlagsTheme() {
				val pagerState = rememberPagerState()
				var width by remember {
					mutableStateOf(0)
				}
				LaunchedEffect(key1 = dateNumber) {
					if (dateNumber > 0) {
						pagerState.animateScrollBy(width.toFloat(), tween(600, 0, EaseInOutCirc))
					}
				}
				val animatedTarget: Float by animateFloatAsState(
					pagerState.currentPage.toFloat(),
					tween(800, easing = EaseInOutCubic)
				)
				Box(
					Modifier
						.background(Colors.LightRed)
						.fillMaxSize(), contentAlignment = Alignment.TopStart
				) {
					Column(
						Modifier
							.padding(top = PaddingSizes.looser),
						verticalArrangement = Arrangement.spacedBy(PaddingSizes.default)
					) {
						Text(
							text = "showcase",
							modifier = Modifier.padding(horizontal = PaddingSizes.loose),
							style = MaterialTheme.typography.h2,
							color = Colors.PrimaryRed,
						)

						Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
							HeartPageIndicator(pagerState)
						}
						HorizontalPager(
							count = dates.size,
							userScrollEnabled = showcaseState == ShowcaseState.SHOWCASE_ALL,
							state = pagerState,
							modifier = Modifier
								.fillMaxWidth()
								.weight(1f)

						) { page ->
							val date = dates[page]
							val creatorImage = viewModel.getPlayerFromName(date.createdBy)?.imageURL
							val sabotagerImage =
								viewModel.getPlayerFromName(date.sabotagedBy)?.imageURL
							Column(verticalArrangement = Arrangement.Top,
								modifier = Modifier
									.fillMaxHeight()
									.onGloballyPositioned { coords ->
										width = coords.size.width
									}
									.padding(horizontal = PaddingSizes.loose)
									.graphicsLayer {
										val pageOffset =
											calculateCurrentOffsetForPage(page).absoluteValue

										alpha = lerp(
											start = 0f,
											stop = 1f,
											fraction = 1f - pageOffset.coerceIn(0f, 1f)
										)
										rotationY = lerp(
											start = 10f,
											stop = 0f,
											fraction = 1f - pageOffset.coerceIn(0f, 1f)
										)
										rotationX = lerp(
											start = 10f,
											stop = 0f,
											fraction = 1f - pageOffset.coerceIn(0f, 1f)
										)

									}
							) {
								Date(date, creatorImage, sabotagerImage)
							}
						}
						if (showcaseState == ShowcaseState.SHOWCASE_ONE_BY_ONE) {
							TimeLeft(
								maxTime, currentTimeLeft,
								Modifier
									.fillMaxWidth()
									.padding(horizontal = 20.dp, vertical = PaddingSizes.looser)
									.height(10.dp)
							)
						} else {
							AnimatedVisibility(visible = iAmSingle) {
								Column(
									modifier = Modifier
										.fillMaxWidth()
										.padding(bottom = PaddingSizes.loose),
									horizontalAlignment = Alignment.CenterHorizontally
								) {
									Box(
										Modifier
											.fillMaxWidth(0.6f)
											.background(
												Colors.PrimaryRed,
												MaterialTheme.shapes.large
											)
											.clip(MaterialTheme.shapes.large)
											.clickable {
												viewModel.sendMessage(WinnerChoosen(dates[pagerState.currentPage]))
											},
										contentAlignment = Alignment.Center
									) {
										Icon(
											painter = painterResource(id = R.drawable.ic_heart),
											contentDescription = "heart",
											tint = Colors.DarkRed,
											modifier = Modifier.padding(vertical = PaddingSizes.loose)
										)
									}
								}
							}
						}
					}
				}

			}
		}
	}


	override fun createBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): FragmentFullscreenComposeBinding {
		return FragmentFullscreenComposeBinding.inflate(inflater, container, false)
	}


}


