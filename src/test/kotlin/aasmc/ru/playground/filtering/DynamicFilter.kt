package aasmc.ru.playground.filtering

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.filtering.dynamic.Category
import aasmc.ru.playground.filtering.dynamic.Item
import aasmc.ru.playground.filtering.dynamic.User
import aasmc.ru.util.TestData
import io.ktor.server.sessions.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hibernate.Session
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DynamicFilter : AbstractTest(
    entityProvider = {
        arrayOf(
            User::class.java,
            Category::class.java,
            Item::class.java
        )
    }
) {

    data class DynamicFilterTestData(
        val categories: TestData,
        val items: TestData,
        val users: TestData
    )

    fun storeTestData(): DynamicFilterTestData = runBlocking {
        val res = entityManagerFactory.withEntityManager {
            val td = DynamicFilterTestData(
                categories = TestData(LongArray(2)),
                items = TestData(LongArray(3)),
                users = TestData(LongArray(2)),
            )
            val jd = User(username = "johndoe")
            persist(jd)
            td.users.identifiers[0] = jd.id!!
            val jr = User(username = "janeroe", rank = 100)
            persist(jr)
            td.users.identifiers[1] = jr.id!!

            val c1 = Category(name = "One")
            persist(c1)
            td.categories.identifiers[0] = c1.id!!

            val c2 = Category(name = "Two")
            persist(c2)
            td.categories.identifiers[1] = c2.id!!

            val foo = Item(name = "Foo", category = c1, seller = jd)
            persist(foo)
            td.items.identifiers[0] = foo.id!!

            val bar = Item(name = "Bar", category = c1, seller = jr)
            persist(bar)
            td.items.identifiers[1] = bar.id!!

            val baz = Item(name = "Baz", category = c2, seller = jr)
            persist(baz)
            td.items.identifiers[2] = baz.id!!
            td
        }
        assertTrue(res is Result.Success)
        res.data
    }

    @Test
    fun filterItems() = runTest {
        val td = storeTestData()
        val res = entityManagerFactory.withEntityManager {
            val filter: org.hibernate.Filter = unwrap(Session::class.java)
                .enableFilter("limitByUserRank")
            filter.setParameter("currentUserRank", 0)
            val items =
                createQuery("select i from Item i", Item::class.java)
                    .resultList
            // select * from ITEM where 0 >=
            // (select u.RANK from USERS u where u.ID = SELLER_ID)
            assertEquals(1, items.size)

            val cb = criteriaBuilder
            val criteria = cb.createQuery()
            criteria.select(criteria.from(Item::class.java))
            val cbItems = createQuery(criteria).resultList as MutableList<Item>
            assertEquals(1, cbItems.size)

            clear()
            filter.setParameter("currentUserRank", 100)
            val newItems = createQuery("select i from Item i")
                .resultList
            assertEquals(3, newItems.size)
        }

        assertTrue(res is Result.Success)
    }

    @Test
    fun filterCollection() = runTest {
        val td = storeTestData()
        val res = entityManagerFactory.withEntityManager {
            val catId = td.categories.getFirstId()
            val filter: org.hibernate.Filter = unwrap(Session::class.java)
                .enableFilter("limitByUserRank")
            filter.setParameter("currentUserRank", 0)
            val category = find(Category::class.java, catId)
            assertEquals(1, category.items.size)

            clear()

            filter.setParameter("currentUserRank", 100)
            val secCategory = find(Category::class.java, catId)
            assertEquals(2, secCategory.items.size)
        }
        assertTrue(res is Result.Success)
    }

}
















