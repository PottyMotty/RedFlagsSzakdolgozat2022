package spoti.repo



import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.selects.select
import org.ktorm.database.Database
import org.ktorm.database.SqlDialect
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf
import spoti.db.entity.Card
import spoti.db.entity.Pack
import spoti.db.table.Cards
import spoti.db.table.Packs
import java.util.stream.Collectors.toList
import javax.swing.text.html.parser.Entity
import kotlin.random.Random

class CardRepository {

    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val dbUrl =appConfig.property("ktor.db.jdbc").getString()
    private val localDBUrl =appConfig.property("ktor.localDB.jdbc").getString()
    private val isDev =System.getProperty("io.ktor.development").toBoolean()

    private val dataSource by lazy{
        val config =HikariConfig().apply {
            jdbcUrl=if(isDev) localDBUrl else dbUrl
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            maximumPoolSize=10
        }
        HikariDataSource(config)
    }

    private val database by lazy {
        Database.connect(dataSource=dataSource,
        dialect = org.ktorm.support.postgresql.PostgreSqlDialect()
        )
    }

    fun getRandomCardFrom(packs : List<Int>, type : String, amount: Int) : List<Card> {
        return database.from(Cards)
            .select()
            .whereWithConditions {
                it +=Cards.packID.inList(packs)
                it +=Cards.type eq type
            }
            .map { row -> Cards.createEntity(row) }
            .shuffled().subList(0,amount)
    }

    fun getPacks(): List<Pack> {
        return database.from(Packs).select().map { row -> Packs.createEntity(row) }
    }

}