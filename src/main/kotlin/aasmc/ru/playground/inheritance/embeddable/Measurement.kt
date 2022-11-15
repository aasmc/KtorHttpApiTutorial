package aasmc.ru.playground.inheritance.embeddable

import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class Measurement constructor() {
    var name: String = ""

    var symbol: String = ""

    constructor(name: String, symbol: String): this() {
        this.name = name
        this.symbol = symbol
    }
}