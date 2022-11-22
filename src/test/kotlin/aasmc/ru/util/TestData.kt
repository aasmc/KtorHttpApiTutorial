package aasmc.ru.util

class TestData(
    private val identifiers: LongArray = longArrayOf()
) {
    fun gerFirstId(): Long {
        return if (identifiers.isNotEmpty())
            identifiers[0]
        else throw IllegalStateException(
            "TestData has empty array of identifiers!"
        )
    }

    fun getLastId(): Long {
        return if (identifiers.isNotEmpty()) {
            identifiers[identifiers.lastIndex]
        } else {
            throw IllegalStateException(
                "TestData has empty array of identifiers!"
            )
        }
    }
}
