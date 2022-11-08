package aasmc.ru.data.cache.daos.impl.jpa

import aasmc.ru.data.cache.daos.interfaces.CustomersDAO
import aasmc.ru.data.cache.models.CachedCustomer
import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.domain.model.exceptions.ItemNotFoundException
import jakarta.persistence.EntityManagerFactory

class JpaCustomerDao(
    private val entityManagerFactory: EntityManagerFactory
) : CustomersDAO {
    override suspend fun allCustomers(): Result<List<CachedCustomer>> {
        return entityManagerFactory.withEntityManager {
            val query = createQuery("select c from CachedCustomer c", CachedCustomer::class.java)
            val result = query.resultList
            result?.toList() ?: emptyList()
        }
    }

    override suspend fun customer(id: String): Result<CachedCustomer?> {
        return entityManagerFactory.withEntityManager {
            find(CachedCustomer::class.java, id)
        }
    }

    override suspend fun addNewCustomer(customer: CachedCustomer): Result<Unit> {
        return entityManagerFactory.withEntityManager {
            persist(customer)
        }
    }

    override suspend fun deleteCustomer(id: String): Result<Unit> {
        val res = entityManagerFactory.withEntityManager {
            val customer = find(CachedCustomer::class.java, id)
            if (customer != null) {
                remove(customer)
                true
            } else {
                false
            }
        }
        return when(res) {
            is Result.Failure -> res
            is Result.Success -> {
                when{
                    res.data -> Result.Success(Unit)
                    else -> Result.Failure(
                        ItemNotFoundException("Customer with id: $id not found in the database!")
                    )
                }
            }
        }
    }
}