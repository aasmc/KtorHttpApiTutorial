package aasmc.ru.data.cache.hibernateproviders

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import java.io.File
import javax.sql.DataSource

object DataSourceProvider {

    fun provideDialect(): String {
        return "org.hibernate.dialect.H2Dialect"
    }

    fun provideDataSource(forTest: Boolean, config: ApplicationConfig? = null): DataSource {
        return if (forTest) {
            provideTestDatasource()
        } else {
            checkNotNull(config)
            provideDatasource(config)
        }
    }

    private fun provideTestDatasource(): DataSource {
        val driverName = "org.h2.Driver"
        val jdbcUrl = "jdbc:h2:mem:test_mem"
        return createHikariDataSource(jdbcUrl, driverName)
    }

    private fun provideDatasource(config: ApplicationConfig): DataSource {
        val driverName = config.property("storage.driverClassName").getString()
        val jdbcUrl = config.property("storage.jdbcURL").getString() +
                (config.propertyOrNull("storage.dbFilePath")?.getString()?.let {
                    File(it).canonicalFile.absolutePath
                } ?: "")
        return createHikariDataSource(
            url = jdbcUrl,
            driver = driverName
        )
    }

    private fun createHikariDataSource(
        url: String,
        driver: String
    ) = HikariDataSource(HikariConfig().apply {
        val runtimeProcessors = Runtime.getRuntime().availableProcessors()
        val maxPoolSize = if (runtimeProcessors == 0) 4 else runtimeProcessors * 4
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = maxPoolSize
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })
}