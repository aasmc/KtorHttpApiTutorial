package aasmc.ru.playground

import aasmc.ru.cache.hibernateproviders.TestDatabaseFactory
import aasmc.ru.cache.hibernateproviders.TestEntityProvider
import aasmc.ru.data.cache.hibernateproviders.interfaces.DatabaseFactory
import aasmc.ru.data.cache.hibernateproviders.interfaces.EntityProvider
import jakarta.persistence.EntityManagerFactory
import org.hibernate.SessionFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class AbstractTest(
    entityProvider: EntityProvider = TestEntityProvider()
) {

    lateinit var entityManagerFactory: EntityManagerFactory
    lateinit var sessionFactory: SessionFactory
    open val dbFactory: DatabaseFactory = TestDatabaseFactory(
        entityProvider = entityProvider
    )


    @BeforeEach
    fun setup() {
        entityManagerFactory = dbFactory.createEntityManagerFactory()
        sessionFactory = dbFactory.createSessionFactory()
    }

    @AfterEach
    fun tearDown() {
        if (this::entityManagerFactory.isInitialized) {
            entityManagerFactory.close()
        }
        if (this::sessionFactory.isInitialized) {
            sessionFactory.close()
        }
    }

}