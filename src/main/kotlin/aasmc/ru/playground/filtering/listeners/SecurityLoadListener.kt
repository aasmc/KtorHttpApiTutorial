package aasmc.ru.playground.filtering.listeners

import org.hibernate.event.internal.DefaultLoadEventListener
import org.hibernate.event.spi.LoadEvent
import org.hibernate.event.spi.LoadEventListener

/**
 * A listener should be considered effectively a singleton,
 * meaning it’s shared between persistence contexts and thus
 * shouldn’t save any transaction-related state as instance variables.
 */
class SecurityLoadListener : DefaultLoadEventListener() {
    override fun onLoad(event: LoadEvent?, loadType: LoadEventListener.LoadType?) {
        val authorized = MySecurity.isAuthorized(
            event?.entityClassName, event?.entityId
        )
        if (!authorized) {
            throw MySecurityException("Unauthorized access")
        }

        super.onLoad(event, loadType)
    }
}

class MySecurity {
    companion object {
        fun isAuthorized(entityName: String?, entityId: Any?): Boolean {
            return true
        }
    }
}

class MySecurityException(message: String) : RuntimeException(message)