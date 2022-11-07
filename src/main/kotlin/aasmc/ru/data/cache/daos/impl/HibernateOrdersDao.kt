package aasmc.ru.data.cache.daos.impl

import aasmc.ru.data.cache.hibernateproviders.HibernateFactory
import aasmc.ru.data.cache.daos.interfaces.OrdersDAO
import aasmc.ru.data.cache.models.CachedOrder
import aasmc.ru.data.cache.withSession
import aasmc.ru.domain.model.Result
import org.hibernate.SessionFactory

class HibernateOrdersDao(
    private val sessionFactory: SessionFactory = HibernateFactory.sessionFactory
) : OrdersDAO {
    override suspend fun hasOrders(): Boolean {
        val result = sessionFactory.withSession {
            val query = createQuery(
                "select o from CachedOrder o",
                CachedOrder::class.java
            )
            query.resultList.isNotEmpty()
        }
        return when(result) {
            is Result.Failure -> false
            is Result.Success -> result.data
        }
    }

    override suspend fun addNewOrder(order: CachedOrder): Result<Unit> {
        val result = sessionFactory.withSession {
            persist(order)
        }
        return result
    }

    override suspend fun allOrders(): Result<List<CachedOrder>> {
        val result = sessionFactory.withSession {
            val query = createQuery(
                "select o from CachedOrder o",
                CachedOrder::class.java
            )
            query.resultList.toList()
        }
        return result
    }

    override suspend fun order(number: String): Result<CachedOrder?> {
        val result = sessionFactory.withSession {
            val query = createQuery(
                "select distinct o from CachedOrder o where o.number = :orderNumber",
                CachedOrder::class.java
            ).setParameter("orderNumber", number)
            query.singleResultOrNull
        }
        return result
    }

    override suspend fun totalAmountForOrder(number: String): Result<Double?> {
        val result = sessionFactory.withSession {
            val query = createQuery(
                "select distinct o from CachedOrder o where o.number = :orderNumber",
                CachedOrder::class.java
            ).setParameter("orderNumber", number)
            val order = query.singleResultOrNull
            order?.items?.sumOf { it.amount * it.price }
        }
        return result
    }
}



























