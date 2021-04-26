package com.example

import com.example.data.checkIfPasswordIsCorrect
import com.example.data.checkIfPasswordIsCorrectRestaurants
import com.example.routes.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.routing.Routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Authentication){
        authenticate()
        authenticateOwners()
    }
    install(Routing) {
        registerRoute()
        loginRoute()
        restaurantRoute()
        foodRoute()
        orderRoute()
        tracksRoute()
    }

}



private fun Authentication.Configuration.authenticate() {
    basic(name = "users") {
        realm = "MyServer"
        validate { credentials ->
            if(checkIfPasswordIsCorrect(credentials.name, credentials.password)) {
                UserIdPrincipal(credentials.name)
            }
            else {
                null
            }
        }
    }
}

private fun Authentication.Configuration.authenticateOwners() {
    basic(name = "owners") {
        realm = "My_Server"
        validate { credentials ->
            if(checkIfPasswordIsCorrectRestaurants(credentials.name, credentials.password)) {
                UserIdPrincipal(credentials.name)
            }
            else {
                null
            }
        }
    }
}
