package aasmc.ru.playground.associations.onetoone

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.associations.onetoone.foreigngenerators.Address
import aasmc.ru.playground.associations.onetoone.foreigngenerators.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OneToOneForeignKeyGeneratorTest: AbstractTest(
    entityProvider = {
        arrayOf(
            User::class.java,
            Address::class.java
        )
    }
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun storeLoadUserAddress() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val user = User("JohnDoe")
            val address = Address(
                user, // Link
                "Some Street 123", "12345", "Some City"
            )
            user.shippingAddress = address // Link

            persist(user)
            user.id to address.id
        }

        assertTrue(idRes is Result.Success)
        val (userId, addressId) = idRes.data
        entityManagerFactory.withEntityManager {
            val user = find(User::class.java, userId)
            assertEquals("12345", user.shippingAddress!!.zipcode)

            val address = find(Address::class.java, addressId)
            assertEquals("12345", address.zipcode)
            assertEquals(address.id, user.id)
            assertEquals(address.user, user)
        }
    }
}




























