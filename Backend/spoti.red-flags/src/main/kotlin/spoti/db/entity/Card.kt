package spoti.db.entity

import org.ktorm.entity.Entity

interface Card : Entity<Card> {
    companion object : Entity.Factory<Card>()
    val id: Int
    var pack: Pack
    val type : String
    val content : String
}