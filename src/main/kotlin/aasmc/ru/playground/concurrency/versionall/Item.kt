package aasmc.ru.playground.concurrency.versionall

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.jetbrains.annotations.NotNull

@Entity
/**
 * This annotation can be used to enable versioning without
 * an explicit version column. It is required that DynamicUpdates
 * are also enabled.
 */
@org.hibernate.annotations.OptimisticLocking(
    type = org.hibernate.annotations.OptimisticLockType.ALL
)
@org.hibernate.annotations.DynamicUpdate
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    @NotNull
    var name: String = ""
) {
}