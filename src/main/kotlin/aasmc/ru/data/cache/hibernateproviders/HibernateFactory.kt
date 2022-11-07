package aasmc.ru.data.cache.hibernateproviders

import io.ktor.server.config.*
import jakarta.persistence.EntityManagerFactory
import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.AvailableSettings
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor
import java.util.*

object HibernateFactory {

    val sessionFactory: SessionFactory by lazy { createSessionFactory() }

    private fun createSessionFactory(): SessionFactory {
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

    fun createEntityManagerFactory(config: ApplicationConfig): EntityManagerFactory {
        val persistenceUnitInfo =
            persistenceUnitInfo(this::class.java.simpleName, config)

        val configuration= properties(config) as MutableMap<String, Any>
        InterceptorProvider.provideInterceptor()?.let {
            configuration[AvailableSettings.INTERCEPTOR] = it
        }
        IntegratorProvider.provideIntegrator()?.let {
            configuration["hibernate.integrator_provider"] = ({ listOf(it) } as org.hibernate.jpa.boot.spi.IntegratorProvider)
        }
        val entityManagerFactoryBuilder = EntityManagerFactoryBuilderImpl(
            PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration
        )
        return entityManagerFactoryBuilder.build()
    }

    private fun entities(): Array<Class<*>> {
        return EntityProvider.provideEntities()
    }

    private fun entityClassNames(): List<String> =
        entities().map {
            it.name
        }

    private fun persistenceUnitInfo(name: String, config: ApplicationConfig): PersistenceUnitInfoImpl {
        return PersistenceUnitInfoImpl(
            name, entityClassNames(), properties(config)
        )
    }

    private fun properties(config: ApplicationConfig): Properties {
        val dataSource = DataSourceProvider.provideDatasource(config)
        val props = Properties()
        props["hibernate.hbm2ddl.auto"] = "update"
        props["show_sql"] = "true"
        props["hibernate.connection.datasource"] = dataSource
        props["hibernate.generate_statistics"] = "true"
        props["dialect"] = DataSourceProvider.provideDialect()
        return props
    }
}

