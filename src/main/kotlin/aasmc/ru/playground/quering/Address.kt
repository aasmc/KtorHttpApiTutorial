package aasmc.ru.playground.quering

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull

@Embeddable
class Address(
    @NotNull
    var street: String = "",
    @NotNull
    @Column(length = 5)
    var zipcode: String = "",
    @NotNull
    var city: String = ""
)