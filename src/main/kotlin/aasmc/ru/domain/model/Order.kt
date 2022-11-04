package aasmc.ru.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class Order(
    val number: String,
    val contents: List<OrderItem>
): java.io.Serializable

@Serializable
data class OrderItem(
    val item: String,
    val amount: Int,
    val price: Double
) : java.io.Serializable