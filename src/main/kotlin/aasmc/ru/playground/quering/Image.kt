package aasmc.ru.playground.quering

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull

@Embeddable
class Image(
    @NotNull
    @Column(nullable = false)
    var name: String = "",

    @NotNull
    @Column(nullable = false)
    var filename: String = "",

    @NotNull
    var width: Int = 0,

    @NotNull
    var height: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val o: Image = (other as? Image) ?: return false
        if (width != o.width) return false
        if (height != o.height) return false
        if (filename != o.filename) return false
        if (name != o.name) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + filename.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}