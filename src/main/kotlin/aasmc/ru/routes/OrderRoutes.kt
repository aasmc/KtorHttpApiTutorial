package aasmc.ru.routes

import aasmc.ru.domain.model.Order
import aasmc.ru.domain.model.Result
import aasmc.ru.domain.repositories.OrdersRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postOrderRoute(orderRepository: OrdersRepository) {
    post("/order") {
        val order = call.receive<Order>()
        when (orderRepository.addNewOrder(order)) {
            is Result.Failure -> {
                call.respond(HttpStatusCode.InternalServerError, "Something went wrong when saving order :$order")
            }
            is Result.Success -> {
                call.respondText("Order stored correctly", status = HttpStatusCode.Created)
            }
        }
    }
}

fun Route.listOrderRoute(orderRepository: OrdersRepository) {
    get("/order") {
        when (val ordersResult = orderRepository.allOrders()) {
            is Result.Failure -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Something went wrong when retrieving all orders from DB"
                )
            }
            is Result.Success -> {
                val orders = ordersResult.data
                if (orders.isNotEmpty()) {
                    call.respond(orders)
                } else {
                    call.respondText("Sorry, there are currently no orders in the database.")
                }
            }
        }

    }
}

fun Route.getOrderRoute(orderRepository: OrdersRepository) {
    get("/order/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText("Bad request", status = HttpStatusCode.BadRequest)
        when(val orderResult = orderRepository.order(id)) {
            is Result.Failure -> {
                call.respond(HttpStatusCode.InternalServerError, "Something went wrong when retrieving data about order with id: $id")
            }
            is Result.Success -> {
                val order = orderResult.data ?: return@get call.respondText(
                    "Not Found",
                    status = HttpStatusCode.NotFound
                )
                call.respond(order)
            }
        }
    }
}

fun Route.totalizeOrderRoute(orderRepository: OrdersRepository) {
    get("/order/{id?}/total") {
        val id = call.parameters["id"]
            ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)

        when(val totalResult = orderRepository.totalAmountForOrder(id)) {
            is Result.Failure -> {
                call.respond(HttpStatusCode.InternalServerError, "Something went wrong when retrieving data about order with id: $id")
            }
            is Result.Success -> {
                val total = totalResult.data ?: return@get call.respondText(
                    "Not Found",
                    status = HttpStatusCode.NotFound
                )
                call.respond(total)
            }
        }
    }
}














