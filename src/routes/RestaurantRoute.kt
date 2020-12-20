package com.example.routes

import com.example.data.*
import com.example.data.collections.Restaurant
import com.example.data.requests.AddPreviewRequest
import com.example.data.requests.DeleteRestaurantRequest
import io.ktor.application.call
import io.ktor.auth.*
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
        authenticate("owners") {
            post {
                withContext(Dispatchers.IO) {
                    val restaurant = try {
                        call.receive<Restaurant>()
                    } catch (e: ConcurrentModificationException) {
                        call.respond(BadRequest)
                        return@withContext
                    }
                    val owner = call.principal<UserIdPrincipal>()!!.name

                 //   val restaurantExists = checkIfRestaurantExists(restaurant.name)

                   // val ownerAlreadyOwnesRestaurant = checkIfOwnerAlreadyHasRestaurant(owner)

                 //   if (!restaurantExists) {
                        if (insertRestaurant(restaurant)) {
                            call.respond(OK)
                        } else {
                            call.respond(Conflict)
                        }
                //    }
                }
            }
        }
    }

    route("/deleteRestaurant") {
        authenticate("owners") {
            post {
                withContext(Dispatchers.IO) {
                    val request = try {
                        call.receive<DeleteRestaurantRequest>()
                    } catch (e: ContentTransformationException) {
                        call.respond(BadRequest)
                        return@withContext
                    }
                    if (deleteRestaurant(request.id)) {
                        call.respond(OK)
                    } else {
                        call.respond(Conflict)
                    }
                }
            }
        }
    }

    route("/addPreview"){
        authenticate("users") {
            post {
                withContext(Dispatchers.IO) {
                    val request = try {
                        call.receive<AddPreviewRequest>()
                    } catch (e: ContentTransformationException) {
                        call.respond(BadRequest)
                        return@withContext
                    }

                    if (request.preview.isEmpty()) {
                        call.respond(Conflict, "Please, type something")
                        return@withContext
                    }

                    if (addReviewToRestaurant(request.id, request.preview)) {
                        call.respond(OK)
                    } else {
                        call.respond(Conflict)
                    }
                }
            }
        }
    }

}