package com.example.routes

import com.example.data.*
import com.example.data.collections.RestaurantAccount
import com.example.data.collections.User
import com.example.data.requests.RegisterUserRequest
import com.example.data.responses.UserToken
import com.example.data.responses.SimpleResponse
import com.example.security.getHashWithSalt
import io.ktor.application.call
import io.ktor.auth.*
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
                    call.receive<RegisterUserRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@withContext
                }
                val userExists = checkIfUserExists(request.email)
                if(!userExists) {
                    if (registerUser(User(request.email, getHashWithSalt(request.password), request.token))) {
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
                    call.receive<RegisterUserRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@withContext
                }
                val restaurantExists = checkIfRestaurantAccountExists(request.email)
                if(!restaurantExists) {
                    if (registerRestaurant(RestaurantAccount(request.email, getHashWithSalt(request.password), request.token))) {
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

    route("/registerUserToken/{user}") {
        authenticate("users") {
            post {
                withContext(Dispatchers.IO) {

                    val request = try {
                        call.receive<UserToken>()
                    } catch (e: ContentTransformationException) {
                        call.respond(BadRequest)
                        return@withContext
                    }

                    val token = request.token
                    val userEmail = call.parameters["user"]

                    userEmail?.let {
                        if (registerUserToken(it, token)) {
                            call.respond(OK, SimpleResponse(true, "Token registered"))
                        }
                        else {
                            call.respond(OK, SimpleResponse(false, "Error occurred"))
                        }
                    } ?: call.respond(OK, SimpleResponse(false, "Error occurred"))



                }
            }
        }
    }

    route("/registerOwnerToken/{owner}") {
        authenticate("owners") {
            post {
                withContext(Dispatchers.IO) {

                    val request = try {
                        call.receive<UserToken>()
                    } catch (e: ContentTransformationException) {
                        call.respond(BadRequest)
                        return@withContext
                    }

                    val token = request.token
                    val ownerEmail = call.parameters["owner"]

                    ownerEmail?.let {
                        if (registerOwnerToken(it, token)) {
                            call.respond(OK, SimpleResponse(true, "Token registered"))
                        }
                        else {
                            call.respond(OK, SimpleResponse(false, "Error occurred"))
                        }
                    } ?: call.respond(OK, SimpleResponse(false, "Error occurred"))



                }
            }
        }
    }


    route("/changeRestaurantToken/{owner}") {
        authenticate("owners") {
            post {
                withContext(Dispatchers.IO) {

                    val request = try {
                        call.receive<UserToken>()
                    } catch (e: ContentTransformationException) {
                        call.respond(BadRequest)
                        return@withContext
                    }

                    val token = request.token
                    val ownerEmail = call.parameters["owner"]

                    ownerEmail?.let {
                        if (changeRestaurantToken(it, token)) {
                            call.respond(OK, SimpleResponse(true, "Token updated successfully"))
                        }
                        else {
                            call.respond(OK, SimpleResponse(false, "Error occurred"))
                        }
                    } ?: call.respond(OK, SimpleResponse(false, "Error occurred"))



                }
            }
        }
    }


}