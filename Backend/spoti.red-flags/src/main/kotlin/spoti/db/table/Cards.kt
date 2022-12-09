package spoti.db.table

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import spoti.db.entity.Card

object Cards : Table<Card>("Cards") {
    val id= int("id").primaryKey().bindTo {it.id}
    val packID = int("packid").references(Packs) {it.pack}
    val type = varchar("type").bindTo { it.type }
    val content = varchar("content").bindTo { it.content }
}