package aasmc.ru.playground.querying.criteria

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
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
        val cb = entityManagerFactory.criteriaBuilder
        val res = entityManagerFactory.withEntityManager {
            var ic = cb.createQuery(Item::class.java)
            val root = ic.from(Item::class.java)
            ic.select(root).where(
                cb.equal(root.get<String>("name"), "Foo")
            )
            var iq = createQuery(ic)
            assertEquals(iq.resultList.size, 1)
            assertEquals(iq.resultList.iterator().next().name, "Foo")
            clear()

            var uc = cb.createQuery(User::class.java)
            val uRoot = uc.from(User::class.java)
            uc.select(uRoot).where(
                cb.equal(uRoot.get<Boolean>("activated"), true)
            )
            var uq = createQuery(uc)
            assertEquals(uq.resultList.size, 2)
        }
        assertTrue(res is Result.Success)
    }
}