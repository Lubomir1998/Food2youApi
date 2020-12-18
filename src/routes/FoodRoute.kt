package com.example.routes

import com.example.data.addFoodToRestaurant
import com.example.data.collections.Food
import com.example.data.getAllFoodForARestaurant
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.foodRoute() {

    route("/addFood") {
        authenticate("owners") {
            post {
                withContext(Dispatchers.IO) {
                    val response = try {
                        call.receive<Food>()
                    } catch (e: ContentTransformationException) {
                        call.respond(BadRequest)
                        return@withContext
                    }

                    if(addFoodToRestaurant(response)) {
                        call.respond(OK)
                    }
                    else {
                        call.respond(Conflict)
                    }

                }
            }
        }
    }

    route("/getFoodResOnly") {
        authenticate("owners") {
            get {
                withContext(Dispatchers.IO) {
                    val restaurantOwner = call.principal<UserIdPrincipal>()!!.name

                    val foodList = getAllFoodForARestaurant(restaurantOwner)

                    call.respond(OK, foodList)

                }
            }
        }
    }

    route("/getFood/{restaurant}") {
        authenticate("users") {
            get {
                withContext(Dispatchers.IO) {

                    val restaurant = call.parameters["restaurant"]

                    val foodList = getAllFoodForARestaurant(restaurant!!)

                    call.respond(OK, foodList)

                }
            }
        }
    }

}