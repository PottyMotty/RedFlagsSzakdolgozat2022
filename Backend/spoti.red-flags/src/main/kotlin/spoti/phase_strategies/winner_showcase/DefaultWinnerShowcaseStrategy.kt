package spoti.phase_strategies.winner_showcase

import spoti.data.model.websocket.DateInfo
import spoti.data.model.websocket.out.WinnerAnnouncement
import spoti.exceptions.ValueNotYetAvalaible
import spoti.game.GameManager
import spoti.other.Phase
import spoti.other.extensions.asJson
import spoti.phase_strategies.PhaseBaseStrategy

class DefaultWinnerShowcaseStrategy : PhaseBaseStrategy(Phase.WINNER_SHOWCASE) {
    override suspend fun invoke(gameManager: GameManager) {
        gameManager.room.let { room ->
            val winnerDate : DateInfo = if(gameManager.isSabotageStage){
                gameManager.finalWinner
            }else{
                gameManager.firstRoundWinner
            } ?: throw ValueNotYetAvalaible("Round winner missing")
            val winnerAnnouncement = WinnerAnnouncement(winnerDate)
            room.broadcast(winnerAnnouncement.asJson())
        }
    }
}