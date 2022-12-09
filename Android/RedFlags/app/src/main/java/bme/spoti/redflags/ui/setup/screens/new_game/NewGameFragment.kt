package bme.spoti.redflags.ui.setup.screens.new_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import bme.spoti.redflags.R
import bme.spoti.redflags.data.model.CreateRoomRequest
import bme.spoti.redflags.databinding.FragmentFullscreenComposeBinding
import bme.spoti.redflags.databinding.FragmentNewGameBinding
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.common.views.BaseFormCell
import bme.spoti.redflags.ui.common.views.CallToActionButton
import bme.spoti.redflags.ui.common.views.TextInputWithHeader
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import bme.spoti.redflags.utils.UIStateOf
import bme.spoti.redflags.utils.snackbar
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.round


class NewGameFragment : BaseBindingFragment<FragmentFullscreenComposeBinding>() {

	private val viewModel: NewGameViewModel by viewModel()


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		listenToEvents()
		setComposeContent()
		viewModel.getPacks()
	}

	private fun setComposeContent() {
		binding.composeView.setContent {
			RedFlagsTheme() {
				var roundAmount by rememberSaveable {
					mutableStateOf<Int?>(null)
				}
				var password by rememberSaveable {
					mutableStateOf<String>("")
				}
				val packsState = viewModel.packs.collectAsState().value
				var selectedPacks by remember {
					mutableStateOf<List<Int>>(emptyList())
				}
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
							text = "new game",
							style = MaterialTheme.typography.h2,
							color = Colors.PrimaryRed
						)
						BaseFormCell(headerText = "Number of rounds") {
							RoundNumChooser(
								roundAmount = roundAmount,
								onChange = { roundAmount = it })
						}
						BaseFormCell(headerText = "Packs") {
							when (packsState) {
								is UIStateOf.Failure -> {
									Column(horizontalAlignment = Alignment.CenterHorizontally) {
										Icon(
											painter = painterResource(id = R.drawable.ic_info),
											contentDescription = "Info",
											tint = Colors.PrimaryRed
										)
										Text(
											text = "Failed to load packs.",
											style = MaterialTheme.typography.h5,
											color = Colors.DarkRed
										)
										Text(
											text = "retry",
											style = MaterialTheme.typography.h5,
											color = Colors.DarkRed,
											modifier= Modifier.background(Colors.SandDarker, CircleShape).clip(CircleShape).clickable {
												viewModel.getPacks()
											}.padding(horizontal = PaddingSizes.default)
										)
									}
								}
								UIStateOf.Loading -> {
									CircularProgressIndicator(
										color = Colors.DarkRed
									)
								}
								is UIStateOf.Success -> {
									PackChooser(
										packs = packsState.data,
										selected = selectedPacks,
										onItemClicked = {
											selectedPacks = if (selectedPacks.contains(it))
												selectedPacks.minus(it)
											else
												selectedPacks.plus(it)
										})
								}
							}

						}
						TextInputWithHeader(
							headerText = "Password",
							value = password,
							onTextChanged = { password = it })
						CallToActionButton(
							callToActionText = "create",
							backgroundColor = Colors.OkGreen,
							foregroundColor = Colors.OkGreenDark,
							isPressable = selectedPacks.isNotEmpty() && roundAmount != null
						) {
							viewModel.navigateAndValidateToCreatePlayer(
								createRoomRequest = CreateRoomRequest(
									numRounds = roundAmount!!,
									packs = selectedPacks,
									password = password
								)
							){
								val action = NewGameFragmentDirections.actionNewGameFragmentToPlayerCreationFragment(it,password.isNotEmpty())
								view?.findNavController()?.navigate(action)
							}
						}
					}
				}
			}
		}
	}


	private fun listenToEvents() {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				viewModel.setupEvent.collect { event ->
					when (event) {
						is NewGameViewModel.SetupEvent.GetPacksErrorEvent -> {
							snackbar(event.error)
						}
						is NewGameViewModel.SetupEvent.NavigateToPlayerCreation -> {

						}
						else -> {}
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