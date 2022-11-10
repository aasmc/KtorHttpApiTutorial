package aasmc.ru.playground

import aasmc.ru.cache.hibernateproviders.TestDatabaseFactory
import aasmc.ru.data.cache.hibernateproviders.interfaces.DatabaseFactory
import jakarta.persistence.EntityManagerFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class AbstractTest {

    lateinit var entityManagerFactory: EntityManagerFactory
    private val dbFactory: DatabaseFactory = TestDatabaseFactory()


    @BeforeEach
    fun setup() {
        entityManagerFactory = dbFactory.createEntityManagerFactory()
    }

    @AfterEach
    fun tearDown() {
        if (this::entityManagerFactory.isInitialized) {
            entityManagerFactory.close()
        }
    }

}