package aasmc.ru.playground.associations.onetoone.jointable

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Item protected constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    var name: String = ""

    constructor(name: String) : this() {
        this.name = name
    }
}