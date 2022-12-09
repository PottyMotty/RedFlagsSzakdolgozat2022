package spoti.service

import spoti.data.model.CardData
import spoti.data.model.PackMetaInfo
import spoti.db.entity.Card
import spoti.db.entity.Pack
import spoti.db.table.Packs
import spoti.repo.CardRepository

class CardServiceImpl(private val cardRepo: CardRepository) : CardService{
    override fun DealCards(amount: Int, type: String, packs: List<Int>): List<CardData> {
        val cards = cardRepo.getRandomCardFrom(packs,type,amount)
        val cardsRes = mutableListOf<CardData>()
        return cards.mapTo(cardsRes) {c-> CardData(c.type,c.content)}
    }

    override fun GetPacks(): List<PackMetaInfo> {
        val packs: List<Pack> =cardRepo.getPacks()
        val packsRes = mutableListOf<PackMetaInfo>()
        return packs.mapTo(packsRes) {p -> PackMetaInfo(
            p.id,p.name,p.positiveCount,p.negativeCount)
        }
    }


}