package aasmc.ru.playground.querying.jpql

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.quering.Bid
import aasmc.ru.playground.quering.Category
import aasmc.ru.playground.quering.Item
import aasmc.ru.playground.quering.User
import aasmc.ru.playground.querying.AbstractQueryTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class Restriction: AbstractQueryTest() {
    @Test
    fun executeQueries() = runTest {
        val td = storeTestData()
        val res = entityManagerFactory.withEntityManager {
            var iq = createQuery("select i from Item i where i.name = 'Foo'", Item::class.java)
            assertEquals(iq.resultList.size, 1)
            clear()

            var uq = createQuery(
                "select u from User u where u.activated = true", User::class.java
            )
            assertEquals(uq.resultList.size, 2)
            clear()

            var bq = createQuery(
                "select b from Bid b where b.amount between 99 and 110",
                Bid::class.java
            )
            assertEquals(bq.resultList.size, 3)
            clear()

            bq = createQuery(
                "select b from Bid b where b.amount > 100", Bid::class.java
            )
            assertEquals(bq.resultList.size, 1)
            clear()

            uq = createQuery(
                "select u from User  u where u.username in ('johndoe', 'janeroe')",
                User::class.java
            )
            assertEquals(uq.resultList.size, 2)
            clear()

            uq = createQuery(
                "select u from User u where (u.firstname, u.lastname) in (('John', 'Doe'), ('Jane', 'Roe'))",
                User::class.java
            )
            assertEquals(uq.resultList.size, 2)
            clear()

            iq = createQuery(
                "select i from Item i where i.auctionType = aasmc.ru.playground.quering.AuctionType.HIGHEST_BID",
                Item::class.java
            )
            assertEquals(iq.resultList.size, 3)
            clear()

            iq = createQuery(
                "select i from Item i where i.buyNowPrice is null",
                Item::class.java
            )
            assertEquals(iq.resultList.size, 2)
            clear()

            iq = createQuery(
                "select i from Item i where i.buyNowPrice is not null",
                Item::class.java
            )
            assertEquals(iq.resultList.size, 1)
            clear()

            uq = createQuery(
                "select u from User u where u.username like 'john%%' ",
                User::class.java
            )
            assertEquals(uq.resultList.size, 1)
            clear()

            uq = createQuery(
                "select u from User u where u.username not like 'john%%'",
                User::class.java
            )
            assertEquals(uq.resultList.size, 2)
            clear()

            uq = createQuery(
                "select u from User u where u.username like '%%oe%%'",
                User::class.java
            )
            assertEquals(uq.resultList.size, 3)
            clear()

            iq = createQuery(
                "select i from Item i where i.name like 'Name\\_with\\_underscores' escape :escapeChar",
                Item::class.java
            )
            iq.setParameter("escapeChar", '\\')
            assertEquals(iq.resultList.size, 0)
            clear()

            iq = createQuery(
                "select i from Item i where (i.name like 'Fo%%' and i.buyNowPrice is not null)" +
                        "or i.name = 'Bar'",
                Item::class.java
            )
            assertEquals(iq.resultList.size, 2)
            clear()

            iq = createQuery(
                "select i from Item i where lower(i.name) like 'ba%%'",
                Item::class.java
            )
            assertEquals(iq.resultList.size, 2)
            clear()

            var cq = createQuery(
                "select c from Category c where c.items is not empty ",
                Category::class.java
            )
            assertEquals(cq.resultList.size, 2)
            clear()

            cq = createQuery(
                "select c from Category c where :item member of c.items",
                Category::class.java
            )
            val item = find(Item::class.java, td.items.getFirstId())
            cq.setParameter("item", item)
            val result = cq.resultList
            assertEquals(result.size, 1)
            clear()

            cq = createQuery(
                "select c from Category c where size(c.items) > 1",
                Category::class.java
            )
            assertEquals(cq.resultList.size, 1)
            clear()

            uq = createQuery(
                "select u from User u order by u.username",
                User::class.java
            )
            assertEquals(uq.resultList.size, 3)
        }
        assertTrue(res is Result.Success)
    }
}





























