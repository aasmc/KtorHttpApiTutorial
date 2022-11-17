package aasmc.ru.playground.associations.onetoone.sharedprimarykey

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotNull

@Entity
class Address(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,

    @NotNull
    var street: String = "",

    @NotNull
    var zipcode: String = "",

    @NotNull
    var city: String = "",
) {

}