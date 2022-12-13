package aasmc.ru.playground.querying.criteria

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.inheritance.tableperclass.BankAccount
import aasmc.ru.playground.inheritance.tableperclass.BillingDetails
import aasmc.ru.playground.inheritance.tableperclass.CreditCard
import aasmc.ru.playground.quering.Item
import aasmc.ru.playground.querying.AbstractQueryTest
import jakarta.persistence.metamodel.EntityType
import jakarta.persistence.metamodel.Metamodel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class Selection : AbstractQueryTest() {

    @Test
    fun executeQueries() = runTest {
        storeTestData()

        val cb = entityManagerFactory.criteriaBuilder

        val res = entityManagerFactory.withEntityManager {
            // This is not guaranteed to work in all JPA providers, criteria.select() should be used
            var criteria = cb.createQuery(Item::class.java)
            criteria.from(Item::class.java)
            var result = createQuery(criteria).resultList
            assertEquals(result.size, 3)
            clear()

            criteria = cb.createQuery(Item::class.java)
            val i = criteria.from(Item::class.java)
            criteria.select(i)
            result = createQuery(criteria).resultList
            assertEquals(result.size, 3)
            clear()

            // Nested calls
            criteria = cb.createQuery(Item::class.java)
            criteria.select(criteria.from(Item::class.java))
            result = createQuery(criteria).resultList
            assertEquals(result.size, 3)
            clear()

            // Restrict type with metamodel
            val c = cb.createQuery()
            val entityType = getEntityType(metamodel, "Item")
            c.select(c.from(entityType))
            val r = createQuery(c).resultList
            assertEquals(r.size, 3)
            clear()

            // Polymorphism restricted types
            var cc = cb.createQuery(BillingDetails::class.java)
            var bd = cc.from(BillingDetails::class.java)
            cc.select(bd)
            var qq = createQuery(cc)
            assertEquals(qq.resultList.size, 2)
            clear()

            cc = cb.createQuery(BillingDetails::class.java)
            cc.select(cc.from(CreditCard::class.java))
            qq = createQuery(cc)
            assertEquals(qq.resultList.size, 1)
            clear()

            cc = cb.createQuery(BillingDetails::class.java)
            bd = cc.from(BillingDetails::class.java)
            cc.select(bd).where(
                cb.not(cb.equal(bd.type(), BankAccount::class.java))
            )
            qq = createQuery(cc)
            assertEquals(qq.resultList.size, 1)
            assertTrue(qq.resultList.iterator().next() is CreditCard)
            clear()

            cc  = cb.createQuery(BillingDetails::class.java)
            bd = cc.from(BillingDetails::class.java)
            cc.select(bd).where(
                bd.type().`in`(cb.parameter(List::class.java, "types"))
            )
            qq = createQuery(cc)
            qq.setParameter("types", listOf(CreditCard::class.java, BankAccount::class.java))
            assertEquals(2, qq.resultList.size)
        }

        assertTrue(res is Result.Success)
    }

    fun getEntityType(metaModel: Metamodel, entityName: String): EntityType<*> {
        var entityType: EntityType<*>? = null
        for (t in metaModel.entities) {
            if (t.name == entityName) {
                entityType = t
                break
            }
        }
        return entityType
            ?: throw IllegalStateException("Managed entity type not found for entity name: $entityName")
    }

}
