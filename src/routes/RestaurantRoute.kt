package com.example.routes

import com.example.data.*
import com.example.data.collections.Restaurant
import com.example.data.requests.AddPreviewRequest
import com.example.data.requests.DeleteRestaurantRequest
import com.example.data.requests.LikeRestaurantRequest
import com.example.data.responses.SimpleResponse
import io.ktor.application.call
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.ContentTransformationException
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.restaurantRoute() {

    route("/getAllRestaurants") {
        get {
            withContext(Dispatchers.IO) {
                val list = getAllRestaurants()

                call.respond(OK, list)
            }
        }
    }

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
                     if (insertRestaurant(restaurant)) {
                         call.respond(OK)
                     } else {
                         call.respond(Conflict)
                     }
                }
            }
        }
    }

    route("/getRestaurantOfOwner") {
        authenticate("owners") {
            get {
                withContext(Dispatchers.IO) {

                    val owner = call.principal<UserIdPrincipal>()!!.name

                    val restaurant = getRestaurantOfOwner(owner)

                    call.respond(OK, restaurant!!)

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

    route("/likeRestaurant") {
        authenticate("users") {
            post {
                withContext(Dispatchers.IO) {
                    val request = try {
                        call.receive<LikeRestaurantRequest>()
                    } catch (e: ContentTransformationException) {
                        call.respond(BadRequest)
                        return@withContext
                    }

                    val user = call.principal<UserIdPrincipal>()!!.name
                    val restaurantId = request.restaurantId

                    if(likeRestaurant(restaurantId, user)) {
                        call.respond(OK, SimpleResponse(true, "You liked that restaurant"))
                    }
                    else {
                        call.respond(Conflict)
                    }

                }
            }
        }
    }

    route("/dislikeRestaurant") {
        authenticate("users") {
            post {
                withContext(Dispatchers.IO) {
                    val request = try {
                        call.receive<LikeRestaurantRequest>()
                    } catch (e: ContentTransformationException) {
                        call.respond(BadRequest)
                        return@withContext
                    }

                    val user = call.principal<UserIdPrincipal>()!!.name
                    val restaurantId = request.restaurantId

                    if(dislikeRestaurant(restaurantId, user)) {
                        call.respond(OK, SimpleResponse(true, "You disliked that restaurant"))
                    }
                    else {
                        call.respond(Conflict)
                    }

                }
            }
        }
    }

    route("/getFavouriteRestaurants") {
        authenticate("users") {
            get {
                withContext(Dispatchers.IO) {
                    val email = call.principal<UserIdPrincipal>()!!.name

                    val list = getFavouriteRestaurants(email)

                    call.respond(OK, list)

                }
            }
        }
    }

    route("/getRestaurantByType/{type}") {
        get {
            withContext(Dispatchers.IO) {
                val type = call.parameters["type"]

                val list = getRestaurantsByType(type!!)

                call.respond(OK, list)

            }
        }
    }

    route("/addPreview"){
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
                    call.respond(OK, SimpleResponse(true, "Review added"))
                } else {
                    call.respond(Conflict)
                }
            }
        }

    }

}