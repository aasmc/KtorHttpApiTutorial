package aasmc.ru.playground.model

import aasmc.ru.playground.converters.ZipcodeConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull

@Embeddable
data class City(
    @NotNull
    @Column(nullable = false, length = 5)
    @Convert(
        converter = ZipcodeConverter::class,
        attributeName = "zipcode"
    )
    var zipcode: Zipcode? = null,

    @NotNull
    @Column(nullable = false)
    var name: String = "",

    @NotNull
    @Column(nullable = false)
    var country: String = ""
)