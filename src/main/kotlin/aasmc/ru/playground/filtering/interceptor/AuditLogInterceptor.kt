package aasmc.ru.playground.filtering.interceptor

import org.hibernate.Interceptor
import org.hibernate.Session
import org.hibernate.type.Type

class AuditLogInterceptor : Interceptor {
    /*
       You need to access the database to write the audit log, so this interceptor
       needs a Hibernate <code>Session</code>. You also want to store the identifier
       of the currently logged-in user in each audit log record. The <code>inserts</code>
       and <code>updates</code> instance variables are collections where this interceptor
       will hold its internal state.
     */
    var currentSession: Session? = null
    var currentUserId: Long? = null

    private var inserts: MutableSet<Auditable> = hashSetOf()
    private var updates: MutableSet<Auditable> = hashSetOf()

    /**
     * This method is called when an entity instance is made persistent.
     */
    override fun onSave(
        entity: Any?,
        id: Any?,
        state: Array<out Any>?,
        propertyNames: Array<out String>?,
        types: Array<out Type>?
    ): Boolean {
        if (entity is Auditable) {
            inserts.add(entity)
        }
        return false // We didn't modify the currentState
    }

    /**
     * This method is called when an entity instance is detected as dirty
     * during flushing of the persistence context.
     */
    override fun onFlushDirty(
        entity: Any?,
        id: Any?,
        currentState: Array<out Any>?,
        previousState: Array<out Any>?,
        propertyNames: Array<out String>?,
        types: Array<out Type>?
    ): Boolean {
        if (entity is Auditable) {
            updates.add(entity)
        }
        return false // We didn't modify the currentState
    }

    /**
     *  This method is called after flushing of the persistence context is complete.
     *  Here, you write the audit log records for all insertions and updates you
     *  collected earlier.
     */
    override fun postFlush(entities: MutableIterator<Any>?) {
        /*
           You are not allowed to access the original persistence context, the
           <code>Session</code> that is currently executing this interceptor.
           The <code>Session</code> is in a fragile state during interceptor calls.
           Hibernate allows you to create a new <code>Session</code> that
           inherits some information from the original <code>Session</code> with
           the <code>sessionWithOptions()</code> method. Here the new temporary
           <code>Session</code> works with the same transaction and database
           connection as the original <code>Session</code>.
         */
        val tmpSession =
            currentSession!!.sessionWithOptions()
                .connection()
                .openSession()

        try {
            /*
                You store a new <code>AuditLogRecord</code> for each insertion and
                update using the temporary <code>Session</code>.
              */
            for (entity in inserts) {
                tmpSession.persist(
                    AuditLogRecord(
                        message = "insert",
                        entityInstance = entity,
                        userId = currentUserId ?: 0
                    )
                )
            }

            for (entity in updates) {
                tmpSession.persist(
                    AuditLogRecord(
                        message = "update",
                        entityInstance = entity,
                        userId = currentUserId ?: 0
                    )
                )
            }

        } finally {
            tmpSession.close()
            inserts.clear()
            updates.clear()
        }
    }
}





















