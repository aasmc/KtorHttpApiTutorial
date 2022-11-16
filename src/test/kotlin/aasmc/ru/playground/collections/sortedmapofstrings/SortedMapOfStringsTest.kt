package aasmc.ru.playground.collections.sortedmapofstrings

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SortedMapOfStringsTest: AbstractTest(
    entityProvider = {
        arrayOf(
            Item::class.java
        )
    }
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun storeLoadCollection() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val item = Item().apply {
                images["foo.jpg"] = "Foo"
                images["bar.jpg"] = "Bar"
                images["baz.jpg"] = "WRONG!"
                images["baz.jpg"] = "Baz"
            }
            persist(item)
            item.id
        }
        assertTrue(idRes is Result.Success)
        val id = idRes.data
        entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, id)

            // Sorted in-memory with TreeMap
            val iterator = item.images.entries.iterator()
            var entry = iterator.next()
            assertEquals(entry.key, "foo.jpg")
            assertEquals(entry.value, "Foo")

            entry = iterator.next()
            assertEquals(entry.key, "baz.jpg")
            assertEquals(entry.value, "Baz")

            entry = iterator.next()
            assertEquals(entry.key, "bar.jpg")
            assertEquals(entry.value, "Bar")

        }
    }
}



















