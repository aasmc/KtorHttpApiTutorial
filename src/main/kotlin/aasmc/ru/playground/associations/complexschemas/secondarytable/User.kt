package aasmc.ru.playground.associations.complexschemas.secondarytable

import jakarta.persistence.*

@Entity
@Table(name = "USERS")
@SecondaryTable(
    name = "BILLING_ADDRESS",
    pkJoinColumns = [PrimaryKeyJoinColumn(name = "USER_ID")]
)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    var username: String = "",
    @Embedded
    var homeAddress: Address = Address(),

    @Embedded
    @AttributeOverrides(
        *[
            AttributeOverride(
                name = "street",
                column = Column(table = "BILLING_ADDRESS", nullable = false)
            ),
            AttributeOverride(
                name = "zipcode",
                column = Column(table = "BILLING_ADDRESS", length = 5, nullable = false),
            ),
            AttributeOverride(
                name = "city",
                column = Column(table = "BILLING_ADDRESS", nullable = false)
            )
        ]
    )
    var billingAddress: Address = Address()
) {
}