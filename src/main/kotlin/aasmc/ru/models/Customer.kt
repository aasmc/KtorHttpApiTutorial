package aasmc.ru.models

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String
)

// simple in-memory storage of customers
val customerStorage = mutableListOf<Customer>()