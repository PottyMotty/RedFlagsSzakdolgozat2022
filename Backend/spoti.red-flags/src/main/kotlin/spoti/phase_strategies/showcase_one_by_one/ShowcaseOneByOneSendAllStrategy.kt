package spoti.phase_strategies.showcase_one_by_one

import com.google.gson.Gson
import io.ktor.websocket.*
import spoti.data.model.websocket.out.AllDates
import spoti.game.GameManager
import spoti.other.Phase
import spoti.phase_strategies.PhaseBaseStrategy

class ShowcaseOneByOneSendAllStrategy : PhaseBaseStrategy(Phase.SHOWCASE_ONE_BY_ONE) {
    override suspend fun invoke(gameManager: GameManager) {
        gameManager.room.players.forEach { player ->
            player.socket.send(Frame.Text(Gson().toJson(AllDates(gameManager.dateOptions))))
        }
    }
}