package aasmc.ru.playground.collections.listofstrings

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListOfStringsTest: AbstractTest(
    entityProvider = {
        arrayOf(
            Item::class.java
        )
    }
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun storeLoadCollection() = runTest{
        val idRes = entityManagerFactory.withEntityManager {
            val item = Item().apply {
                images.add("foo.jpg")
                images.add("bar.jpg")
                images.add("baz.jpg")
                images.add("baz.jpg")
            }

            persist(item)
            item.id
        }
        assertTrue(idRes is Result.Success)
        val id = idRes.data
        entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, id)
            assertEquals(item.images.size, 4)
            assertEquals(item.images[0], "foo.jpg")
            assertEquals(item.images[1], "bar.jpg")
            assertEquals(item.images[2], "baz.jpg")
            assertEquals(item.images[3], "baz.jpg")
        }
    }
}































