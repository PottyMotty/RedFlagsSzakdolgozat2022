package spoti.db.entity

import org.ktorm.entity.Entity

interface Pack : Entity<Pack> {
    companion object : Entity.Factory<Pack>()
    val id: Int
    var name : String
    var positiveCount : Int
    var negativeCount : Int
}