package aasmc.ru.playground.collections.setofstrings

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetOfStringsTest : AbstractTest(
    entityProvider = {
        arrayOf(Item::class.java)
    }
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun storeLoadCollection() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val item = Item()
            item.images.add("foo.jpg")
            item.images.add("bar.jpg")
            item.images.add("baz.jpg")
            // Duplicate, filtered at Java level by HashSet!
            item.images.add("baz.jpg")

            persist(item)
            item.id
        }
        assertTrue(idRes is Result.Success)
        val id = idRes.data
        entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, id)
            assertEquals(3, item.images.size)
        }
    }

}

















