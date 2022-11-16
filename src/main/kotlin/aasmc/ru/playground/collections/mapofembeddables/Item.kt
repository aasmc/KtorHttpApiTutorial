package aasmc.ru.playground.collections.mapofembeddables

import jakarta.persistence.*

@Entity
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @ElementCollection
    @CollectionTable(name = "IMAGE")
    var images: MutableMap<FileName, Image> = hashMapOf()
}