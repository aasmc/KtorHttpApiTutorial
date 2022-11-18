package aasmc.ru.playground.associations.onetomany.embeddablejointable

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@Entity
class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @NotNull
    var createdOn: LocalDate = LocalDate.now()
}