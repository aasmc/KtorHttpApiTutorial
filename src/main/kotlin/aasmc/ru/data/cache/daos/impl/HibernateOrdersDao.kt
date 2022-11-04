package aasmc.ru.data.cache.daos.impl

import aasmc.ru.data.cache.HibernateSessionFactory
import aasmc.ru.data.cache.daos.interfaces.OrdersDAO
import aasmc.ru.data.cache.models.CachedOrder
import aasmc.ru.data.cache.withTransaction
import org.hibernate.SessionFactory

class HibernateOrdersDao(
    private val sessionFactory: SessionFactory = HibernateSessionFactory.sessionFactory
) : OrdersDAO {
    override suspend fun hasOrders(): Boolean {
        val session = sessionFactory.openSession()
        val hasOrders = session.withTransaction {
            val query = createQuery(
                "select o from CachedOrder o",
                CachedOrder::class.java
            )
            query.resultList.isNotEmpty()
        }
        return hasOrders
    }

    override suspend fun addNewOrder(order: CachedOrder): Boolean {
        val session = sessionFactory.openSession()
        val persisted = session.withTransaction {
            try {
                persist(order)
                true
            } catch (e: Exception) {
                false
            }
        }
        return persisted
    }

    override suspend fun allOrders(): List<CachedOrder> {
        val session = sessionFactory.openSession()
        val result = session.withTransaction {
            val query = createQuery(
                "select o from CachedOrder o",
                CachedOrder::class.java
            )
            query.resultList
        }
        return result.toList()
    }

    override suspend fun order(number: String): CachedOrder? {
        val session = sessionFactory.openSession()
        val result = session.withTransaction {
            val query = createQuery(
                "select o from CachedOrder o where o.number = :orderNumber",
                CachedOrder::class.java
            ).setParameter("orderNumber", number)
            query.singleResultOrNull
        }
        return result
    }

    override suspend fun totalAmountForOrder(number: String): Double {
        val session = sessionFactory.openSession()
        val result = session.withTransaction {
            val query = createQuery(
                "select o from CachedOrder o where o.number = :orderNumber",
                CachedOrder::class.java
            ).setParameter("orderNumber", number)
            val order = query.singleResultOrNull
            order.items.sumOf { it.amount * it.price }
        }
        return result
    }
}



























