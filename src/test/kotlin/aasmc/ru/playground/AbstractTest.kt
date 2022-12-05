package aasmc.ru.playground

import aasmc.ru.cache.hibernateproviders.TestDatabaseFactory
import aasmc.ru.cache.hibernateproviders.TestEntityProvider
import aasmc.ru.cache.hibernateproviders.TestInterceptorProvider
import aasmc.ru.data.cache.hibernateproviders.interfaces.DatabaseFactory
import aasmc.ru.data.cache.hibernateproviders.interfaces.EntityProvider
import aasmc.ru.data.cache.hibernateproviders.interfaces.InterceptorProvider
import jakarta.persistence.EntityManagerFactory
import org.hibernate.SessionFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class AbstractTest(
    entityProvider: EntityProvider = TestEntityProvider(),
    interceptorProvider: InterceptorProvider = TestInterceptorProvider()
) {

    lateinit var entityManagerFactory: EntityManagerFactory
    lateinit var sessionFactory: SessionFactory
    open val dbFactory: DatabaseFactory = TestDatabaseFactory(
        entityProvider = entityProvider,
        interceptorProvider = interceptorProvider
    )

    open fun afterJpaBootStrap() {

    }

    open fun beforeJpaClose() {

    }

    @BeforeEach
    fun setup() {
        entityManagerFactory = dbFactory.createEntityManagerFactory()
        sessionFactory = dbFactory.createSessionFactory()
        afterJpaBootStrap()
    }

    @AfterEach
    fun tearDown() {
        beforeJpaClose()
        if (this::entityManagerFactory.isInitialized) {
            entityManagerFactory.close()
        }
        if (this::sessionFactory.isInitialized) {
            sessionFactory.close()
        }
    }

}