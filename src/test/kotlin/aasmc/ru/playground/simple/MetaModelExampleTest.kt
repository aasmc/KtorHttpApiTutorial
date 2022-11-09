package aasmc.ru.playground.simple

import aasmc.ru.data.cache.hibernateproviders.HibernateFactory
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.metamodel.Attribute
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class MetaModelExampleTest {

    private lateinit var entityManagerFactory: EntityManagerFactory

    @BeforeEach
    fun setup() {
        entityManagerFactory = HibernateFactory.createTestEntityManagerFactory()
    }

    @AfterEach
    fun tearDown() {
        if (this::entityManagerFactory.isInitialized) {
            entityManagerFactory.close()
        }
    }

    @Test
    fun metaModelExample() {
        val metaModel = entityManagerFactory.metamodel
        val managedTypes = metaModel.managedTypes
        // there are 4 entities: Bid, Item, Category and User
        // and 1 @Embeddable type Address
        assertEquals(5, managedTypes.size)

        val list = managedTypes.toList()
        val itemType = list.filter { it.javaType == Item::class.java }
            .firstNotNullOfOrNull { it } ?: throw NullPointerException()

        val namedAttribute = itemType.getSingularAttribute("name")
        assertEquals(
            namedAttribute.javaType,
            String::class.java
        )

        assertEquals(
            namedAttribute.persistentAttributeType,
            Attribute.PersistentAttributeType.BASIC
        )

        assertFalse(
            namedAttribute.isOptional
        )
        val auctionEndAttribute =
            itemType.getSingularAttribute("auctionEnd")
        assertEquals(
            auctionEndAttribute.javaType,
            Date::class.java
        )

        assertFalse(
            auctionEndAttribute.isCollection
        )
        assertFalse(
            auctionEndAttribute.isAssociation
        )
    }

}


























