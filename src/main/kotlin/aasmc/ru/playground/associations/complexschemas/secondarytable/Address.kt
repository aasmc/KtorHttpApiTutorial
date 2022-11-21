package aasmc.ru.playground.associations.complexschemas.secondarytable

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Address constructor() {

    @Column(nullable = false)
    var street: String = ""

    @Column(nullable = false, length = 5)
    var zipcode: String = ""

    @Column(nullable = false)
    var city: String = ""

    constructor(street: String, zipcode: String, city: String): this() {
        this.street = street
        this.zipcode = zipcode
        this.city = city
    }

}