package aasmc.ru.util

class TestData(
    val identifiers: LongArray = longArrayOf()
) {
    fun getFirstId(): Long {
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
