package aasmc.ru.playground.fetching

import org.hibernate.proxy.HibernateProxy

object HibernateProxyHelper {
    fun getClassWithoutInitializingProxy(any: Any): Class<*> {
        if (any is HibernateProxy) {
            val li = any.hibernateLazyInitializer
            return li.persistentClass
        }
        return any.javaClass
    }
}