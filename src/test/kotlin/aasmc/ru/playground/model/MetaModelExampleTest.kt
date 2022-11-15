package aasmc.ru.playground.model

import aasmc.ru.playground.AbstractTest
import jakarta.persistence.metamodel.Attribute
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class MetaModelExampleTest : AbstractTest (){

    @Test
    fun metaModelExample() {
        val metaModel = entityManagerFactory.metamodel
        val managedTypes = metaModel.managedTypes
        // there are 4 entities: Bid, Item, Category, User
        // and 3 @Embeddable type Address and City MonetaryAmount
        assertEquals(7, managedTypes.size)

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
            Instant::class.java
        )

        assertFalse(
            auctionEndAttribute.isCollection
        )
        assertFalse(
            auctionEndAttribute.isAssociation
        )
    }

}


























