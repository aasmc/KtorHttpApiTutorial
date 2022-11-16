package aasmc.ru.playground.collections.mapofembeddables

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

/**
 * Since this class is used as a key in a map, then
 * its properties (DB columns) must not be null,
 * because they are all pars of the primary key.
 *
 * We also MUST override equals and hashcode.
 */
@Embeddable
class FileName constructor() {
    @Column(nullable = false) // Must be NOT NULL, part of PK!
    var name: String = ""

    @Column(nullable = false) // Must be NOT NULL, part of PK!
    var extension: String = ""

    constructor(name: String, extension: String): this() {
        this.name= name
        this.extension = extension
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val o: FileName = other as? FileName ?: return false
        if (extension != o.extension) return false
        if (name != o.name) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + extension.hashCode()
        return result
    }
}