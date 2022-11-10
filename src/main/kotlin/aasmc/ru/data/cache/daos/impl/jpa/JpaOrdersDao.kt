package aasmc.ru.data.cache.daos.impl.jpa

import aasmc.ru.data.cache.daos.interfaces.OrdersDAO
import aasmc.ru.data.cache.models.CachedOrder
import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import jakarta.persistence.EntityManagerFactory

class JpaOrdersDao(
    private val entityManagerFactory: EntityManagerFactory
) : OrdersDAO {

    override suspend fun hasOrders(): Boolean {
        val result = entityManagerFactory.withEntityManager {
            val result = createQuery("select o from CachedOrder o", CachedOrder::class.java).resultList
            result != null && result.isNotEmpty()
        }
        return when (result) {
            is Result.Success -> result.data
            is Result.Failure -> false
        }
    }

    override suspend fun addNewOrder(order: CachedOrder): Result<Unit> {
        return entityManagerFactory.withEntityManager {
            persist(order)
        }
    }

    override suspend fun allOrders(): Result<List<CachedOrder>> {
        return entityManagerFactory.withEntityManager {
            val query = createQuery(
                "select o from CachedOrder o",
                CachedOrder::class.java
            )
            query.resultList.toList()
        }
    }

    override suspend fun order(number: String): Result<CachedOrder?> {
        return entityManagerFactory.withEntityManager {
            createQuery(
                "select o from CachedOrder o where o.number = :number",
                CachedOrder::class.java
            ).setParameter("number", number)
                .resultList.firstNotNullOfOrNull { it }
        }
    }

    override suspend fun totalAmountForOrder(number: String): Result<Double?> {
        return entityManagerFactory.withEntityManager {
            val query = createQuery(
                "select o from CachedOrder o where o.number = :number",
                CachedOrder::class.java
            ).setParameter("number", number)
            val order = query.resultList.firstNotNullOfOrNull { it }
            order?.items?.sumOf { it.price * it.amount }
        }
    }
}