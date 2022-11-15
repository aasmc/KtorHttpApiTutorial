# Playground

This package contains some test entity classes and data access methods. It has not been extracted to a separate project just for simplicity.   

### Inheritance
There are four different strategies for representing an inheritance hierarchy:
- One table per concrete class and default runtime polymorphic behavior.
- One table per concrete class but discard polymorphism and inheritance relationships completely from the SQL schema. Use SQL UNION queries for runtime polymorphic behavior.
- One table per class hierarchy: enable polymorphism by denormalizing the SQL schema and relying on row-based discrimination to determine super / subtypes.
- One table per subclass: represent *is a* (inheritance) relationships as *has a* (foreign key) relationships, and use SQL JOIN operations. 