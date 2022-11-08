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

    private var entityManagerFactory: EntityManagerFactory? = null

    private var testEntityManagerFactory: EntityManagerFactory? = null

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

    @Synchronized
    fun createTestEntityManagerFactory(): EntityManagerFactory {
        if (testEntityManagerFactory == null) {
            val builder = createEntityManagerFactoryBuilder(
                forTest = true,
                config = null
            )
            testEntityManagerFactory = builder.build()
        }
        return testEntityManagerFactory!!
    }

    @Synchronized
    fun createEntityManagerFactory(config: ApplicationConfig): EntityManagerFactory {
        if (entityManagerFactory == null) {
            val builder = createEntityManagerFactoryBuilder(
                forTest = false,
                config = config
            )
            entityManagerFactory = builder.build()
        }
        return entityManagerFactory!!
    }

    private fun createEntityManagerFactoryBuilder(
        forTest: Boolean,
        config: ApplicationConfig?
    ): EntityManagerFactoryBuilderImpl {
        val persistenceUnitInfo =
            persistenceUnitInfo(this::class.java.simpleName, config, forTest)

        val configuration = properties(forTest, config) as MutableMap<String, Any>
        InterceptorProvider.provideInterceptor()?.let {
            configuration[AvailableSettings.INTERCEPTOR] = it
        }
        IntegratorProvider.provideIntegrator()?.let {
            configuration["hibernate.integrator_provider"] =
                ({ listOf(it) } as org.hibernate.jpa.boot.spi.IntegratorProvider)
        }
        return EntityManagerFactoryBuilderImpl(
            PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration
        )
    }

    private fun entities(forTest: Boolean): Array<Class<*>> {
        return EntityProvider.provideEntities(forTest)
    }

    private fun entityClassNames(forTest: Boolean): List<String> =
        entities(forTest).map {
            it.name
        }

    private fun persistenceUnitInfo(
        name: String,
        config: ApplicationConfig?,
        forTest: Boolean
    ): PersistenceUnitInfoImpl {
        return PersistenceUnitInfoImpl(
            name, entityClassNames(forTest), properties(forTest, config)
        )
    }


    private fun properties(forTest: Boolean, config: ApplicationConfig?): Properties {
        val dataSource = DataSourceProvider.provideDataSource(forTest, config)
        val props = Properties()
        if (forTest) {
            props["hibernate.hbm2ddl.auto"] = "create-drop"
        } else {
            props["hibernate.hbm2ddl.auto"] = "update"
        }
        props["show_sql"] = "true"
        props["hibernate.connection.datasource"] = dataSource
        props["hibernate.generate_statistics"] = "true"
        props["dialect"] = DataSourceProvider.provideDialect()
        return props
    }
}

