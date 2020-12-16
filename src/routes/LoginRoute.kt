package com.example.routes

import com.example.data.checkIfPasswordIsCorrect
import com.example.data.requests.AccountRequest
import com.example.data.responses.SimpleResponse
import io.ktor.application.call
import io.ktor.request.ContentTransformationException
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK

fun Route.loginRoute() {
    route("/login") {
        post {
            withContext(Dispatchers.IO) {
                val request = try {
                    call.receive<AccountRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@withContext
                }

                val isPasswordCorrect = checkIfPasswordIsCorrect(request.email, request.password)

                if(isPasswordCorrect) {
                    call.respond(OK, SimpleResponse(true, "Successfully logged in"))
                }
                else {
                    call.respond(OK, SimpleResponse(false, "Email or password incorrect"))
                }

            }
        }
    }
}