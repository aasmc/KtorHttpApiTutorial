package aasmc.ru.database.dao

import aasmc.ru.database.DatabaseFactory.dbQuery
import aasmc.ru.models.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

class DAOFacadeImpl : DAOFacade {

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

    private fun resultRowToCustomer(row: ResultRow) = Customer(
        id = row[CustomersTable.id].value.toString(),
        firstName = row[CustomersTable.firstName],
        lastName = row[CustomersTable.lastName],
        email = row[CustomersTable.email]
    )

    private fun mapEntityToCustomer(customerEntity: CustomerEntity) = Customer(
        id = customerEntity.id.value.toString(),
        firstName = customerEntity.firstName,
        lastName = customerEntity.lastName,
        email = customerEntity.email
    )

    override suspend fun allCustomers(): List<Customer> = dbQuery {
        CustomerEntity.all().map(::mapEntityToCustomer)
    }

    override suspend fun customer(id: String): Customer? = dbQuery {
        val entity = CustomerEntity.findById(id.toInt()) ?: return@dbQuery null
        mapEntityToCustomer(entity)
    }

    override suspend fun addNewCustomer(customer: Customer): Customer = dbQuery {
        val entity = CustomerEntity.new(customer.id.toInt()) {
            firstName = customer.firstName
            lastName = customer.lastName
            email = customer.email
        }
        mapEntityToCustomer(entity)
    }

    override suspend fun deleteCustomer(id: String): Boolean = dbQuery {
        val entity = CustomerEntity.findById(id.toInt()) ?: return@dbQuery false
        entity.delete()
        return@dbQuery true
    }

    override suspend fun hasOrders(): Boolean = dbQuery{
        !Orders.selectAll().empty()
    }

    override suspend fun addNewOrder(orderToSave: Order): Order {
        val items = orderToSave.contents
        dbQuery {
            val orderEntity = OrderEntity.new {
                number = orderToSave.number
            }
            for (orderItem in items) {
                OrderItemsEntity.new {
                    item = orderItem.item
                    amount = orderItem.amount
                    price = orderItem.price
                    order = orderEntity
                }
            }
        }
        return orderToSave
    }

    override suspend fun allOrders(): List<Order> = dbQuery {
        val entities = OrderEntity.all()
        if (entities.empty()) return@dbQuery emptyList<Order>()
        entities.map { entity ->
                mapToOrder(entity)
            }
    }

    private fun mapToOrder(entity: OrderEntity): Order {
        return Order(
            number = entity.number,
            contents = entity.items.map { i ->
                OrderItem(
                    item = i.item,
                    amount = i.amount,
                    price = i.price
                )
            }
        )
    }

    override suspend fun order(number: String): Order? = dbQuery {
        val result = OrderEntity.find {
            Orders.number eq number
        }.limit(1)
        if (result.empty()) return@dbQuery null
        val entity = result.first()
        return@dbQuery mapToOrder(entity)
    }

    override suspend fun totalAmountForOrder(number: String): Double = dbQuery {
        val result = OrderEntity.find {
            Orders.number eq number
        }.limit(1)
        if (result.empty()) return@dbQuery -1.0
        val entity = result.first()
        val total = entity.items.sumOf { it.price * it.amount }
        total
    }
}
