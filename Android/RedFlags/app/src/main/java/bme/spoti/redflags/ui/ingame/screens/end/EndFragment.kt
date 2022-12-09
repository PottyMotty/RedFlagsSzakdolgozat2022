package bme.spoti.redflags.ui.ingame.screens.end

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bme.spoti.redflags.databinding.FragmentFullscreenComposeBinding
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EndFragment : BaseBindingFragment<FragmentFullscreenComposeBinding>() {
	private val viewModel: EndViewModel by viewModel()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.composeView.setContent {
			val winner = viewModel.winner.collectAsState(initial = null).value
			val coroutineScope = rememberCoroutineScope()
			RedFlagsTheme() {
				Box(
					Modifier
						.fillMaxSize()
						.background(Colors.LightRed), contentAlignment = Alignment.Center
				) {
					Column() {
						Text(text = "Game ended")
						Text(text = "Winner: ${winner?.username}")
						Button(onClick = {
							activity?.finish()
						}) {
							Text(text = "Back to main menu")
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