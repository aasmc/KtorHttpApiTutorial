package aasmc.ru.routes

import aasmc.ru.models.Order
import aasmc.ru.models.orderStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postOrderRoute() {
    post {
        val order = call.receive<Order>()
        orderStorage.add(order)
        call.respondText("Order stored correctly", status = HttpStatusCode.Created)
    }
}

fun Route.listOrderRoute() {
    get("/order") {
        if (orderStorage.isNotEmpty()) {
            call.respond(orderStorage)
        }
    }
}

fun Route.getOrderRoute() {
    get("/order/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText("Bad request", status = HttpStatusCode.BadRequest)
        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        call.respond(order)
    }
}

fun Route.totalizeOrderRoute() {
    get("/order/{id?}/total") {
        val id = call.parameters["id"]
            ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)

        val order = orderStorage.find { it.number == id }
            ?: return@get call.respondText("Not Found", status = HttpStatusCode.NotFound)
        val total = order.contents.sumOf { it.price * it.amount }
        call.respond(total)
    }
}














