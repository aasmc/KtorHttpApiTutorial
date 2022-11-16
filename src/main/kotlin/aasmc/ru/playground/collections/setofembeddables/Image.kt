package aasmc.ru.playground.collections.setofembeddables

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

/**
 * Hibernate generates primary key out of all
 * non-nullable columns of the IMAGE collection table.
 */
@Embeddable
class Image constructor() {
    @Column(nullable = false)
    var title: String = ""

    @Column(nullable = false)
    var filename: String = ""

    @Column(nullable = false)
    var width: Int = 0

    @Column(nullable = false)
    var height: Int = 0

    @org.hibernate.annotations.Parent
    var item: Item = Item()

    constructor(title: String, filename: String, width: Int, height: Int) : this() {
        this.title = title
        this.filename = filename
        this.width = width
        this.height = height
    }

    // Whenever value-types are managed in collections, overriding equals/hashCode is a good idea!
    // here we don't include Item in equality checks, therefore mixing Images of different Items
    // in a set or map will cause problems
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val o: Image = (other as? Image) ?: return false
        if (title != o.title) return false
        if (filename != o.filename) return false
        if (width != o.width) return false
        if (height != o.height) return false
        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + filename.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}