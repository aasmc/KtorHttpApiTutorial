package aasmc.ru.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Order(
    val number: String,
    val contents: List<OrderItem>
)

@Serializable
data class OrderItem(
    val item: String,
    val amount: Int,
    val price: Double
)

object Orders : IntIdTable() {
    val number = varchar("number", 75)
}

object OrderItems : IntIdTable() {
    val item = varchar("item", 50)
    val amount = integer("amount")
    val price = double("price")
    val order = reference("order", Orders)
}

class OrderEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OrderEntity>(Orders)

    var number by Orders.number
    val items by OrderItemsEntity referrersOn OrderItems.order
}

class OrderItemsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OrderItemsEntity>(OrderItems)

    var item by OrderItems.item
    var amount by OrderItems.amount
    var price by OrderItems.price
    var order by OrderEntity referencedOn OrderItems.order
}

val orderStorage = mutableListOf<Order>(
    Order(
        "2020-04-06-01", listOf(
            OrderItem("Ham Sandwich", 2, 5.50),
            OrderItem("Water", 1, 1.50),
            OrderItem("Beer", 3, 2.30),
            OrderItem("Cheesecake", 1, 3.75)
        )
    ),
    Order(
        "2020-04-03-01", listOf(
            OrderItem("Cheeseburger", 1, 8.50),
            OrderItem("Water", 2, 1.50),
            OrderItem("Coke", 2, 1.76),
            OrderItem("Ice Cream", 1, 2.35)
        )
    )
)