package bme.spoti.redflags.ui.ingame.screens.leaderboard

import bme.spoti.redflags.databinding.FragmentLeaderboardBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import bme.spoti.redflags.R
import bme.spoti.redflags.data.model.websocket.LeaderBoardEntry
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.theme.RedFlagsTheme

import com.google.accompanist.appcompattheme.AppCompatTheme
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.first
import org.koin.androidx.viewmodel.ext.android.viewModel

class LeaderboardFragment : BaseBindingFragment<FragmentLeaderboardBinding>() {

    private val viewModel: LeaderboardViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.leaderboardFragment.setContent {
            val standings = viewModel.leaderboardData.collectAsState(initial = null).value
            RedFlagsTheme() {
                if(standings != null) {
                    Leaderboard(standings)
                }
            }
        }
        viewModel.resetForNewRound()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLeaderboardBinding {
        return FragmentLeaderboardBinding.inflate(inflater,container,false)
    }

}

@Composable
fun Leaderboard(standings: List<PlayerDataWithScores>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "leaderboard",
            style = MaterialTheme.typography.h2,
            color = colorResource(id = R.color.primary_red),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            textAlign = TextAlign.Center
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
        ) {
            itemsIndexed(standings) { index, entry ->
                LeaderboardPlayerItem(index = index + 1, player = entry.leaderBoardEntry,
                    image= entry.player.imageURL
                )
            }
        }

    }
}

@Composable
fun LeaderboardPlayerItem(index: Int, player: LeaderBoardEntry, image: String) {
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(55.dp)) {
            Box(
                Modifier
                    .aspectRatio(1f)
                    .fillMaxHeight(), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index}.",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorResource(id = R.color.dark_red)
                )
            }
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                val medalEmojis = listOf("\uD83E\uDD47", "\uD83E\uDD48","\uD83E\uDD49")
                val medal = if(index<=3) medalEmojis[index-1] else ""
                Text(
                    text = medal + player.username,
                    style =MaterialTheme.typography.body1,
                    color = colorResource(id = R.color.dark_red)
                )
                Text(
                    text = "${player.points}+${player.pointsThisRound}=${player.points+player.pointsThisRound}" ,
                    style = MaterialTheme.typography.subtitle2,
                    color = colorResource(id = R.color.dark_red)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                GlideImage(
                    imageModel = image,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(CircleShape)

                )
            }

        }

    }
    Spacer(modifier = Modifier.height(3.dp))
    Divider(
        Modifier
            .fillMaxWidth()
            .height(2.dp)
    )

}
