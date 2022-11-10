package aasmc.ru.cache.hibernateproviders

import aasmc.ru.data.cache.hibernateproviders.interfaces.DataSourceProvider
import javax.sql.DataSource

class TestDataSourceProvider : DataSourceProvider {
    override fun provideDataSource(): DataSource {
        val driverName = "org.h2.Driver"
        val jdbcUrl = "jdbc:h2:mem:test_mem"
        return createHikariDataSource(jdbcUrl, driverName)
    }

    override fun provideDialect(): String =
        "org.hibernate.dialect.H2Dialect"

}