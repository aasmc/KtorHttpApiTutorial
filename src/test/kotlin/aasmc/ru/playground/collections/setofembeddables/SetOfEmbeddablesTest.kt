package aasmc.ru.playground.collections.setofembeddables

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetOfEmbeddablesTest: AbstractTest(
    entityProvider = { arrayOf(
        Image::class.java,
        Item::class.java
    )}
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun storeLoadCollection() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val item = Item().apply {
                images.add(
                    Image(
                        "Foo", "foo.jpg", 640, 480
                    )
                )

                images.add(
                    Image(
                        "Bar", "bar.jpg", 800, 600
                    )
                )
                images.add(
                    Image(
                        "Baz", "baz.jpg", 1024, 768
                    )
                )
                images.add(
                    Image(
                        "Baz", "baz.jpg", 1024, 768
                    )
                )
            }
            assertEquals(item.images.size, 3)
        }
        assertTrue(idRes is Result.Success)
        val id = idRes.data
        entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, id)
            assertEquals(item.images.size, 3)
        }
    }

}

























