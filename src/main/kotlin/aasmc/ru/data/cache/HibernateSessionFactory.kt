package aasmc.ru.data.cache

import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder

object HibernateSessionFactory {

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
}

