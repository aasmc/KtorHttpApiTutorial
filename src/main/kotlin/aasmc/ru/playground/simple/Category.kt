package aasmc.ru.playground.simple

import jakarta.persistence.*

@Entity
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private var id: Long = 0
) {
    constructor(id: Long, name: String) : this(id) {
        this.name = name
    }

    constructor(name: String) : this() {
        this.name = name
    }

    @Column(name = "name")
    var name: String = ""

    fun getId() = id
}
