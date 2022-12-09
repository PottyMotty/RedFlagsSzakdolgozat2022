package bme.spoti.redflags.ui.ingame.screens.single_reveal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import bme.spoti.redflags.R
import bme.spoti.redflags.databinding.FragmentFullscreenComposeBinding
import bme.spoti.redflags.databinding.FragmentSingleRevealBinding
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import com.bumptech.glide.Glide
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SingleRevealFragment : BaseBindingFragment<FragmentFullscreenComposeBinding>() {

	val viewModel: SingleRevealViewModel by viewModel()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setComposeContent()
	}

	fun setComposeContent() {
		binding.composeView.setContent {
			val single = viewModel.singlePlayerFlow.observeAsState().value
			RedFlagsTheme() {
				Box(
					Modifier
						.background(Colors.LightRed)
						.fillMaxSize(), contentAlignment = Alignment.TopCenter
				) {
					if (single != null) {
						Column(
							Modifier
								.padding(horizontal = PaddingSizes.loose)
								.padding(top = 120.dp),
							horizontalAlignment = Alignment.CenterHorizontally
						) {
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
									imageModel = single.imageURL
								)
							}
							Spacer(modifier = Modifier.height(PaddingSizes.loose))
							Text(
								text = "The single this round is:",
								style = MaterialTheme.typography.body2,
								color = Colors.DarkRed,
								modifier = Modifier.padding(bottom = PaddingSizes.default)
							)
							Text(
								text = single.username,
								style = MaterialTheme.typography.h2,
								color = Colors.PrimaryRed,
								modifier = Modifier.padding(bottom = PaddingSizes.default)
							)
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
