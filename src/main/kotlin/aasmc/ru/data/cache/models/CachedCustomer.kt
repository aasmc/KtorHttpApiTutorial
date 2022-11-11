package aasmc.ru.data.cache.models

import kotlinx.serialization.Serializable
import jakarta.persistence.*


/**
 * Need to make the class open to enable Hibernate proxying and
 * lazy loading in associations.
 */
@Table(name = "customers")
@Entity
@Serializable
data class CachedCustomer(
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    var id: String = "",
) {
    @Column(name = "first_name", nullable = false)
    var firstName: String = ""
    @Column(name = "last_name", nullable = false)
    var lastName: String = ""
    @Column(name = "email", nullable = false)
    var email: String = ""

    constructor(id: String, firstName: String, lastName: String, email: String): this(id) {
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
    }

    override fun toString(): String =
        "Customer: [id = $id, firstName = $firstName, lastName = $lastName, email = $email]"
}