package aasmc.ru.playground.collections.mapofstrings

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MapOfStringsTest: AbstractTest(
    entityProvider = {
        arrayOf(
            Item::class.java
        )
    }
) {
    @Test
    fun storeLoadCollection() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val item = Item().apply {
                images["foo.jpg"] = "Foo"
                images["bar.jpg"] = "Bar";
                images["baz.jpg"] = "WRONG!"
                images["baz.jpg"] = "Baz";
            }
            persist(item)
            item.id
        }
        assertTrue(idRes is Result.Success)
        val id = idRes.data
        entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, id)
            assertEquals(item.images.size, 3)
            assertEquals(item.images["foo.jpg"], "Foo")
            assertEquals(item.images["bar.jpg"], "Bar")
            assertEquals(item.images["baz.jpg"], "Baz")
        }
    }
}
























