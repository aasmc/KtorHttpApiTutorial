package aasmc.ru.playground.inheritance.onetomany

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractInheritanceTestOneToMany
import aasmc.ru.playground.inheritance.associations.onetomany.BankAccount
import aasmc.ru.playground.inheritance.associations.onetomany.CreditCard
import aasmc.ru.playground.inheritance.associations.onetomany.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PolymorphicOneToMany: AbstractInheritanceTestOneToMany() {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun storeAndLoadUsers() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val ba = BankAccount(
                "Jane Roe", "445566", "One Percent Bank Inc.", "999"
            )
            val cc = CreditCard(
                "John Doe", "1234123412341234", "06", "2015"
            )

            val johndoe = User("johndoe")
            johndoe.billingDetails.add(ba)
            ba.user = johndoe

            johndoe.billingDetails.add(cc)
            cc.user = johndoe

            persist(cc)
            persist(ba)
            persist(johndoe)
            johndoe.id
        }

        assertTrue(idRes is Result.Success)
        val id = idRes.data

        entityManagerFactory.withEntityManager {
            val user = find(User::class.java, id)
            for(bd in user.billingDetails) {
                bd.pay(123)
            }
            assertEquals(2, user.billingDetails.size)
        }
    }
}





























