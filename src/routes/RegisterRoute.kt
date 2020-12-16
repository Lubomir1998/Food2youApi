package com.example.routes

import com.example.data.checkIfRestaurantAccountExists
import com.example.data.checkIfUserExists
import com.example.data.collections.RestaurantAccount
import com.example.data.collections.User
import com.example.data.registerRestaurant
import com.example.data.registerUser
import com.example.data.requests.AccountRequest
import com.example.data.responses.SimpleResponse
import com.example.security.getHashWithSalt
import io.ktor.application.call
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.ContentTransformationException
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.registerRoute() {

    route("/registerUser") {
        post {
            withContext(Dispatchers.IO) {
                val request = try {
                    call.receive<AccountRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@withContext
                }
                val userExists = checkIfUserExists(request.email)
                if(!userExists) {
                    if (registerUser(User(request.email, getHashWithSalt(request.password)))) {
                        call.respond(OK, SimpleResponse(true, "Successfully created account"))
                    } else {
                        call.respond(OK, SimpleResponse(false, "An unknown error occurred"))
                    }
                }
                else {
                    call.respond(OK, SimpleResponse(false, "User already exists"))
                }
            }
        }
    }

    route("/registerRestaurant"){
        post {
            withContext(Dispatchers.IO) {
                val request = try {
                    call.receive<AccountRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@withContext
                }
                val restaurantExists = checkIfRestaurantAccountExists(request.email)
                if(!restaurantExists) {
                    if (registerRestaurant(RestaurantAccount(request.email, request.password))) {
                        call.respond(OK, SimpleResponse(true, "Successfully created account"))
                    } else {
                        call.respond(OK, SimpleResponse(false, "An unknown error occurred"))
                    }
                }
                else {
                    call.respond(OK, SimpleResponse(false, "Account already exists"))
                }
            }
        }
    }

}