package aasmc.ru.data.repositoriesimpl

import aasmc.ru.data.cache.daos.impl.HibernateOrdersDao
import aasmc.ru.data.cache.daos.interfaces.OrdersDAO
import aasmc.ru.data.cache.models.mappers.OrdersMapper
import aasmc.ru.domain.model.Order
import aasmc.ru.domain.model.OrderItem
import aasmc.ru.domain.model.Result
import aasmc.ru.domain.repositories.OrdersRepository
import kotlinx.coroutines.runBlocking

class OrdersRepositoryImpl(
    private val ordersDAO: OrdersDAO = HibernateOrdersDao(),
    private val ordersMapper: OrdersMapper = OrdersMapper()
) : OrdersRepository {
    init {
        runBlocking {
            if (!hasOrders()) {
                val o1 = Order(
                    "2020-04-06-01", listOf(
                        OrderItem("Ham Sandwich", 2, 5.50),
                        OrderItem("Water", 1, 1.50),
                        OrderItem("Beer", 3, 2.30),
                        OrderItem("Cheesecake", 1, 3.75)
                    )
                )
                val o2 = Order(
                    "2020-04-03-01", listOf(
                        OrderItem("Cheeseburger", 1, 8.50),
                        OrderItem("Water", 2, 1.50),
                        OrderItem("Coke", 2, 1.76),
                        OrderItem("Ice Cream", 1, 2.35)
                    )
                )
                addNewOrder(o1)
                addNewOrder(o2)
            }
        }
    }

    override suspend fun hasOrders(): Boolean = ordersDAO.hasOrders()

    override suspend fun addNewOrder(order: Order): Result<Unit> =
        ordersDAO.addNewOrder(ordersMapper.mapToEntity(order))

    override suspend fun allOrders(): Result<List<Order>> {
        return when (val result = ordersDAO.allOrders()) {
            is Result.Failure -> result
            is Result.Success -> Result.Success(result.data.map(ordersMapper::mapToDomain))
        }
    }

    override suspend fun order(number: String): Result<Order?> {
        return when (val result = ordersDAO.order(number)) {
            is Result.Failure -> result
            is Result.Success -> Result.Success(result.data?.let { ordersMapper.mapToDomain(it) })
        }
    }

    override suspend fun totalAmountForOrder(number: String): Result<Double?> =
        ordersDAO.totalAmountForOrder(number)
}