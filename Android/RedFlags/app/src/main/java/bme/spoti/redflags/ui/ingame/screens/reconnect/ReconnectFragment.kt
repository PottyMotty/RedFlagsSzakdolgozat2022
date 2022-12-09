package bme.spoti.redflags.ui.ingame.screens.reconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.lifecycleScope
import bme.spoti.redflags.R
import bme.spoti.redflags.databinding.FragmentFullscreenComposeBinding
import bme.spoti.redflags.network.ws.SocketState
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import bme.spoti.redflags.utils.observe
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReconnectFragment : BaseBindingFragment<FragmentFullscreenComposeBinding>() {
	override fun createBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): FragmentFullscreenComposeBinding {
		return FragmentFullscreenComposeBinding.inflate(inflater, container, false)
	}

	private val viewModel: ReconnectViewModel by viewModel()
	private fun setComposeContent() {
		binding.composeView.setContent {
			RedFlagsTheme() {
				val socketState = viewModel.connectionState.observeAsState()
				Box(
					modifier = Modifier
						.fillMaxSize()
						.background(Colors.LightRed),
					contentAlignment = Alignment.Center
				) {
					Column(
						modifier = Modifier.padding(PaddingSizes.default).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
					) {
						when (socketState.value) {
							SocketState.Disconnected -> {
								CircularProgressIndicator(
									color = Colors.DarkRed
								)
							}
							SocketState.ConnectionLost, SocketState.FailedToConnect -> {
								Text(
									text = when (socketState.value) {
										SocketState.ConnectionLost -> "Connection has been lost! Try again."
										SocketState.FailedToConnect -> "Failed to connect! Try again."
										else -> "Failed!"
									},
									style = MaterialTheme.typography.h4,
									textAlign = TextAlign.Center
								)
								Button(
									onClick = { viewModel.reconnect() },
									colors = ButtonDefaults.buttonColors(
										backgroundColor = Colors.Sand,
										contentColor = Colors.DarkRed
									)
								) {
									Text(
										text = "Reconnect",
										style = MaterialTheme.typography.button,
									)
								}
							}
							else -> {}
						}
					}
				}
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setComposeContent()
		listenToEvents()
	}

	private fun listenToEvents() {
		viewModel.eventBus.observe(viewLifecycleOwner) {
			when (it) {
				is FailedReconnection -> {
					Snackbar.make(
						binding.root,
						R.string.error_connection_failed,
						Snackbar.LENGTH_LONG
					).show()
				}
			}
		}
	}
}