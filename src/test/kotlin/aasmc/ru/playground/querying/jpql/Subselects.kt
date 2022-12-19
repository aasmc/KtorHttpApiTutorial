package aasmc.ru.playground.querying.jpql

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.quering.Bid
import aasmc.ru.playground.quering.Item
import aasmc.ru.playground.quering.User
import aasmc.ru.playground.querying.AbstractQueryTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Subselects: AbstractQueryTest() {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun executeQueries() = runTest {
        val td = storeTestData()
        val res = entityManagerFactory.withEntityManager {
            val correlated = createQuery(
                "select u from User u " +
                        "where (" +
                        "select count(i) from Item i where i.seller = u" +
                        ") > 1",
                User::class.java
            )
            val correlatedRes = correlated.resultList
            assertEquals(correlatedRes.size, 1)
            val user = correlatedRes.iterator().next()
            assertEquals(user.id, td.users.getFirstId())
            clear()

            val uncorrelated = createQuery(
                "select b from Bid b " +
                        "where b.amount + 1 >= (" +
                            "select max(b2.amount) from Bid b2" +
                        ")",
                Bid::class.java
            )
            val uRes = uncorrelated.resultList
            assertEquals(uRes.size, 2)
            clear()

            val exists = createQuery(
                "select i from Item i " +
                        "where exists (" +
                            "select b from Bid b where b.item = i" +
                        ")",
                Item::class.java
            )
            val eRes = exists.resultList
            assertEquals(eRes.size, 2)
            clear()

            val quantifyAll = createQuery(
                "select i from Item i " +
                        "where 10 >= all(" +
                            "select b.amount from i.bids b" +
                        ")",
                Item::class.java
            )
            val qAresult = quantifyAll.resultList
            assertEquals(qAresult.size, 2)
            clear()

            val quantifyAny = createQuery(
                "select i from Item i " +
                        "where 101.00 = any(" +
                            "select b.amount from i.bids b" +
                        ")",
                Item::class.java
            )
            val anyRes = quantifyAny.resultList
            assertEquals(anyRes.size, 1)
        }
        assertTrue(res is Result.Success)
    }

}


















