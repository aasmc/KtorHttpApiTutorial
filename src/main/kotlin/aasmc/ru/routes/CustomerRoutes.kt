package aasmc.ru.routes

import aasmc.ru.domain.model.Customer
import aasmc.ru.domain.model.Result
import aasmc.ru.domain.model.exceptions.ItemNotFoundException
import aasmc.ru.domain.repositories.CustomersRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.customerRouting(customerRepository: CustomersRepository) {
    route("/customer") {
        get {
            when (val customersResult = customerRepository.allCustomers()) {
                is Result.Failure -> {

                }
                is Result.Success -> {
                    val customers = customersResult.data
                    if (customers.isNotEmpty()) {
                        call.respond(customers)
                    } else {
                        call.respondText("No customers found", status = HttpStatusCode.OK)
                    }
                }
            }
        }

        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )

            when (val customerResult = customerRepository.customer(id)) {
                is Result.Failure -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        "Something went wrong when trying to retrieve customer with id: $id"
                    )
                }
                is Result.Success -> {
                    val customer = customerResult.data ?: return@get call.respondText(
                        "No customer with id $id",
                        status = HttpStatusCode.NotFound
                    )
                    call.respond(customer)
                }
            }
        }

        post {
            val customer = call.receive<Customer>()
            when (customerRepository.addNewCustomer(customer)) {
                is Result.Failure -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        "Something went wrong when trying to save customer $customer"
                    )
                }
                is Result.Success -> {
                    call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
                }
            }
        }

        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            when (val deletedResult = customerRepository.deleteCustomer(id)) {
                is Result.Failure -> {
                    when(deletedResult.cause) {
                        is ItemNotFoundException -> call.respond(HttpStatusCode.NotFound, "Customer with id: $id not found")
                        else -> call.respond(HttpStatusCode.InternalServerError, "Something went wrong when deleting customer with id: $id")
                    }
                }
                is Result.Success -> {
                    call.respondText("Customer removed successfully", status = HttpStatusCode.Accepted)
                }
            }
        }
    }
}