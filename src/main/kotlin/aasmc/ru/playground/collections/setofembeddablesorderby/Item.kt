package aasmc.ru.playground.collections.setofembeddablesorderby

import jakarta.persistence.*

@Entity
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @ElementCollection
    @CollectionTable(name = "IMAGE")
    @OrderBy("filename, width DESC")
    var images: MutableSet<Image> = LinkedHashSet()
}