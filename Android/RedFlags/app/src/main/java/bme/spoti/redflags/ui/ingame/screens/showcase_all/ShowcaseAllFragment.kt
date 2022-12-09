package bme.spoti.redflags.ui.ingame.screens.showcase_all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import bme.spoti.redflags.R
import bme.spoti.redflags.data.model.websocket.out.WinnerChoosen
import bme.spoti.redflags.databinding.FragmentShowcaseAllBinding
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.ingame.component.Date
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShowcaseAllFragment : BaseBindingFragment<FragmentShowcaseAllBinding>() {

    private val viewModel : ShowcaseAllViewModel by viewModel()
    @OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentShowcaseAll.setContent {
            val isSingle= viewModel.iAmSingle.collectAsState(false).value
            val pagerState = rememberPagerState()
            val dates = viewModel.allDates.collectAsState().value

            AppCompatTheme {
                Column(Modifier.fillMaxSize()) {
                    HorizontalPager(count = dates.size, state = pagerState, modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)) { page ->
                        val date = dates[page]
                        val creatorImage =
                            viewModel.players.collectAsState().value.find { it.username == date.createdBy }?.imageURL
                        val sabotagerImage =
                            viewModel.players.collectAsState().value.find { it.username == date.sabotagedBy }?.imageURL
                        if (creatorImage != null) {
                            Date(date, creatorImage, sabotagerImage)
                        }
                    }
                    Column(modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                        ){
                        HorizontalPagerIndicator(
                            activeColor = colorResource(id = R.color.primary_red),
                            inactiveColor = colorResource(id = R.color.background_darker),
                            pagerState = pagerState,
                            modifier = Modifier
                                .padding(16.dp),
                        )
                        if(isSingle) {
                            Card(
                                modifier = Modifier
                                    .height(70.dp)
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .fillMaxHeight(),
                                backgroundColor = colorResource(id = R.color.ok_green),
                                elevation = 0.dp,
                                shape = RoundedCornerShape(10.dp),
                                onClick = {viewModel.sendMessage(WinnerChoosen(dates[pagerState.currentPage]))}
                            ) {
                                Icon(
                                    Icons.Filled.PlayArrow,
                                    contentDescription = "play",
                                    modifier = Modifier.fillMaxWidth(),
                                    tint = colorResource(id = R.color.ok_green_darker)
                                )
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
    ): FragmentShowcaseAllBinding {
        return FragmentShowcaseAllBinding.inflate(inflater,container,false)
    }

}