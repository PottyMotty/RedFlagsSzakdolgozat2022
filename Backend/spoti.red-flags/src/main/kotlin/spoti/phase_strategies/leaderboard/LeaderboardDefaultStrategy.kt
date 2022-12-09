package spoti.phase_strategies.leaderboard

import com.google.gson.Gson
import spoti.data.model.websocket.LeaderBoardEntry
import spoti.data.model.websocket.out.LeaderBoardData
import spoti.game.GameManager
import spoti.other.Phase
import spoti.phase_strategies.PhaseBaseStrategy

class LeaderboardDefaultStrategy : PhaseBaseStrategy(Phase.LEADERBOARD){
    override suspend fun invoke(gameManager: GameManager) {
        gameManager.room.let { room ->
            val leaderBoardData = room.players.map { LeaderBoardEntry(it.username, it.points, it.pointsThisRound) }.sortedByDescending { it.pointsThisRound+ it.points }
            gameManager.leaderBoardStandings = leaderBoardData
            room.broadcast(Gson().toJson(LeaderBoardData(leaderBoardData)))
            room.players.forEach {
                it.points += it.pointsThisRound
                it.pointsThisRound = 0
            }
        }
    }
}