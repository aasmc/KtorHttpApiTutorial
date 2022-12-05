package aasmc.ru.playground.filtering.callback

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PostPersist
import jakarta.validation.constraints.NotNull

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var username: String = ""
) {

    @PostPersist
    fun notifyAdmin() {
        val currentUser = CurrentUser.INSTANCE.get()
        val mail = Mail.INSTANCE
        mail.send(
            """
            Entity instance persisted by: ${currentUser.username}: $this   
        """.trimIndent()
        )
    }
}