package bme.spoti.redflags.ui.setup.screens.landing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.navigation.findNavController
import bme.spoti.redflags.R
import bme.spoti.redflags.databinding.FragmentFullscreenComposeBinding
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.common.views.CallToActionButton
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import bme.spoti.redflags.utils.observe
import bme.spoti.redflags.utils.snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class LandingFragment : BaseBindingFragment<FragmentFullscreenComposeBinding>() {
	private val viewmodel: LandingViewModel by viewModel()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setComposeContent()
		listenToSetupEvents()
	}

	private fun setComposeContent() {
		binding.composeView.setContent {
			var roomCode by rememberSaveable {
				mutableStateOf("")
			}
			var oldRoomCode = viewmodel.roomCode.collectAsState(initial = null)
			RedFlagsTheme() {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.background(Colors.LightRed),
					contentAlignment = Alignment.Center
				) {
					Column(
						Modifier.padding(horizontal = PaddingSizes.loose),
						verticalArrangement = Arrangement.spacedBy(PaddingSizes.default)
					) {
						RoomCodeInput(
							codeInput = roomCode,
							onCodeChange = { roomCode = it },
							onStartClicked = {
								viewmodel.navigateAndValidateToPlayerCreation(
									roomCode
								)
							}
						)
						CallToActionButton(
							"Create a room!",
							backgroundColor = Colors.PrimaryRed,
							foregroundColor = Colors.DarkRed,
							isPressable = true
						) {
							viewmodel.navigateAndValidateToGameCreation()
						}
					}
				}
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(bottom = PaddingSizes.looser),
					contentAlignment = Alignment.BottomCenter
				) {
					Row(
						horizontalArrangement = Arrangement.spacedBy(PaddingSizes.default), verticalAlignment = Alignment.CenterVertically
					) {
						FooterIcon(iconResourceId = R.drawable.ic_info) {
							val url = "https://www.ultraboardgames.com/red-flags/game-rules.php"
							val i = Intent(Intent.ACTION_VIEW)
							i.data = Uri.parse(url)
							startActivity(i)
						}
						FooterIcon(iconResourceId = R.drawable.ic_wireless) {
							val action =
								LandingFragmentDirections.actionLandingFragmentToNearbyRoomsFragment()
							view?.findNavController()?.navigate(action)
						}
						AnimatedVisibility(visible = oldRoomCode.value != null) {
							oldRoomCode.value?.let { oldRoom ->
								Box(contentAlignment = Alignment.Center) {
									Text(
										text = oldRoom,
										style = MaterialTheme.typography.h2,
										color = Colors.DarkRed,
										modifier = Modifier
											.background(
												Colors.PrimaryRed,
												MaterialTheme.shapes.large
											)
											.clip(MaterialTheme.shapes.large)
											.clickable {
												viewmodel.validateAndNavigateToGame(oldRoom)
											}
											.padding(
												horizontal = PaddingSizes.looser,
												vertical = PaddingSizes.tight
											)
									)
								}
							}
						}
					}
				}
			}
		}
	}

	private fun listenToSetupEvents() {
		viewmodel.setupEvent.observe(viewLifecycleOwner) { event ->
			when (event) {
				is LandingViewModel.SetupEvent.InputTooShort -> {
					snackbar(R.string.room_code_too_short)
				}
				is LandingViewModel.SetupEvent.FailedToReconnect -> {
					snackbar("Failed to reconnect, most likely this room no longer exists.")
				}
				is LandingViewModel.SetupEvent.NavigateToGame -> {
					val action =
						LandingFragmentDirections.actionLandingFragmentToInGameActivity(
							event.roomCode,
							joinRequest = null,
							isReconnect = true
						)
					view?.findNavController()?.navigate(action)
				}
				is LandingViewModel.SetupEvent.NavigateToCreateGame -> {
					val action =
						LandingFragmentDirections.actionLandingFragmentToNewGameFragment()
					view?.findNavController()?.navigate(action)
				}
				is LandingViewModel.SetupEvent.NavigateToPlayerCreation -> {
					val action =
						LandingFragmentDirections.actionLandingFragmentToPlayerCreationFragment(
							event.roomCode,
							event.isPasswordProtected
						)
					view?.findNavController()?.navigate(action)
				}
				is LandingViewModel.SetupEvent.NetworkError -> {
					snackbar(event.errorMsg)
				}
				is LandingViewModel.SetupEvent.FailedToCheckPassword -> {
					snackbar(R.string.failed_to_check_password)

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