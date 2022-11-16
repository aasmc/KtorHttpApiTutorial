package aasmc.ru.playground.collections.embeddablesetofstrings

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Embeddable
class Address constructor() {
    @NotNull
    @Column(nullable = false)
    var street: String = ""

    @NotNull
    @Column(nullable = false, length = 5)
    var zipcode: String = ""

    @NotNull
    @Column(nullable = false)
    var city: String = ""

    @ElementCollection
    @CollectionTable(
        name = "CONTACT",
        joinColumns = [JoinColumn(name = "USER_ID")]
    )
    @Column(name = "NAME", nullable = false)
    var contacts: MutableSet<String> = hashSetOf()

    constructor(street: String, zipcode: String, city: String): this() {
        this.city = city
        this.zipcode = zipcode
        this.street = street
    }
}