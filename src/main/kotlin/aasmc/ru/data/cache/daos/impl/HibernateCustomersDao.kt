package aasmc.ru.data.cache.daos.impl

import aasmc.ru.data.cache.hibernateproviders.HibernateFactory
import aasmc.ru.data.cache.daos.interfaces.CustomersDAO
import aasmc.ru.data.cache.models.CachedCustomer
import aasmc.ru.data.cache.withSession
import aasmc.ru.domain.model.exceptions.ItemNotFoundException
import aasmc.ru.domain.model.Result
import org.hibernate.SessionFactory

class HibernateCustomersDao(
    private val sessionFactory: SessionFactory = HibernateFactory.sessionFactory
) : CustomersDAO {
    override suspend fun allCustomers(): Result<List<CachedCustomer>> {
        val result = sessionFactory.withSession {
            val query = createQuery(
                "select c from CachedCustomer c ",
                CachedCustomer::class.java
            )
            query.resultList.toList()
        }
        return result
    }

    override suspend fun customer(id: String): Result<CachedCustomer?> {
        val result = sessionFactory.withSession {
            val query = createQuery(
                "select c from CachedCustomer c where c.id = :customerId",
                CachedCustomer::class.java
            ).setParameter("customerId", id)
            query.singleResultOrNull
        }
        return result
    }

    override suspend fun addNewCustomer(customer: CachedCustomer): Result<Unit> {
        val result = sessionFactory.withSession {
            persist(customer)
        }
        return result
    }

    override suspend fun deleteCustomer(id: String): Result<Unit> {
        val result = sessionFactory.withSession {
            createMutationQuery("delete from CachedCustomer where id = :customerId")
                .setParameter("customerId", id).executeUpdate()
//            val query = createQuery(
//                "delete from CachedCustomer where id = :customerId",
//                CachedCustomer::class.java
//            ).setParameter("customerId", id)
//            query.executeUpdate()
        }
        return when(result) {
            is Result.Failure -> result
            is Result.Success -> {
                when {
                    result.data > 0 -> Result.Success(Unit)
                    else -> Result.Failure(
                        ItemNotFoundException("Customer with id: $id not found in the database!")
                    )
                }
            }
        }
    }
}
