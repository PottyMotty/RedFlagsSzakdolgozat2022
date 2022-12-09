package bme.spoti.redflags.ui.ingame.screens.waiting_for_reconnect

import androidx.lifecycle.ViewModel
import bme.spoti.redflags.repository.GameRepository
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class WaitingForReconnectViewModel(
	private val gameRepository: GameRepository
) : ViewModel() {
	val disconnectedPlayers = gameRepository.playersInRoom.map { it.filter { player->!player.isConnected } }
}