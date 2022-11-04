package aasmc.ru.routes

import aasmc.ru.domain.model.Order
import aasmc.ru.domain.repositories.OrdersRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postOrderRoute(orderRepository: OrdersRepository) {
    post("/order") {
        val order = call.receive<Order>()
        orderRepository.addNewOrder(order)
        call.respondText("Order stored correctly", status = HttpStatusCode.Created)
    }
}

fun Route.listOrderRoute(orderRepository: OrdersRepository) {
    get("/order") {
        val orders = orderRepository.allOrders()
        if (orders.isNotEmpty()) {
            call.respond(orders)
        }
    }
}

fun Route.getOrderRoute(orderRepository: OrdersRepository) {
    get("/order/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText("Bad request", status = HttpStatusCode.BadRequest)
        val order = orderRepository.order(id) ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        call.respond(order)
    }
}

fun Route.totalizeOrderRoute(orderRepository: OrdersRepository) {
    get("/order/{id?}/total") {
        val id = call.parameters["id"]
            ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)

        val total = orderRepository.totalAmountForOrder(id)
        if (total.compareTo(-1.0) == 0) {
            return@get call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
        call.respond(total)
    }
}














