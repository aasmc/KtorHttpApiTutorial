package aasmc.ru.playground

import aasmc.ru.cache.hibernateproviders.TestDatabaseFactory
import aasmc.ru.data.cache.hibernateproviders.interfaces.DatabaseFactory
import jakarta.persistence.EntityManagerFactory
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

abstract class AbstractTest {

    companion object {
        lateinit var entityManagerFactory: EntityManagerFactory
        private val dbFactory: DatabaseFactory = TestDatabaseFactory()

        @JvmStatic
        @BeforeAll
        fun setup() {
            entityManagerFactory = dbFactory.createEntityManagerFactory()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            if (this::entityManagerFactory.isInitialized) {
                entityManagerFactory.close()
            }
        }
    }

}