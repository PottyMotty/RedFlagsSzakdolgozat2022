package spoti.phase_strategies.sabotage

import org.koin.java.KoinJavaComponent
import spoti.data.model.CardData
import spoti.data.model.websocket.out.DateToSabotage
import spoti.data.model.websocket.out.DealtCards
import io.ktor.websocket.*
import spoti.data.Player
import spoti.game.GameManager
import spoti.other.Phase
import spoti.other.extensions.asJson
import spoti.phase_strategies.PhaseBaseStrategy
import spoti.repo.CardRepository

class SabotageWithRandomizedPairsStrategy(private val negativeCardAmount: Int) : PhaseBaseStrategy(Phase.SABOTAGE) {
    private fun createPairings(nonSinglePlayers: List<Player>): Map<String, String> {
        var good = false
        var zipped: List<Pair<Player, Player>> = emptyList()
        while (!good) {
            val shuffled = nonSinglePlayers.shuffled()
            zipped = nonSinglePlayers.zip(shuffled)
            if (zipped.none { it.first.clientId == it.second.clientId }) {
                good = true
            }
        }
        return zipped.associate { Pair(it.first.username, it.second.username) }
    }

    override suspend fun invoke(gameManager: GameManager) {
        val cardRepo: CardRepository by KoinJavaComponent.inject(CardRepository::class.java)
        gameManager.room.let { room ->
            val nonSinglePlayers = room.players.filter { it.clientId != gameManager.single?.clientId }
            val pairs = createPairings(nonSinglePlayers)
            gameManager.sabotagePairing = gameManager.sabotagePairing.plus(pairs)
            nonSinglePlayers.forEach { player ->
                val hand =
                    DealtCards(cardRepo.getRandomCardFrom(gameManager.packs, "negative", negativeCardAmount)
                        .map { card -> CardData(card.type, card.content) })
                player.cardsInHand = hand.cards
                player.socket.send(hand.asJson())
                val dateToSabotage = gameManager.dateOptions.find { it.createdBy == pairs[player.username] }
                dateToSabotage?.let {
                    player.socket.send(DateToSabotage(it).asJson())
                } ?: run { }
            }
        }
    }
}