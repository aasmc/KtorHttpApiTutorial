package aasmc.ru.playground.filtering.interceptor

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
class AuditLogRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var message: String = "",
    @NotNull
    var entityId: Long = 0,
    @NotNull
    var entityClass: Class<*>? = null,
    @NotNull
    var userId: Long = 0,
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    var createdOn: LocalDateTime = LocalDateTime.now()
) {
    constructor(message: String, entityInstance: Auditable, userId: Long): this(
        message = message,
        entityId = entityInstance.getId(),
        entityClass = entityInstance::class.java,
        userId = userId
    )
}