package aasmc.ru.playground.filtering.envers

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.envers.Audited

@Entity
@Table(name = "USERS")
@Audited
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    @NotNull
    var username: String = ""
) {
}