package aasmc.ru.playground.namingstragegy

import org.apache.commons.lang3.StringUtils
import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment
import java.util.*

/**
 * Defines a custom Physical naming strategy, that may be used in the
 * application.
 *
 * This strategy implies the use of underscores instead of camelCase names
 * in DB tables. It also replaces some words in the DB names.
 *
 * All sequences should end with _seq
 */
class AppNamingStrategy : PhysicalNamingStrategyStandardImpl() {
    private val ABBREVIATIONS: Map<String, String> = TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER).apply {
        put("account", "acct")
        put("number", "num")
    }

    override fun toPhysicalTableName(
        logicalName: Identifier?,
        context: JdbcEnvironment?
    ): Identifier {
        return logicalName?.let { lName ->
            val parts = splitAndReplace(lName.text)
            context?.identifierHelper?.toIdentifier(
                StringUtils.join(parts, '_'),
                lName.isQuoted
            )
        } ?: super.toPhysicalTableName(logicalName, context)
    }

    override fun toPhysicalSequenceName(
        logicalName: Identifier?,
        context: JdbcEnvironment?
    ): Identifier {
        return logicalName?.let { lName ->
            val parts = splitAndReplace(lName.text).toMutableList()
            if ("seq" != parts.last()) {
                parts.add("seq")
            }
            context?.identifierHelper?.toIdentifier(
                StringUtils.join(parts, '_'),
                lName.isQuoted
            )
        } ?: super.toPhysicalSequenceName(logicalName, context)
    }

    override fun toPhysicalColumnName(
        logicalName: Identifier?,
        context: JdbcEnvironment?
    ): Identifier {
        return logicalName?.let { lName ->
            val parts = splitAndReplace(lName.text)
            context?.identifierHelper?.toIdentifier(
                StringUtils.join(parts, '_'),
                lName.isQuoted
            )
        } ?: super.toPhysicalColumnName(logicalName, context)
    }

    private fun splitAndReplace(name: String): List<String> {
        return StringUtils.splitByCharacterTypeCamelCase(name)
            .filter(StringUtils::isNotBlank)
            .map { p -> ABBREVIATIONS.getOrDefault(p, p).lowercase(Locale.ROOT) }
    }

}