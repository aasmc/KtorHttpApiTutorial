package aasmc.ru.database

import aasmc.ru.database.dao.DAOFacade
import aasmc.ru.database.dao.DAOFacadeCacheImpl
import aasmc.ru.database.dao.DAOFacadeImpl
import aasmc.ru.models.*
import aasmc.ru.security.database.dao.SecurityDAOFacade
import aasmc.ru.security.database.dao.SecurityDAOFacadeImpl
import aasmc.ru.security.model.user.UsersTable
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
    lateinit var daoCache: DAOFacade
    lateinit var securityDao: SecurityDAOFacade
    fun init(config: ApplicationConfig) {
        val driverName = config.property("storage.driverClassName").getString()
        val jdbcUrl = config.property("storage.jdbcURL").getString() +
                (config.propertyOrNull("storage.dbFilePath")?.getString()?.let {
                    File(it).canonicalFile.absolutePath
                } ?: "")
        val database = Database.connect(
            createHikariDataSource(
                url = jdbcUrl,
                driver = driverName
            )
        )

        transaction(database) {
            SchemaUtils.create(CustomersTable)
            SchemaUtils.create(Orders)
            SchemaUtils.create(OrderItems)
            SchemaUtils.create(UsersTable)
        }
        dao = DAOFacadeImpl()
        val cacheFile = File(config.property("storage.ehcacheFilePath").getString())
        daoCache = DAOFacadeCacheImpl(dao, cacheFile)
        securityDao = SecurityDAOFacadeImpl()
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