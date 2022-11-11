package aasmc.ru.playground.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import kotlinx.serialization.Serializable

@Embeddable
data class Address(
    @NotNull
    @Column(nullable = false)
    var street: String = "",

    @Embedded
    @NotNull
    @AttributeOverrides(
        AttributeOverride(
            name = "name",
            column = Column(name = "CITY", nullable = false)
        )
    )
    var city: City = City()
)
