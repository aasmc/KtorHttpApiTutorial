package aasmc.ru.playground.fetching.batch

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "USERS")
/**
 * This setting tells Hibernate that it may load up to 10 User proxies if
 * one has to be loaded, al with the same SELECT statement. This is also
 * called - blind-guess optimization.
 */
@org.hibernate.annotations.BatchSize(size = 10)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var username: String = ""
) {
}