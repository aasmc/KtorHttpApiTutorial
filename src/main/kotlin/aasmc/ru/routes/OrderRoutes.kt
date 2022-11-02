package aasmc.ru.routes

import aasmc.ru.database.dao.DAOFacade
import aasmc.ru.models.Order
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postOrderRoute(dao: DAOFacade) {
    post("/order") {
        val order = call.receive<Order>()
        dao.addNewOrder(order)
        call.respondText("Order stored correctly", status = HttpStatusCode.Created)
    }
}

fun Route.listOrderRoute(dao: DAOFacade) {
    get("/order") {
        val orders = dao.allOrders()
        if (orders.isNotEmpty()) {
            call.respond(orders)
        }
    }
}

fun Route.getOrderRoute(dao: DAOFacade) {
    get("/order/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText("Bad request", status = HttpStatusCode.BadRequest)
        val order = dao.order(id) ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        call.respond(order)
    }
}

fun Route.totalizeOrderRoute(dao: DAOFacade) {
    get("/order/{id?}/total") {
        val id = call.parameters["id"]
            ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)

        val total = dao.totalAmountForOrder(id)
        if (total.compareTo(-1.0) == 0) {
            return@get call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
        call.respond(total)
    }
}














