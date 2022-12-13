package aasmc.ru.playground.querying

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.quering.Item
import jakarta.persistence.FlushModeType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hibernate.Session
import org.hibernate.query.Query
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CreateExecuteQueries : AbstractQueryTest() {

    @Test
    fun createQueries() = runTest {
        storeTestData()
        val res = entityManagerFactory.withEntityManager {
            val query = createQuery("select i from Item i")
            assertEquals(query.resultList.size, 3)

            val cb = criteriaBuilder
            // Also available on EntityManagerFactory:
            // CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();

            val criteria = cb.createQuery(Item::class.java)
            criteria.select(criteria.from(Item::class.java))

            val criteriaQuery = createQuery(criteria)
            assertEquals(criteriaQuery.resultList.size, 3)

            val nativeQuery = createNativeQuery(
                "select * from ITEM", Item::class.java
            )
            assertEquals(nativeQuery.resultList.size, 3)
        }
        assertTrue(res is Result.Success)
    }

    @Test
    fun createTypedQueries() = runTest {
        val td = storeTestData()
        val itemId = td.items.getFirstId()

        val res = entityManagerFactory.withEntityManager {
            val query = createQuery(
                "select i from Item i where i.id = :id", Item::class.java
            ).setParameter("id", itemId)

            val item = query.singleResult
            assertEquals(itemId, item.id)

            val cb = criteriaBuilder
            val criteria = cb.createQuery(Item::class.java)
            val root = criteria.from(Item::class.java)
            criteria.select(root).where(
                cb.equal(
                    root.get<Long>("id"), itemId
                )
            )

            val criteriaQuery = createQuery(criteria)
            val criteriaItem = criteriaQuery.singleResult
            assertEquals(itemId, criteriaItem.id)
        }
        assertTrue(res is Result.Success)
    }

    @Test
    fun pagination() = runTest {
        storeTestData()
        val res = entityManagerFactory.withEntityManager {
            var items: MutableList<Item> = mutableListOf()

            val query = createQuery("select i from Item i", Item::class.java)
            query.setFirstResult(40).setMaxResults(10)
            items = query.resultList
            assertEquals(items.size, 0)

            val native = createNativeQuery("select * from ITEM", Item::class.java)
            native.setFirstResult(40).setMaxResults(10)
            items = native.resultList as MutableList<Item>
            assertEquals(items.size, 0)

            // Getting total count with a cursor
            val newQuery = createQuery("select i from Item i", Item::class.java)
            val hibernateQuery = newQuery.unwrap(Query::class.java)
            val cursor =
                hibernateQuery.scroll(org.hibernate.ScrollMode.SCROLL_INSENSITIVE)

            /*
               Execute the query with a database cursor; this does not retrieve the
               result set into memory.
             */
            cursor.last()
            val count = cursor.rowNumber + 1
            // You must close the database cursor.
            cursor.close()
            hibernateQuery.setFirstResult(40).setMaxResults(10)
            assertEquals(3, count)
            items = hibernateQuery.resultList as MutableList<Item>
            assertEquals(items.size, 0)
        }
        assertTrue(res is Result.Success)
    }

    @Test
    fun namedQueries() = runTest {
        val td = storeTestData()
        val itemId = td.items.getFirstId()
        val res = entityManagerFactory.withEntityManager {
            val session = unwrap(Session::class.java)

            var query = createNamedQuery("findItems", Item::class.java)
            assertEquals(query.resultList.size, 3)

            query = createNamedQuery("findItemById", Item::class.java)
            query.setParameter("id", itemId)
            val result = query.singleResult
            assertEquals(itemId, result.id!!)
        }
        assertTrue(res is Result.Success)
    }

    @Test
    fun queryHints() = runTest {
        storeTestData()
        val res = entityManagerFactory.withEntityManager {
            val queryString = "select i from Item i"
            // Not supported on PostgreSQL
            var query = createQuery(queryString, Item::class.java)
                .setHint("jakarta.persistence.query.timeout", 60000) // 1 minute

            assertEquals(3, query.resultList.size)

            query = createQuery(queryString, Item::class.java)
                .setFlushMode(FlushModeType.COMMIT)
            assertEquals(3, query.resultList.size)

            query = createQuery(queryString, Item::class.java)
                .setHint(org.hibernate.jpa.AvailableHints.HINT_READ_ONLY, true)
            assertEquals(3, query.resultList.size)

            query = createQuery(queryString, Item::class.java)
                .setHint(
                    org.hibernate.jpa.AvailableHints.HINT_COMMENT,
                    "Custom SQL comment"
                )
            assertEquals(3, query.resultList.size)
        }
        assertTrue(res is Result.Success)
    }
}


























