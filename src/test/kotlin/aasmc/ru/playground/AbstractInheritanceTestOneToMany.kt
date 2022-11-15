package aasmc.ru.playground

import aasmc.ru.cache.hibernateproviders.InheritanceEntityProviderOneToMany
import aasmc.ru.cache.hibernateproviders.TestDatabaseFactory
import aasmc.ru.data.cache.hibernateproviders.interfaces.DatabaseFactory
import jakarta.persistence.EntityManagerFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class AbstractInheritanceTestOneToMany {
    lateinit var entityManagerFactory: EntityManagerFactory
    open val dbFactory: DatabaseFactory = TestDatabaseFactory(
        entityProvider = InheritanceEntityProviderOneToMany()
    )


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