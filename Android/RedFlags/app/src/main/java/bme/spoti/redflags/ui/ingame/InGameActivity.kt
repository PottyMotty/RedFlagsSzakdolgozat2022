package bme.spoti.redflags.ui.ingame

import android.R.attr.data
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import bme.spoti.redflags.R
import bme.spoti.redflags.data.Game
import bme.spoti.redflags.data.model.websocket.out.DisconnectRequest
import bme.spoti.redflags.data.model.websocket.out.JoinRoomHandshake
import bme.spoti.redflags.data.model.websocket.out.ReconnectRequest
import bme.spoti.redflags.databinding.ActivityIngameBinding
import bme.spoti.redflags.network.ws.RedFlagsSocketService
import bme.spoti.redflags.receiver.WebSocketReceiver
import bme.spoti.redflags.repository.GameRepository
import bme.spoti.redflags.ui.ingame.screens.date_creation.DateCreationFragment
import bme.spoti.redflags.ui.ingame.screens.end.EndFragment
import bme.spoti.redflags.ui.ingame.screens.leaderboard.LeaderboardFragment
import bme.spoti.redflags.ui.ingame.screens.lobby.LobbyFragment
import bme.spoti.redflags.ui.ingame.screens.reconnect.ReconnectFragment
import bme.spoti.redflags.ui.ingame.screens.sabotage.SabotageFragment
import bme.spoti.redflags.ui.ingame.screens.showcase_all.ShowcaseAllFragment
import bme.spoti.redflags.ui.ingame.screens.showcase_one.ShowcaseOneFragment
import bme.spoti.redflags.ui.ingame.screens.single_reveal.SingleRevealFragment
import bme.spoti.redflags.ui.ingame.screens.waiting_for_reconnect.WaitingForReconnectFragment
import bme.spoti.redflags.ui.ingame.screens.winner_showcase.WinnerAnnouncementFragment
import bme.spoti.redflags.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.createActivityRetainedScope
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import timber.log.Timber


class InGameActivity : AppCompatActivity() {

	private lateinit var binding: ActivityIngameBinding

	private val args: InGameActivityArgs by navArgs()

	private val clientId: String by inject(qualifier = named("clientID"))

