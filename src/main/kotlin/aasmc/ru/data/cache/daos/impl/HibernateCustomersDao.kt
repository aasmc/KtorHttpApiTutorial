package aasmc.ru.data.cache.daos.impl

import aasmc.ru.data.cache.HibernateSessionFactory
import aasmc.ru.data.cache.daos.interfaces.CustomersDAO
import aasmc.ru.data.cache.models.CachedCustomer
import aasmc.ru.data.cache.withTransaction
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory

class HibernateCustomersDao(
    private val sessionFactory: SessionFactory = HibernateSessionFactory.sessionFactory
) : CustomersDAO {
    override suspend fun allCustomers(): List<CachedCustomer> {
        val session = sessionFactory.openSession()
        val resultList = session.withTransaction {
            val query = createQuery(
                "select c from CachedCustomer c ",
                CachedCustomer::class.java
            )
            query.resultList
        }
        return resultList.toList()
    }

    override suspend fun customer(id: String): CachedCustomer? {
        val session = sessionFactory.openSession()
        val result = session.withTransaction {
            val query = createQuery(
                "select c from CachedCustomer c where c.id = :customerId",
                CachedCustomer::class.java
            ).setParameter("customerId", id)
            query.singleResultOrNull
        }
        return result
    }

    override suspend fun addNewCustomer(customer: CachedCustomer): Boolean? {
        val session = sessionFactory.openSession()
        val persisted = session.withTransaction {
            try {
                persist(customer)
                true
            }catch (e: Exception) {
                false
            }
        }
        return persisted
    }

    override suspend fun deleteCustomer(id: String): Boolean {
        val session = sessionFactory.openSession()
        val deleted = session.withTransaction {
            val query = createQuery(
                "select c from CachedCustomer c where c.id = :customerId",
                CachedCustomer::class.java
            ).setParameter("customerId", id)
            val result = query.singleResultOrNull
            if (result == null) {
                false
            } else {
                session.remove(result)
                true
            }
        }
        return deleted
    }
}
