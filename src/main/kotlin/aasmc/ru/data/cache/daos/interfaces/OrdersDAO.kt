package aasmc.ru.data.cache.daos.interfaces

import aasmc.ru.data.cache.models.CachedOrder
import aasmc.ru.domain.model.Result

interface OrdersDAO {
    suspend fun hasOrders(): Boolean
    suspend fun addNewOrder(order: CachedOrder): Result<Unit>
    suspend fun allOrders(): Result<List<CachedOrder>>
    suspend fun order(number: String): Result<CachedOrder?>
    suspend fun totalAmountForOrder(number: String): Result<Double?>
}