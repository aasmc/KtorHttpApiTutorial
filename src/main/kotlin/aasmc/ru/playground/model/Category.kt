package aasmc.ru.playground.model

import jakarta.persistence.*

@Entity
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private var id: Long = 0
) {

    constructor(name: String) : this() {
        this.name = name
    }

    @Column(name = "name", nullable = false)
    var name: String = ""

    fun getId() = id
}
