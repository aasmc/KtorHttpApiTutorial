package aasmc.ru

import aasmc.ru.models.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {
    lateinit var dao: DAOFacade
    fun init(config: ApplicationConfig) {
        val driverName = config.property("storage.driverClassName").getString()
        val jdbcUrl = config.property("storage.jdbcURL").getString() +
                (config.propertyOrNull("storage.dbFilePath")?.getString()?.let {
                    File(it).canonicalFile.absolutePath
                } ?: "")
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db"
        val database = Database.connect(createHikariDataSource(
            url = jdbcUrl,
            driver = driverName
        ))

        transaction(database) {
            SchemaUtils.create(CustomersTable)
            SchemaUtils.create(Orders)
            SchemaUtils.create(OrderItems)
        }
        dao = DAOFacadeImpl()
    }

    private fun createHikariDataSource(
        url: String,
        driver: String
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}