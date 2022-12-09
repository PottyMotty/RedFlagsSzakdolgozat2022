package bme.spoti.redflags.ui.ingame.screens.lobby

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import bme.spoti.redflags.databinding.FragmentFullscreenComposeBinding
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.common.views.PopUpFooter
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import bme.spoti.redflags.utils.changeColor
import bme.spoti.redflags.utils.generateQR
import bme.spoti.redflags.utils.isPermanantlyDenied
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

import org.koin.androidx.viewmodel.ext.android.viewModel


class LobbyFragment : BaseBindingFragment<FragmentFullscreenComposeBinding>() {


	private val viewModel: LobbyViewModel by viewModel()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setComposeContent()

	}

	@OptIn(ExperimentalPermissionsApi::class)
	private fun setComposeContent() {
		binding.composeView.setContent {
			val roomCode = viewModel.roomCode.observeAsState().value ?: "Loading"
			val isPasswordProtected = viewModel.passwordProtected.collectAsState(null).value
			val iamRoomOwner = viewModel.iamRoomOwner.observeAsState().value ?: false
			val phase = viewModel.phase.observeAsState().value
			val players = viewModel.playersInRoom.observeAsState(initial = emptyList()).value
			val isSharingOn = viewModel.isSharingActive.collectAsState(initial = false).value
			val state = rememberLazyListState()

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
						Row(
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier.padding(vertical = PaddingSizes.loose)
						) {
							Text(
								text = "lobby",
								style = MaterialTheme.typography.h2,
								color = Colors.PrimaryRed,
								modifier = Modifier.weight(1f)
							)
							Text(
								text = "${players.size}/8",
								style = MaterialTheme.typography.h3,
								color = Colors.DarkRed
							)

						}
						LazyColumn(
							state = state,
							modifier = Modifier.fillMaxSize(),
							verticalArrangement = Arrangement.spacedBy(PaddingSizes.default)
						) {
							items(players) { player ->
								LobbyItem(player = player)
							}
						}
					}
					val permissionsState = rememberMultiplePermissionsState(
						permissions = buildList {
							add(Manifest.permission.ACCESS_FINE_LOCATION)
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
								addAll(
									listOf(
										Manifest.permission.BLUETOOTH_ADVERTISE,
										Manifest.permission.BLUETOOTH_CONNECT,
										Manifest.permission.BLUETOOTH_SCAN
									)
								)
							}
						}
					)
					PopUpFooter(
						hiddenOn = {state.isScrollInProgress},
						visible = {
							LobbyFooter(
								roomCode = roomCode,
								phase = phase,
								isRoomOwner = iamRoomOwner,
								isSharingRoom = isSharingOn,
								eventOutputFlow = viewModel.uiEventsInput,
								popUpAction = it,
								sharingClicked = {
									if(!permissionsState.allPermissionsGranted){
										permissionsState.launchMultiplePermissionRequest()
									}
									if(permissionsState.isPermanantlyDenied()){
										Toast.makeText(context, "Permissions are permanantly denied", Toast.LENGTH_SHORT).show()
									}
									if(permissionsState.allPermissionsGranted){
										viewModel.sharingClicked()
									}
								}
							)
						},
						hidden = {
							Column(
								Modifier
									.padding(
										horizontal = PaddingSizes.loose,
										vertical = PaddingSizes.looser
									)
									.fillMaxWidth()
									.aspectRatio(1f)
									.background(Colors.Sand, MaterialTheme.shapes.large)
									.clip(MaterialTheme.shapes.large)
							) {
								val deeplink = "redflags://room/$roomCode/$isPasswordProtected"
								val bitmapQR = generateQR(deeplink, 512)
									?.changeColor(0xFFFFFFFF, 0xFFFFD9B7)
									?.changeColor(0xFF000000, 0xFF674142)
									?: throw Exception()
								Image(
									bitmap = bitmapQR.asImageBitmap(),
									contentDescription = "qr",
									contentScale = ContentScale.FillBounds,
									modifier = Modifier.fillMaxSize(),
								)
							}
						}
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