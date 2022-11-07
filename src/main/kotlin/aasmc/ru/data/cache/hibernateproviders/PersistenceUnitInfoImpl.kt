package aasmc.ru.data.cache.hibernateproviders

import jakarta.persistence.SharedCacheMode
import jakarta.persistence.ValidationMode
import jakarta.persistence.spi.ClassTransformer
import jakarta.persistence.spi.PersistenceUnitInfo
import jakarta.persistence.spi.PersistenceUnitTransactionType
import org.hibernate.jpa.HibernatePersistenceProvider
import java.net.URL
import java.util.*
import javax.sql.DataSource

class PersistenceUnitInfoImpl(
    private val persistenceUnitName: String,
    private val managedClassNames: List<String>,
    private val properties: Properties
): PersistenceUnitInfo {

    private var transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL
    private val mappingFileNames = arrayListOf<String>()
    private var jtaDataSource: DataSource? = null
    private var nonJtaDataSource: DataSource? = null

    companion object {
        const val JPA_VERSION = "2.1"
    }

    override fun getPersistenceUnitName(): String = persistenceUnitName

    override fun getPersistenceProviderClassName(): String =
        HibernatePersistenceProvider::class.java.name

    override fun getTransactionType(): PersistenceUnitTransactionType =
        transactionType

    override fun getJtaDataSource(): DataSource? {
        return jtaDataSource
    }

    fun setJtaDataSource(dataSource: DataSource): PersistenceUnitInfoImpl {
        this.jtaDataSource = dataSource
        this.nonJtaDataSource = null
        transactionType = PersistenceUnitTransactionType.JTA
        return this
    }

    override fun getNonJtaDataSource(): DataSource? {
        return nonJtaDataSource
    }

    fun setNonJtaDataSource(dataSource: DataSource): PersistenceUnitInfoImpl {
        this.nonJtaDataSource = dataSource
        this.jtaDataSource = null
        transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL
        return this
    }

    override fun getMappingFileNames(): MutableList<String> = mappingFileNames

    override fun getJarFileUrls(): MutableList<URL> {
        return emptyList<URL>().toMutableList()
    }

    override fun getPersistenceUnitRootUrl(): URL? {
        return null
    }

    override fun getManagedClassNames(): List<String> =
        managedClassNames

    override fun excludeUnlistedClasses(): Boolean = false

    override fun getSharedCacheMode(): SharedCacheMode =
        SharedCacheMode.UNSPECIFIED

    override fun getValidationMode(): ValidationMode =
        ValidationMode.AUTO

    override fun getProperties(): Properties = properties

    override fun getPersistenceXMLSchemaVersion(): String =
        JPA_VERSION

    override fun getClassLoader(): ClassLoader =
        Thread.currentThread().contextClassLoader

    override fun addTransformer(transformer: ClassTransformer?) {
    }

    override fun getNewTempClassLoader(): ClassLoader? {
        return null
    }
}