package bme.spoti.redflags.ui.ingame.screens.waiting_for_reconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import bme.spoti.redflags.databinding.FragmentFullscreenComposeBinding
import bme.spoti.redflags.databinding.FragmentSingleRevealBinding
import bme.spoti.redflags.databinding.FragmentWaitingForReconnectBinding
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.ingame.screens.single_reveal.SingleRevealViewModel
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import com.bumptech.glide.Glide
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class WaitingForReconnectFragment : BaseBindingFragment<FragmentFullscreenComposeBinding>(){
	private val viewModel : WaitingForReconnectViewModel by viewModel()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.composeView.setContent { 
			val disconnectedPlayers = viewModel.disconnectedPlayers.collectAsState(initial = emptyList())
			RedFlagsTheme() {
				Box(
					Modifier
						.fillMaxSize()
						.background(Colors.LightRed)
				){
					BouncingItemList(items = disconnectedPlayers.value, modifier = Modifier.fillMaxSize()) {
						GlideImage(imageModel =it.imageURL, modifier = Modifier
							.size(100.dp)
							.clip(
								CircleShape
							))
					}
					/*Box(
						Modifier
							.fillMaxSize()
							.background(Colors.DarkRed)
							.alpha(0.65f), contentAlignment = Alignment.Center){
						Text(text = "Waiting for reconnection..")
					}*/
				}
			}
		}
	}

	override fun createBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): FragmentFullscreenComposeBinding {
		return FragmentFullscreenComposeBinding.inflate(inflater,container,false)
	}
}