package com.example.routes

import com.example.data.checkIfRestaurantExists
import com.example.data.collections.Restaurant
import com.example.data.deleteRestaurant
import com.example.data.insertRestaurant
import com.example.data.requests.DeleteRestaurantRequest
import io.ktor.application.call
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.ContentTransformationException
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.restaurantRoute() {

    route("/addRestaurant") {
        post {
            withContext(Dispatchers.IO) {
                val restaurant = try {
                    call.receive<Restaurant>()
                } catch (e: ConcurrentModificationException) {
                    call.respond(BadRequest)
                    return@withContext
                }

                val restaurantExists = checkIfRestaurantExists(restaurant.name)
                if(!restaurantExists) {
                    if(insertRestaurant(restaurant)) {
                        call.respond(OK)
                    }
                    else {
                        call.respond(Conflict)
                    }
                }
            }
        }
    }
    route("/deleteRestaurant") {
        post {
            withContext(Dispatchers.IO) {
                val request = try {
                    call.receive<DeleteRestaurantRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@withContext
                }
                if(deleteRestaurant(request.id)) {
                    call.respond(OK)
                }
                else {
                    call.respond(Conflict)
                }
            }
        }
    }
}