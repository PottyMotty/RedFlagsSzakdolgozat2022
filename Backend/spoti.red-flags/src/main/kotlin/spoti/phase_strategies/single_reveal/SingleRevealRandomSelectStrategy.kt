package spoti.phase_strategies.single_reveal

import com.google.gson.Gson
import spoti.data.mapToPlayerState
import spoti.data.model.websocket.out.SingleAnnouncement
import spoti.game.GameManager
import spoti.other.Phase
import spoti.phase_strategies.PhaseBaseStrategy

class SingleRevealRandomSelectStrategy : PhaseBaseStrategy(Phase.SINGLE_REVEAL) {
    override suspend operator fun invoke(gameManager: GameManager) {
        gameManager.apply {
            single = gameManager.room.getRandomPlayer(pastSinglesIDs)
            single?.let { singlePlayer ->
                pastSinglesIDs.add(singlePlayer.clientId)
                val announcement = SingleAnnouncement(singlePlayer.mapToPlayerState())
                gameManager.room.broadcast(Gson().toJson(announcement))
            }
        }
    }
}