package spoti.service

import spoti.data.model.CardData
import spoti.data.model.PackMetaInfo
import spoti.db.entity.Card
import spoti.db.entity.Pack

interface CardService {
    fun DealCards(amount: Int, type: String, packs: List<Int>) : List<CardData>
    fun GetPacks() : List<PackMetaInfo>
}