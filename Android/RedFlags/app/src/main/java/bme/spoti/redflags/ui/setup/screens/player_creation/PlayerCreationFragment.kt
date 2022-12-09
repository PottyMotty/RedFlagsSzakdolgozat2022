package bme.spoti.redflags.ui.setup.screens.player_creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import bme.spoti.redflags.R
import bme.spoti.redflags.databinding.FragmentFullscreenComposeBinding
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.common.views.TextInputWithHeader
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import bme.spoti.redflags.utils.Resource
import bme.spoti.redflags.utils.snackbar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import bme.spoti.redflags.data.model.websocket.JoinRequest
import bme.spoti.redflags.ui.common.views.CallToActionButton

import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayerCreationFragment : BaseBindingFragment<FragmentFullscreenComposeBinding>() {


	private val viewModel: PlayerCreationViewModel by viewModel()

	val args: PlayerCreationFragmentArgs by navArgs()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setComposeContent()
		listeningToEvents()
		println(args.roomCode)
		viewModel.getNewImage()

	}

	private fun setComposeContent() {
		binding.composeView.setContent {
			var name by rememberSaveable {
				mutableStateOf("")
			}
			var password by rememberSaveable {
				mutableStateOf("")
			}
			val imageURL = viewModel.imageURLFlow.observeAsState().value
			RedFlagsTheme() {
				Box(
					Modifier
						.background(Colors.LightRed)
						.fillMaxSize(), contentAlignment = Alignment.TopStart
				) {
					Column(
						Modifier
							.padding(horizontal = PaddingSizes.loose)
							.padding(top = PaddingSizes.looser),
						verticalArrangement = Arrangement.spacedBy(PaddingSizes.default)
					) {
						Text(
							modifier = Modifier.offset(y = PaddingSizes.default),
							text = "Hi!",
							style = MaterialTheme.typography.h1,
							color = Colors.PrimaryRed
						)
						TextInputWithHeader(
							headerText = "What is your name?",
							value = name,
							onTextChanged = { name = it })
						if (imageURL != null) {
							ProfilePictureSelector(imageUrl = imageURL) {
								viewModel.getNewImage()
							}
						}
						if (args.isPasswordProtected) {
							TextInputWithHeader(
								headerText = "Password",
								value = password,
								onTextChanged = { password = it })
						}
						CallToActionButton(
							callToActionText = "done",
							backgroundColor = Colors.OkGreen,
							foregroundColor = Colors.OkGreenDark,
							isPressable = if (args.isPasswordProtected) name.isNotEmpty() && password.isNotEmpty() else name.isNotEmpty()
						) {
							viewModel.navigateAndValidateToLobby(args.roomCode, name,password)
						}
					}
				}
			}
		}
	}

	private fun listeningToEvents() {
		lifecycleScope.launchWhenStarted {
			viewModel.setupEvent.collect { event ->
				when (event) {
					PlayerCreationViewModel.SetupEvent.InputEmptyError -> {
						snackbar(R.string.username_error)
					}
					is PlayerCreationViewModel.SetupEvent.NavigateToLobby -> {
						val action =
							PlayerCreationFragmentDirections.actionPlayerCreationFragmentToInGameActivity(
								event.roomCode,
								joinRequest = event.joinRequest
							)
						view?.findNavController()?.navigate(action)
					}
					is PlayerCreationViewModel.SetupEvent.RoomCheckFailed -> {
						snackbar(event.msg)
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