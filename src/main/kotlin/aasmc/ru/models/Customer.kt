package aasmc.ru.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Customer(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String
)

object CustomersTable : IntIdTable("customers") {
    val firstName = varchar("firstName", 25)
    val lastName = varchar("lastName", 25)
    val email = varchar("email", 50)
}

class CustomerEntity(id: EntityID<Int>) : IntEntity(id){
    companion object : IntEntityClass<CustomerEntity>(CustomersTable)

    var firstName by CustomersTable.firstName
    var lastName by CustomersTable.lastName
    var email by CustomersTable.email
}