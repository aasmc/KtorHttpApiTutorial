package aasmc.ru.data.cache.hibernateproviders.impl

import aasmc.ru.data.cache.hibernateproviders.interfaces.DataSourceProvider
import io.ktor.server.config.*
import java.io.File
import javax.sql.DataSource

class AppDataSourceProvider(
    private val config: ApplicationConfig
) : DataSourceProvider {

    override fun provideDataSource(): DataSource {
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

    override fun provideDialect(): String =
        "org.hibernate.dialect.H2Dialect"
}