package aasmc.ru.playground.querying.jpql

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.inheritance.tableperclass.BankAccount
import aasmc.ru.playground.inheritance.tableperclass.BillingDetails
import aasmc.ru.playground.inheritance.tableperclass.CreditCard
import aasmc.ru.playground.querying.AbstractQueryTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class Selection: AbstractQueryTest() {

    @Test
    fun executeQueries() = runTest {
        val td = storeTestData()
        val res = entityManagerFactory.withEntityManager {
            // This only works in Hibernate, SELECT clause isn't optional in JPA
            var query = createQuery("from Item")
            assertEquals(3, query.resultList.size)
            clear()

            query = createQuery("select bd from BillingDetails bd", BillingDetails::class.java)
            assertEquals(2, query.resultList.size)
            clear()

            query = createQuery("select cc from CreditCard cc", CreditCard::class.java)
            assertEquals(1, query.resultList.size)
            clear()

            query = createQuery("select bd from BillingDetails bd where type(bd) = CreditCard", CreditCard::class.java)
            assertEquals(1, query.resultList.size)
            clear()

            query = createQuery("select bd from BillingDetails bd where type(bd) in :types", BillingDetails::class.java)
            query.setParameter("types", listOf(CreditCard::class.java, BankAccount::class.java))
            assertEquals(2, query.resultList.size)
            clear()

            query = createQuery("select bd from BillingDetails bd where not type(bd) = BankAccount ", BillingDetails::class.java)
            assertEquals(1, query.resultList.size)
            assertTrue(query.resultList.iterator().next() is CreditCard)
        }
        assertTrue(res is Result.Success)
    }

}



