	private val webSocketReceiver: WebSocketReceiver by inject() {
		parametersOf(lifecycleScope)
	}
	private val preferencesDataStore: DataStore<Preferences> by inject()
	private val redFlagsSocketService: RedFlagsSocketService by inject()
	private val gameRepository: GameRepository by inject()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		binding = ActivityIngameBinding.inflate(layoutInflater)
		setContentView(binding.root)
		saveRoomCode()
		initializeSession()
		listenToPhaseChanges()
		handleBackNavigation()
		logConnectionState()
		handleError()
		if (!args.isReconnect) {
			lifecycleScope.launch {
				gameRepository.clearAll(false)
			}
		}
	}

	private fun saveRoomCode() {
		lifecycleScope.launch {
			preferencesDataStore.saveRoomCode(args.roomCode)
		}
	}

	private fun logConnectionState() {
		redFlagsSocketService.socketStateFlow.observe(this) {
			Timber.d("SOCKET STATE: $it")
		}
	}

	private fun initializeSession() {
		Timber.d("Try to init websocket session")
		lifecycleScope.launchWhenStarted {
			when (val result = redFlagsSocketService.initSession(args.roomCode)) {
				is Resource.Error -> {
					Snackbar.make(
						binding.root,
						R.string.error_connection_failed,
						Snackbar.LENGTH_LONG
					).show()
					Timber.d("Error websockets: ${result.message}")
				}
				is Resource.Success -> {
					webSocketReceiver.listenToMessages()
					Timber.d("Connection opened")
					if (args.isReconnect) {
						redFlagsSocketService.sendMessage(
							ReconnectRequest()
						)
					} else {
						args.joinRequest?.let { joinRequest ->
							redFlagsSocketService.sendMessage(
								JoinRoomHandshake(
									username = joinRequest.username,
									imgURL = joinRequest.imageURL,
									clientID = clientId,
									password = joinRequest.password
								)
							)
						}
					}
				}
			}
		}
	}

	private fun handleError() {
		gameRepository.errorFlow.observe(this) {
			binding.root.snackbar(it)
		}
	}

	private fun closeSession() {
		lifecycleScope.launch {
			if (gameRepository.phase.value == Game.Phase.WAITING_FOR_PLAYERS
				|| gameRepository.phase.value == Game.Phase.PLAYERS_GATHERED
				|| gameRepository.phase.value == Game.Phase.END
			) {
				redFlagsSocketService.sendMessage(DisconnectRequest())
				gameRepository.clearAll(true)
				finish()
			}
			Timber.d("closing Session")
			redFlagsSocketService.closeSession()
			Timber.d("closed Session")
		}
		webSocketReceiver.stopListening()
	}

	override fun onPause() {
		super.onPause()
	}

	override fun onStop() {
		Timber.d("ON STOP WAS CALLED")
		closeSession()
		super.onStop()
		if (gameRepository.phase.value == Game.Phase.END) {
			lifecycleScope.launch {
				gameRepository.clearAll(true)
			}
			finish()
		}
	}

	var pressedBackOnce = false
	private fun handleBackNavigation() {
		onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				if (pressedBackOnce) {
					finish()
				} else {
					lifecycleScope.launch {
						Toast.makeText(
							this@InGameActivity, "Press again to go back",
							Toast.LENGTH_LONG
						).show()
						pressedBackOnce = true
						delay(2000L)
						pressedBackOnce = false
					}
				}
			}
		})
	}

	private fun listenToPhaseChanges() {
		lifecycleScope.launchWhenStarted {
			gameRepository.calculatedPhase.collect { phaseEvent ->
				when (phaseEvent) {
					Game.Phase.WAITING_FOR_PLAYERS -> {
						supportFragmentManager.commit {
							setReorderingAllowed(true)
							replace<LobbyFragment>(R.id.fragmentContainer)
						}
					}
					Game.Phase.SINGLE_REVEAL -> {
						supportFragmentManager.commit {
							setReorderingAllowed(true)
							replace<SingleRevealFragment>(R.id.fragmentContainer)
						}
					}
					Game.Phase.DATE_CRAFTING -> {
						supportFragmentManager.commit {
							setReorderingAllowed(true)
							replace<DateCreationFragment>(R.id.fragmentContainer)
						}
					}
					Game.Phase.SHOWCASE_ONE_BY_ONE, Game.Phase.SHOWCASE_ALL -> {
						if (gameRepository.phase.value == Game.Phase.SHOWCASE_ONE_BY_ONE && phaseEvent == Game.Phase.SHOWCASE_ALL)
							return@collect

						supportFragmentManager.commit {
							setReorderingAllowed(true)
							replace<ShowcaseOneFragment>(R.id.fragmentContainer)
						}
					}
					Game.Phase.SABOTAGE -> {
						supportFragmentManager.commit {
							setReorderingAllowed(true)
							replace<SabotageFragment>(R.id.fragmentContainer)
						}
					}
					Game.Phase.LEADERBOARD -> {
						supportFragmentManager.commit {
							setReorderingAllowed(true)
							replace<LeaderboardFragment>(R.id.fragmentContainer)
						}
					}
					Game.Phase.WINNER_SHOWCASE -> {
						supportFragmentManager.commit {
							setReorderingAllowed(true)
							replace<WinnerAnnouncementFragment>(R.id.fragmentContainer)
						}
					}
					Game.Phase.WAITING_FOR_RECONNECT -> {
						supportFragmentManager.commit {
							setReorderingAllowed(true)
							replace<WaitingForReconnectFragment>(R.id.fragmentContainer)
						}
					}
					Game.Phase.RECONNECT -> {
						supportFragmentManager.commit {
							setReorderingAllowed(true)
							replace<ReconnectFragment>(R.id.fragmentContainer)
						}
					}
					Game.Phase.END -> {
						Timber.d("Game over")
						supportFragmentManager.commit {
							setReorderingAllowed(true)
							replace<EndFragment>(R.id.fragmentContainer)
						}
					}
					else -> {}
				}
			}
		}
	}


}