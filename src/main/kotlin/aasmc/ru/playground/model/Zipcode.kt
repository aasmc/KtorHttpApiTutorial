package aasmc.ru.playground.model

abstract class Zipcode(
    val value: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val o: Zipcode = (other as? Zipcode) ?: return false
        return value == o.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}