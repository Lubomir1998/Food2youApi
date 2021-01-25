package com.example.routes

import com.example.data.collections.Order
import com.example.data.getAllOrdersForARestaurant
import com.example.data.insertOrder
import com.example.data.requests.UpdateOrderStatusRequest
import com.example.data.responses.SimpleResponse
import com.example.data.updateOrderStatus
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream


fun Route.orderRoute() {

    route("order") {
        post {
            withContext(Dispatchers.IO) {

                val order = try {
                    call.receive<Order>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@withContext
                }

                if (insertOrder(order)) {
                    call.respond(OK, SimpleResponse(true, "Order received successfully"))
                } else {
                    call.respond(Conflict)
                }


            }
        }
    }

    route("/allOrders") {
        authenticate("owners") {
            get {
                withContext(Dispatchers.IO) {

                    val restaurant = call.principal<UserIdPrincipal>()!!.name
                    val orders = getAllOrdersForARestaurant(restaurant)

                    call.respond(OK, orders)
                }
            }
        }
    }

    route("/updateOrderStatus") {
        authenticate("owners") {
            post {
                withContext(Dispatchers.IO) {
                    val request = try {
                        call.receive<UpdateOrderStatusRequest>()
                    } catch (e: ContentTransformationException) {
                        call.respond(BadRequest)
                        return@withContext
                    }

                    if(updateOrderStatus(request.orderId, request.newStatus)) {
                        call.respond(OK, SimpleResponse(true, "Status updated"))
                    }
                    else {
                        call.respond(OK, SimpleResponse(false, "Error occurred"))
                    }

                }
            }
        }
    }


}