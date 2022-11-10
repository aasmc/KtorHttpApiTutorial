package aasmc.ru.data.cache.hibernateproviders.interfaces

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

interface DataSourceProvider {

    fun provideDataSource(): DataSource

    fun provideDialect(): String

    fun createHikariDataSource(
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