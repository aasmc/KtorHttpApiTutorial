package aasmc.ru.data.cache.hibernateproviders.impl

import aasmc.ru.data.cache.hibernateproviders.PersistenceUnitInfoImpl
import aasmc.ru.data.cache.hibernateproviders.interfaces.*
import io.ktor.server.config.*
import jakarta.persistence.EntityManagerFactory
import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.AvailableSettings
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor
import java.util.*

class AppDatabaseFactory(
    config: ApplicationConfig,
    private val entityProvider: EntityProvider = AppEntityProvider(),
    private val integratorProvider: IntegratorProvider = AppIntegratorProvider(),
    private val interceptorProvider: InterceptorProvider = AppInterceptorProvider(),
    private val dataSourceProvider: DataSourceProvider = AppDataSourceProvider(config)
) : DatabaseFactory {

    private val sessionFactory: SessionFactory by lazy(
        mode = LazyThreadSafetyMode.SYNCHRONIZED
    ) { createLazySessionFactory() }

    private val entityManagerFactory: EntityManagerFactory by lazy(
        mode = LazyThreadSafetyMode.SYNCHRONIZED
    ) {
        createLazyEntityManagerFactory()
    }

    @Synchronized
    override fun createSessionFactory(): SessionFactory {
        return sessionFactory
    }

    @Synchronized
    override fun createEntityManagerFactory(): EntityManagerFactory {
        return entityManagerFactory
    }

    private fun createLazySessionFactory(): SessionFactory {
        val registry = StandardServiceRegistryBuilder()
            .configure()
            .build()
        val factory: SessionFactory = try {
            MetadataSources(registry).buildMetadata().buildSessionFactory()
        } catch (e: Exception) {
            StandardServiceRegistryBuilder.destroy(registry)
            throw e
        }
        return factory
    }

    private fun createLazyEntityManagerFactory(): EntityManagerFactory {
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
        props["hibernate.hbm2ddl.auto"] = "update"
        props["show_sql"] = "true"
        props["hibernate.connection.datasource"] = dataSource
        props["hibernate.generate_statistics"] = "true"
        props["dialect"] = dataSourceProvider.provideDialect()
        return props
    }
}






















