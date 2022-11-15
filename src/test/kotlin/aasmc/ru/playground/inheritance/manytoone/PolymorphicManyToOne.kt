package aasmc.ru.playground.inheritance.manytoone

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractInheritanceTestManyToOne
import aasmc.ru.playground.inheritance.associations.manytoone.User
import aasmc.ru.playground.inheritance.joined.CreditCard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PolymorphicManyToOne : AbstractInheritanceTestManyToOne() {
    @Test
    fun storeAndLoadUsers() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val cc = CreditCard(
                "John Doe", "1234567890", "06", "2015"
            )
            val johnDoe = User("johndoe")
            johnDoe.defaultBilling = cc
            persist(cc)
            persist(johnDoe)

            johnDoe.id
        }

        assertTrue(idRes is Result.Success)
        val id = (idRes as Result.Success).data
        entityManagerFactory.withEntityManager {
            val user = find(User::class.java, id)
            user.defaultBilling!!.pay(123)

            assertEquals("John Doe", user.defaultBilling!!.owner)
        }

        entityManagerFactory.withEntityManager {
            val user = find(User::class.java, id)
            val bd = user.defaultBilling
            assertFalse(bd is CreditCard)

            // don't do this: ClassCastException
            // val creditCard = bd as CreditCard
        }

        entityManagerFactory.withEntityManager {
            val user = find(User::class.java, id)
            val bd = user.defaultBilling
            val creditCard = getReference(CreditCard::class.java, bd!!.id) // No SELECT!
            assertTrue(bd != creditCard) // Careful
        }

        entityManagerFactory.withEntityManager {
            val user = createQuery(
                "select u from User u left join fetch u.defaultBilling where u.id = :id",
                User::class.java
            ).setParameter("id", id)
                .singleResult
            // no proxy has been used, the BillingDetails instance has been fetched eagerly
            val creditCard = user.defaultBilling as CreditCard
        }
    }
}




























