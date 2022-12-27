package aasmc.ru.playground.model

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pooled")
    @GenericGenerator(
        name = "pooled",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = [
            org.hibernate.annotations.Parameter(name = "sequence_name", value = "sequence"),
            org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
            org.hibernate.annotations.Parameter(name = "increment_size", value = "3"),
            org.hibernate.annotations.Parameter(name = "optimizer", value = "pooled")
        ]
    )
    @Column(name = "id", nullable = false, updatable = false)
    private var id: Long? = null,

    @Column(name = "name", nullable = false, unique = true)
    var name: String = ""
) {
    fun getId() = id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val o: Category = (other as? Category) ?: return false
        return Objects.equals(name, o.name)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(name)
    }

    override fun toString(): String {
        return "Category: [id=$id, name=$name]"
    }
}
