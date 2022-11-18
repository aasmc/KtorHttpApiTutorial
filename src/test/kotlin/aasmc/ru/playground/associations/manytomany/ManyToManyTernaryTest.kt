package aasmc.ru.playground.associations.manytomany

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.associations.manytomany.ternary.CategorizedItem
import aasmc.ru.playground.associations.manytomany.ternary.Category
import aasmc.ru.playground.associations.manytomany.ternary.Item
import aasmc.ru.playground.associations.manytomany.ternary.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ManyToManyTernaryTest : AbstractTest(
    entityProvider = {
        arrayOf(
            Item::class.java,
            User::class.java,
            Category::class.java,
            CategorizedItem::class.java
        )
    }
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun storeLoad() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val someCategory = Category(name = "Some Category")
            val otherCategory = Category(name = "Other Category")

            persist(someCategory)
            persist(otherCategory)

            val someItem = Item(name = "Some Item")
            val otherItem = Item(name = "Other Item")
            persist(someItem)
            persist(otherItem)

            val user = User(userName = "JohnDoe")
            persist(user)

            val linkOne = CategorizedItem(
                addedBy = user, item = someItem
            )
            someCategory.items.add(linkOne)

            val linkTwo = CategorizedItem(
                addedBy = user, item = otherItem
            )
            someCategory.items.add(linkTwo)

            val linkThree = CategorizedItem(
                addedBy = user, item = someItem
            )
            otherCategory.items.add(linkThree)

            listOf(someCategory.id, otherCategory.id, someItem.id, user.id)
        }

        assertTrue(idRes is Result.Success)
        val (someCatId, otherCatId, someItemId, userId) = idRes.data
        entityManagerFactory.withEntityManager {
            val cat1 = find(Category::class.java, someCatId)
            val cat2 = find(Category::class.java, otherCatId)

            val item1 = find(Item::class.java, someItemId)

            val user = find(User::class.java, userId)

            assertEquals(cat1.items.size, 2)
            assertEquals(cat2.items.size, 1)

            assertEquals(cat2.items.iterator().next().item, item1)
            assertEquals(cat2.items.iterator().next().addedBy, user)
        }

        entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, someItemId)
            val categoriesOfItem =
                createQuery("select c from Category c " +
                        "join c.items ci where ci.item = :itemParam", Category::class.java)
                    .setParameter("itemParam", item)
                    .resultList

            assertEquals(categoriesOfItem.size, 2)
        }
    }
}



























