package aasmc.ru.routes

import aasmc.ru.domain.model.Customer
import aasmc.ru.domain.repositories.CustomersRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.customerRouting(customerRepository: CustomersRepository) {
    route("/customer") {
        get {
            val customers = customerRepository.allCustomers()
            if (customers.isNotEmpty()) {
                call.respond(customers)
            } else {
                call.respondText("No customers found", status = HttpStatusCode.OK)
            }
        }

        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )

            val customer = customerRepository.customer(id) ?: return@get call.respondText(
                "No customer with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(customer)
        }

        post {
            val customer = call.receive<Customer>()
            customerRepository.addNewCustomer(customer) ?: kotlin.run {
                call.respondText("Customer with the same id is already present in the DB", status = HttpStatusCode.Conflict)
                return@post
            }
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }

        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val deleted = customerRepository.deleteCustomer(id)
            if (deleted) {
                call.respondText("Customer removed successfully", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}