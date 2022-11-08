package aasmc.ru.playground.simple

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull
import kotlinx.serialization.Serializable

@Embeddable
@Serializable
class Address(
    @NotNull
    @Column(nullable = false)
    var street: String = "",

    @NotNull
    @Column(nullable = false, length = 5)
    var zipcode: String = "",

    @NotNull
    @Column(nullable = false)
    var city: String = ""
)
