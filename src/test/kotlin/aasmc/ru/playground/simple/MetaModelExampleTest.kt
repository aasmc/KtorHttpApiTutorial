package aasmc.ru.playground.simple

import aasmc.ru.playground.AbstractTest
import jakarta.persistence.metamodel.Attribute
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class MetaModelExampleTest : AbstractTest (){

    @Test
    fun metaModelExample() {
        val metaModel = entityManagerFactory.metamodel
        val managedTypes = metaModel.managedTypes
        // there are 4 entities: Bid, Item, Category, User
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


























