package aasmc.ru.playground.converters

import aasmc.ru.playground.model.MonetaryAmount
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class MonetaryAmountConverter : AttributeConverter<MonetaryAmount, String> {

    override fun convertToDatabaseColumn(attribute: MonetaryAmount): String {
        return attribute.toString()
    }

    override fun convertToEntityAttribute(dbData: String): MonetaryAmount {
        return MonetaryAmount.fromString(dbData)
    }
}