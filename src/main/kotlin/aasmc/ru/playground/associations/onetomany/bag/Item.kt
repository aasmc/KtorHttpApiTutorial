package aasmc.ru.playground.associations.onetomany.bag

import jakarta.persistence.*

@Entity
class Item constructor() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    var name: String = ""

    /**
     * This mapping is the most efficient of the OneToMany mappings,
     * because Hibernate doesn't have to check for duplicates and
     * maintain indices of the Bid elements.
     *
     * BUT. We can't eager fetch wto collections of bag type simultaneously,
     * because of the Cartesian product problem.
     *
     * Hibernate doesn't execute any SELECT statements until we iterate
     * over the collection. E.g. when we add an element to the bids, no
     * SELECT happens. While with Set and List, Hibernate executed
     * SELECT statement when you added another element to the collection.
     */
    @OneToMany(mappedBy = "item")
    var bids: MutableCollection<Bid> = ArrayList()

    constructor(name: String) : this() {
        this.name = name
    }
}