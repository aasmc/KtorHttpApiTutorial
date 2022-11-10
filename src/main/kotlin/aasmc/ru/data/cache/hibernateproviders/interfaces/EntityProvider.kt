package aasmc.ru.data.cache.hibernateproviders.interfaces

interface EntityProvider {
    fun provideEntities(): Array<Class<*>>
}