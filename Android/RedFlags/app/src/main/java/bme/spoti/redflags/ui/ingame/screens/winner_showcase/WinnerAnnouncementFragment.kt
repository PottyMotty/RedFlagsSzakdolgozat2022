package bme.spoti.redflags.ui.ingame.screens.winner_showcase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import bme.spoti.redflags.R
import bme.spoti.redflags.databinding.FragmentFullscreenComposeBinding
import bme.spoti.redflags.databinding.WinnerAnnouncementFragmentBinding
import bme.spoti.redflags.other.Fonts
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import com.skydoves.landscapist.glide.GlideImage
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class WinnerAnnouncementFragment : BaseBindingFragment<FragmentFullscreenComposeBinding>() {

	private val viewModel: WinnerAnnouncementViewModel by viewModel()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.composeView.setContent {
			RedFlagsTheme() {
				val winner = viewModel.winnerPlayer.collectAsState(null).value
				val party by remember {
					mutableStateOf(
						Party(
							speed = 0f,
							angle = -50,
							maxSpeed = 40f,
							damping = 0.96f,
							spread = 70,
							colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
							position = Position.Relative(0.0, 0.3),
							emitter = Emitter(duration = 5000, TimeUnit.MILLISECONDS).max(250)
						)
					)
				}
				val party2 by remember {
					mutableStateOf(
						Party(
							speed = 0f,
							angle = -140,
							maxSpeed = 40f,
							damping = 0.96f,
							spread = 70,
							colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
							position = Position.Relative(1.0, 0.3),
							emitter = Emitter(duration = 5000, TimeUnit.MILLISECONDS).max(250)
						)
					)
				}
				Box(contentAlignment = Alignment.Center) {
					Column(
						Modifier
							.fillMaxSize()
							.background(Colors.LightRed),
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.Center
					) {
						Text(
							text = "\uD83C\uDFC6The winner\uD83C\uDFC6",
							color = Colors.DarkRed,
							style = MaterialTheme.typography.h2,
							modifier = Modifier.padding(top = 12.dp, bottom = 30.dp)
						)
						Box(
							modifier = Modifier
								.padding(horizontal = PaddingSizes.loosest)
								.fillMaxWidth()
								.aspectRatio(1f)
								.background(Colors.Darker, RoundedCornerShape(20))
								.padding(30.dp)
								.clip(
									RoundedCornerShape(20)
								)
						) {
							GlideImage(
								imageModel = winner?.imageURL
							)
						}
						winner?.let {
							Text(
								text = it.username,
								color = Colors.PrimaryRed,
								modifier = Modifier.padding(6.dp),
								style = MaterialTheme.typography.h2
							)
						}
					}
					KonfettiView(
						modifier = Modifier.fillMaxSize(),
						parties = listOf(party,party2),
					)
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