package aasmc.ru.data.cache.hibernateproviders.interfaces

fun interface EntityProvider {
    fun provideEntities(): Array<Class<*>>
}