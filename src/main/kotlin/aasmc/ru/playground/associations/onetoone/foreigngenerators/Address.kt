package aasmc.ru.playground.associations.onetoone.foreigngenerators

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
class Address protected constructor() {
    @Id
    @GeneratedValue(generator = "addressKeyGenerator")
    @org.hibernate.annotations.GenericGenerator(
        name = "addressKeyGenerator",
        // Hibernate only strategy of generating identifiers
        // when the Address gets persisted, this generator grabs
        // the ID of the associated User
        strategy = "foreign",
        parameters = [
            org.hibernate.annotations.Parameter(
                name = "property",
                value = "user"
            )
        ]
    )
    var id: Long = 0


    @NotNull
    var street: String = ""

    @NotNull
    var zipcode: String = ""

    @NotNull
    var city: String = ""

    @OneToOne(optional = false)
    @PrimaryKeyJoinColumn
    var user: User? = null

    constructor(user: User) : this() {
        this.user = user
    }

    constructor(user: User, street: String, zipcode: String, city: String) : this() {
        this.user = user
        this.street = street
        this.zipcode = zipcode
        this.city = city
    }
}




















