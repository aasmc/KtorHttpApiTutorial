package aasmc.ru.playground.associations.complexschemas.custom

import jakarta.persistence.*

@Entity
@Table(
    name = "USERS",
    uniqueConstraints = [
        UniqueConstraint(
            name = "UNQ_USERNAME_EMAIL",
            columnNames = ["USERNAME", "EMAIL"]
        )
    ],
    indexes = [
        Index(
            name = "IDX_USERNAME",
            columnList = "USERNAME"
        ),
        Index(
            name = "IDX_USERNAME_EMAIL",
            columnList = "USERNAME, EMAIL"
        )
    ]
)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,

    @Column(
        nullable = false,
        unique = true,
    )
    @org.hibernate.annotations.Check(
        constraints = "position('@', value) > 1"
    )
    var email: String = "",

    @Column(
        columnDefinition =
                "varchar(15) not null unique" +
                " check (not substring(lower(USERNAME), 0, 5) = 'admin')"
    )
    var username: String = ""
) {
}