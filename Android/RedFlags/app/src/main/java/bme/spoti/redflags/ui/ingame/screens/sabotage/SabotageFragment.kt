package bme.spoti.redflags.ui.ingame.screens.sabotage

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import bme.spoti.redflags.R
import bme.spoti.redflags.data.model.DoneIndicatorInfo
import bme.spoti.redflags.databinding.FragmentSabotageBinding
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.common.views.SmallProfilePicture
import bme.spoti.redflags.ui.ingame.adapters.CardAdapter
import bme.spoti.redflags.ui.ingame.adapters.CardLayoutManager
import bme.spoti.redflags.ui.ingame.component.CounterButton
import bme.spoti.redflags.ui.ingame.component.Date
import bme.spoti.redflags.ui.ingame.component.TimeLeft
import bme.spoti.redflags.ui.ingame.screens.date_creation.DateCreationTobBar
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import bme.spoti.redflags.utils.asJson
import bme.spoti.redflags.utils.observe
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.update
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber


class SabotageFragment : BaseBindingFragment<FragmentSabotageBinding>() {


	private val viewModel: SabotageViewModel by viewModel()

	val cardsAdapter: CardAdapter by inject() {
		parametersOf(true)
	}

	fun setComposeContent() {
		binding.composeCounter.setContent {
			val selectedCount = cardsAdapter.selectedCount.collectAsState().value
			val done = cardsAdapter.playerDoneCreating.collectAsState().value
			CounterButton(count = selectedCount,
				maxCount = 1,
				doneCreating = done,
				onClickCheckmark = {
					cardsAdapter.playerDoneCreating.update{true}
					viewModel.sendNegativeDate(cardsAdapter.selected[0])
				},
				onClickResume = {
					cardsAdapter.playerDoneCreating.update{false}
					viewModel.sendResumeWork()
				})
		}
		binding.composeTopBar.setContent {
			val maxTime = viewModel.phaseDuration.observeAsState().value
			val remainingTime = viewModel.remainingTime.observeAsState().value
			val doneInfo =
				viewModel.doneIndicatorInfo.observeAsState(initial = DoneIndicatorInfo(0, 0)).value
			Spacer(modifier = Modifier.height(PaddingSizes.loose))
			DateCreationTobBar("sabotage",maxTime = maxTime, remainingTime = remainingTime, doneInfo = doneInfo)
		}
		binding.singleMessage.setContent {
			RedFlagsTheme() {
				Box(modifier = Modifier
					.fillMaxSize()
					.padding(PaddingSizes.loosest), contentAlignment = Alignment.Center) {
					Text(
						text = "This round you are the single, you don't need to do anything.",
						style = MaterialTheme.typography.body1,
						textAlign = TextAlign.Center,
						color = Colors.PrimaryRed
					)
				}
			}
		}
		binding.dateToSabotage.setContent {
			RedFlagsTheme {
				val personToSabotage = viewModel.sabotagedPerson.collectAsState(null).value
				val dateToSabotage = viewModel.dateToSabotage.collectAsState(null).value
				DateToSabotageFooter(personToSabotage = personToSabotage, dateToSabotage = dateToSabotage)
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupRecyclerView()
		setupViews()
		setComposeContent()
	}


	private fun setupRecyclerView() {
		binding.rvCards.adapter = cardsAdapter
		val displayMetrics: DisplayMetrics = DisplayMetrics()
		requireActivity().windowManager.getDefaultDisplay().getMetrics(displayMetrics)
		val screenWidth: Int = displayMetrics.widthPixels
		binding.rvCards.layoutManager = CardLayoutManager(resources, screenWidth)
		val snapHelper: SnapHelper = PagerSnapHelper()
		snapHelper.attachToRecyclerView(binding.rvCards)
	}

	private fun setupViews() {
		viewModel.iamSingle.observe(viewLifecycleOwner) { iAmSingle ->
			if (iAmSingle) {
				binding.singleMessage.visibility = View.VISIBLE
				binding.rvCards.visibility = View.INVISIBLE
				binding.composeCounter.visibility = View.INVISIBLE
				binding.dateToSabotage.visibility = View.GONE
			} else {
				binding.singleMessage.visibility = View.INVISIBLE
				binding.rvCards.visibility = View.VISIBLE
				binding.composeCounter.visibility = View.VISIBLE
				binding.dateToSabotage.visibility = View.VISIBLE
			}
		}
		viewLifecycleOwner.lifecycleScope.launchWhenStarted {
			viewModel.cardsInHand.collect { cards ->
				cardsAdapter.updateDataset(cards)
			}
		}
		viewModel.lastSabotagedDate.observe(viewLifecycleOwner){
			if(it!=null) {
				cardsAdapter.setSelectedCards(listOf(it.negativeAttribute ?: ""))
			}
		}
	}

	override fun createBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): FragmentSabotageBinding {
		return FragmentSabotageBinding.inflate(inflater, container, false)
	}
}
