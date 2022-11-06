package aasmc.ru.domain.repositories

import aasmc.ru.domain.model.Order
import aasmc.ru.domain.model.Result

interface OrdersRepository {
    suspend fun hasOrders(): Boolean
    suspend fun addNewOrder(order: Order): Result<Unit>
    suspend fun allOrders(): Result<List<Order>>
    suspend fun order(number: String): Result<Order?>
    suspend fun totalAmountForOrder(number: String): Result<Double?>
}