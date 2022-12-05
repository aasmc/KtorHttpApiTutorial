package aasmc.ru.playground.filtering.cascade

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "USERS")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var username: String = ""
) {
    /**
     * When a managed User is refreshed, Hibernate cascades the operation to the
     * managed BillingDetails and refreshes each with an SQL SELECT. If none of these
     * instances remain in the database, Hibernate throws EntityNotFoundException.
     * Then Hibernate refreshes the User instance and eagerly loads the entire
     * billingDetails collection to discover any new BillingDetails.
     */
    @OneToMany(cascade = [CascadeType.PERSIST, CascadeType.REFRESH])
    @JoinColumn(name = "USER_ID", nullable = false)
    var billingDetails: MutableSet<BillingDetails> = hashSetOf()
}