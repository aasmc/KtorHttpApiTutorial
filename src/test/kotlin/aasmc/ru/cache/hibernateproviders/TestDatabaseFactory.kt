package aasmc.ru.cache.hibernateproviders

import aasmc.ru.data.cache.hibernateproviders.PersistenceUnitInfoImpl
import aasmc.ru.data.cache.hibernateproviders.interfaces.*
import jakarta.persistence.EntityManagerFactory
import org.hibernate.SessionFactory
import org.hibernate.cfg.AvailableSettings
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor
import java.util.*

class TestDatabaseFactory(
    private val entityProvider: EntityProvider = TestEntityProvider(),
    private val integratorProvider: IntegratorProvider = TestIntegratorProvider(),
    private val interceptorProvider: InterceptorProvider = TestInterceptorProvider(),
    private val dataSourceProvider: DataSourceProvider = TestDataSourceProvider()
) : DatabaseFactory {

    private val emFactory: EntityManagerFactory by lazy() { createLazyEMFactory() }

    override fun createSessionFactory(): SessionFactory {
        val emf = createEntityManagerFactory()
        return emf.unwrap(SessionFactory::class.java)
    }

    override fun createEntityManagerFactory(): EntityManagerFactory {
        return emFactory
    }

    private fun createLazyEMFactory(): EntityManagerFactory {
        return createEntityManagerFactoryBuilder().build()
    }

    private fun createEntityManagerFactoryBuilder(): EntityManagerFactoryBuilderImpl {
        val persistenceUnitInfo =
            persistenceUnitInfo(this::class.java.simpleName)

        val configuration = properties() as MutableMap<String, Any>
        interceptorProvider.provideInterceptor()?.let {
            configuration[AvailableSettings.INTERCEPTOR] = it
        }
        integratorProvider.provideIntegrator()?.let {
            configuration["hibernate.integrator_provider"] =
                ({ listOf(it) } as org.hibernate.jpa.boot.spi.IntegratorProvider)
        }
        return EntityManagerFactoryBuilderImpl(
            PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration
        )
    }

    private fun entities(): Array<Class<*>> {
        return entityProvider.provideEntities()
    }

    private fun entityClassNames(): List<String> =
        entities().map {
            it.name
        }

    private fun persistenceUnitInfo(
        name: String,
    ): PersistenceUnitInfoImpl {
        return PersistenceUnitInfoImpl(
            name, entityClassNames(), properties()
        )
    }

    private fun properties(): Properties {
        val dataSource = dataSourceProvider.provideDataSource()
        val props = Properties()
        props["hibernate.hbm2ddl.auto"] = "create-drop"
        props["show_sql"] = "true"
        props["hibernate.connection.datasource"] = dataSource
        props["hibernate.generate_statistics"] = "true"
        props["hibernate.auto_quote_keyword"] = "true"
        props["dialect"] = dataSourceProvider.provideDialect()
        return props
    }
}