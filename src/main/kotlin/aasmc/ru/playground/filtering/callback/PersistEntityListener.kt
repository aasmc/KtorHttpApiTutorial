package aasmc.ru.playground.filtering.callback

import jakarta.persistence.PostPersist

/**
 * An entity listener class must have either no constructor or a public no-argument
 * constructor. It doesn't have to implement any special interfaces. An entity listener
 * is stateless; the JPA engine automatically creates and destroys it.
 *
 * Only one method of the listener can be annotated @PostPersist.
 *
 * Allowed listener method annotations:
 *  1. @PostLoad
 *  2. @PrePersist @PostPersist
 *  4. @PreUpdate, @PostUpdate
 *  5. @PreRemove, @PostRemove
 */
class PersistEntityListener constructor() {
    /*
      You may annotate any method of an entity listener class as a callback method
      for persistence life cycle events. The <code>notifyAdmin()</code> method is
      invoked after a new entity instance is stored in the database.
      If the listener is used only for a particular type of Entity, then
      the method may accept the parameter of that Entity type.
      If the method throws a RuntimeException, Hibernate will abort the operation
      and mark the current transaction for rollback.
    */
    @PostPersist
    fun notifyAdmin(entityInstance: Any) {
        /*
          Because event listener classes are stateless, it can be difficult to get
          more contextual information when you need it. Here, you want the currently
          logged in user, and access to the email system to send a notification. A primitive
          solution is to use thread-local variables and singletons.
        */
        val currentUser = CurrentUser.INSTANCE.get()
        val mail = Mail.INSTANCE
        mail.send("""
            Entity instance persisted by $currentUser: $entityInstance
        """.trimIndent())
    }
}