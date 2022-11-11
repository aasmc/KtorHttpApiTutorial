package aasmc.ru.playground.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull

@Embeddable
data class City(
    @NotNull
    @Column(nullable = false, length = 5)
    var zipcode: String = "",

    @NotNull
    @Column(nullable = false)
    var name: String = "",

    @NotNull
    @Column(nullable = false)
    var country: String = ""
)