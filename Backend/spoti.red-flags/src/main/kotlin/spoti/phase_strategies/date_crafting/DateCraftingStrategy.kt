package spoti.phase_strategies.date_crafting

import io.ktor.websocket.*
import org.koin.java.KoinJavaComponent
import spoti.data.model.CardData
import spoti.data.model.websocket.out.DealtCards
import spoti.game.GameManager
import spoti.other.Phase
import spoti.other.extensions.asJson
import spoti.phase_strategies.PhaseBaseStrategy
import spoti.repo.CardRepository

class DateCraftingStrategy(
    private val positiveCardsAmount: Int
) : PhaseBaseStrategy(Phase.DATE_CRAFTING) {
    override suspend fun invoke(gameManager: GameManager) {
        val cardRepo: CardRepository by KoinJavaComponent.inject(CardRepository::class.java)
        gameManager.room.let { room ->
            room.players
                .filter { it.clientId != gameManager.single?.clientId }
                .forEach { player ->
                    val cards =
                        cardRepo.getRandomCardFrom(gameManager.packs, "positive", positiveCardsAmount)
                            .map { card -> CardData(card.type, card.content) }
                    player.cardsInHand = cards
                    player.socket.send(DealtCards(cards).asJson())
                }
        }
    }
}