package aasmc.ru.playground.collections.mapofembeddables

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Image constructor() {
    @Column(nullable = true) // Can be null if we have surrogate PK!
    var title: String? = ""

    @Column(nullable = false)
    var width: Int = 0

    @Column(nullable = false)
    var height: Int = 0

    constructor(title: String, width: Int, height: Int) : this() {
        this.title = title
        this.width = width
        this.height = height
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val o: Image = (other as? Image) ?: return false
        if (title == null && other.title != null) return false
        if (title != null && other.title == null) return false
        if (title != other.title) return false
        if (width != o.width) return false
        if (height != o.height) return false
        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 1
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}