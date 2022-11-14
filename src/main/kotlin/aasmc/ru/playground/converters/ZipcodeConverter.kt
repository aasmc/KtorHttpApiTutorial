package aasmc.ru.playground.converters

import aasmc.ru.playground.model.GermanZipcode
import aasmc.ru.playground.model.SwissZipcode
import aasmc.ru.playground.model.Zipcode
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class ZipcodeConverter: AttributeConverter<Zipcode, String> {
    override fun convertToDatabaseColumn(attribute: Zipcode): String {
        return attribute.value
    }

    override fun convertToEntityAttribute(s: String): Zipcode {
        return when (s.length) {
            5 -> {
                GermanZipcode(s)
            }
            4 -> {
                SwissZipcode(s)
            }
            else -> {
                throw IllegalArgumentException("Unsupported zipcode in database: $s")
            }
        }
    }
}