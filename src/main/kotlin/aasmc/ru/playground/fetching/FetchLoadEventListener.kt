package aasmc.ru.playground.fetching

import jakarta.persistence.EntityManagerFactory
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.event.spi.PostLoadEvent
import org.hibernate.event.spi.PostLoadEventListener
import org.hibernate.service.ServiceRegistry

class FetchLoadEventListener(
    private val emf: EntityManagerFactory
) : PostLoadEventListener {
    init {
        val serviceRegistry: ServiceRegistry =
            (emf.unwrap(org.hibernate.SessionFactory::class.java) as SessionFactoryImplementor).serviceRegistry
        serviceRegistry.getService(EventListenerRegistry::class.java)
            .appendListeners(EventType.POST_LOAD, this)

    }

    private val loadCountMap: MutableMap<Class<*>, Int> = hashMapOf()

    override fun onPostLoad(event: PostLoadEvent) {
        val clazz = event.entity.javaClass
        loadCountMap.merge(clazz, 1, Int::plus)
    }

    fun getLoadCount(entityType: Class<*>): Int {
        return loadCountMap[entityType] ?: 0
    }

    fun reset() {
        loadCountMap.clear()
    }
}