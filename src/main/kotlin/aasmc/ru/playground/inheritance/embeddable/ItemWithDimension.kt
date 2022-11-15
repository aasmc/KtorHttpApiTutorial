package aasmc.ru.playground.inheritance.embeddable

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
class ItemWithDimension constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @NotNull
    @Size(
        min = 2,
        max = 255,
        message = "Name is required, maximum 255 characters"
    )
    var name: String = ""

    @Embedded
    var dimensions: Dimensions = Dimensions()

    @Embedded
    var weight: Weight = Weight()

    constructor(name: String, dimensions: Dimensions, weight: Weight) : this() {
        this.name = name
        this.dimensions = dimensions
        this.weight = weight
    }
}