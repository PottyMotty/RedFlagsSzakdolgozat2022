package bme.spoti.redflags.ui.ingame.screens.date_creation

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.SnapHelper
import bme.spoti.redflags.R
import bme.spoti.redflags.data.model.DoneIndicatorInfo
import bme.spoti.redflags.databinding.FragmentDateCreationBinding
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.ingame.adapters.CardAdapter
import bme.spoti.redflags.ui.ingame.adapters.CardLayoutManager
import bme.spoti.redflags.ui.ingame.component.CounterButton
import bme.spoti.redflags.ui.ingame.component.TimeLeft
import bme.spoti.redflags.ui.ingame.component.ViewState
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import bme.spoti.redflags.utils.observe
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber


class DateCreationFragment : BaseBindingFragment<FragmentDateCreationBinding>() {

    val viewModel: DateCreationViewModel by viewModel()

    private val cardsAdapter: CardAdapter by inject(){
        parametersOf(false)
    }

    fun setComposeContent(){
        binding.composeTopBar.setContent {
            val maxTime = viewModel.phaseDuration.observeAsState().value
            val remainingTime = viewModel.remainingTime.observeAsState().value
            val doneInfo =
                viewModel.doneIndicatorInfo.observeAsState(initial = DoneIndicatorInfo(0, 0)).value
            Spacer(modifier = Modifier.height(PaddingSizes.loose))
            DateCreationTobBar("create a date",maxTime = maxTime, remainingTime = remainingTime, doneInfo = doneInfo)
        }
        binding.composeCounter.setContent {
            val selectedCount = cardsAdapter.selectedCount.collectAsState().value
            val done = cardsAdapter.playerDoneCreating.collectAsState().value
            CounterButton(count = selectedCount,
                maxCount = 2,
                doneCreating = done,
                onClickCheckmark = {
                    cardsAdapter.playerDoneCreating.update{true}
                    viewModel.sendPositiveDate(cardsAdapter.selected)
                },
                onClickResume = {
                    cardsAdapter.playerDoneCreating.update{ false}
                    viewModel.sendResumeWork()
                })
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
        (binding.rvCards.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        val screenWidth: Int = displayMetrics.widthPixels
        binding.rvCards.layoutManager = CardLayoutManager(resources, screenWidth)
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCards)
    }



    private fun setupViews() {
        viewModel.cardsInHand.observe(viewLifecycleOwner) { cards ->
            cardsAdapter.updateDataset(cards)
            Timber.d("dataset updated cards")
        }
        viewModel.isMeSingle.observe(viewLifecycleOwner){ iAmSingle->
            if(iAmSingle){
                binding.singleMessage.visibility = View.VISIBLE
                binding.rvCards.visibility = View.INVISIBLE
                binding.composeCounter.visibility = View.INVISIBLE
            }else{
                binding.singleMessage.visibility = View.INVISIBLE
                binding.rvCards.visibility = View.VISIBLE
                binding.composeCounter.visibility = View.VISIBLE
            }
        }
        viewModel.lastHandedInDate.observe(viewLifecycleOwner){ lastHandedIn->
            if(lastHandedIn!= null) {
                cardsAdapter.setSelectedCards(lastHandedIn.positiveAttributes)
            }
        }
    }


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDateCreationBinding {
        return FragmentDateCreationBinding.inflate(inflater,container,false)
    }
}

