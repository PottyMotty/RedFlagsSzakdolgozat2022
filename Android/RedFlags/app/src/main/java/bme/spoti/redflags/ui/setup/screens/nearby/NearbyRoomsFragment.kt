package bme.spoti.redflags.ui.setup.screens.nearby

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.findNavController
import bme.spoti.redflags.R
import bme.spoti.redflags.databinding.FragmentFullscreenComposeBinding
import bme.spoti.redflags.ui.base.BaseBindingFragment
import bme.spoti.redflags.ui.common.views.PermissionAsker
import bme.spoti.redflags.ui.setup.screens.landing.LandingFragmentDirections
import bme.spoti.redflags.ui.setup.screens.landing.LandingViewModel
import bme.spoti.redflags.ui.theme.Colors
import bme.spoti.redflags.ui.theme.PaddingSizes
import bme.spoti.redflags.ui.theme.RedFlagsTheme
import bme.spoti.redflags.utils.isPermanentlyDenied
import com.google.accompanist.permissions.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class NearbyRoomsFragment : BaseBindingFragment<FragmentFullscreenComposeBinding>() {

	private val viewModel: NearbyRoomsViewModel by viewModel()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setComposeContent()
	}

	@OptIn(ExperimentalPermissionsApi::class)
	private fun setComposeContent() {
		binding.composeView.setContent {
			RedFlagsTheme {
				val permissionsState = rememberMultiplePermissionsState(
					permissions = buildList {
						add(Manifest.permission.ACCESS_FINE_LOCATION)
						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
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

				val lifecycleOwner = LocalLifecycleOwner.current
				DisposableEffect(key1 = lifecycleOwner) {
					val observer = LifecycleEventObserver { _, event ->
						when (event) {
							Lifecycle.Event.ON_START -> {
								permissionsState.launchMultiplePermissionRequest()
							}
							Lifecycle.Event.ON_STOP -> {
								if (permissionsState.allPermissionsGranted) {
									viewModel.stopListening()
								}
							}
							else -> {}
						}
					}
					lifecycleOwner.lifecycle.addObserver(observer)

					onDispose {
						lifecycleOwner.lifecycle.removeObserver(observer)
					}
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
						if (permissionsState.allPermissionsGranted) {
							LaunchedEffect(key1 = true){
								viewModel.startListening()
							}
							val rooms = viewModel.nearbyRooms.collectAsState().value.values.toList()
							Text(
								text = "Nearby rooms",
								style = MaterialTheme.typography.h2,
								color = Colors.PrimaryRed
							)
							LazyColumn(modifier = Modifier.fillMaxSize()) {
								items(rooms) {
									NearbyRoom(info = it) {
										val action =
											NearbyRoomsFragmentDirections.actionNearbyRoomsFragmentToPlayerCreationFragment(
												it.roomCode,
												it.isPasswordProtected
											)
										view?.findNavController()?.navigate(action)
									}
								}
							}
						} else {
							Text(
								text = "Permission missing",
								color = Colors.DarkRed
							)
							PermissionAsker(
								permissions = listOf(
									Manifest.permission.ACCESS_FINE_LOCATION
								),
								icon = R.drawable.ic_location,
								title = "Locations",
								explanation = "The app needs your fine location data in order to find rooms that are near you currently."
							)
							if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
								PermissionAsker(
									permissions = listOf(
										Manifest.permission.BLUETOOTH_ADVERTISE,
										Manifest.permission.BLUETOOTH_CONNECT,
										Manifest.permission.BLUETOOTH_SCAN
									),
									icon = R.drawable.ic_bluetooth,
									title = "Bluetooth",
									explanation = "The app needs access to your bluetooth to use it to find devices near you and use it to establish a connection, and recieve information about nearby rooms."
								)
							}
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
