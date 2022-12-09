package spoti.db.table

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import spoti.db.entity.Pack

object Packs : Table<Pack>("Packs") {
    val id = int("id").primaryKey().bindTo{ it.id}
    val name = varchar("name").bindTo { it.name }
    val positive_count = int("positive_count").bindTo { it.positiveCount }
    val negative_count = int("negative_count").bindTo { it.negativeCount }

}