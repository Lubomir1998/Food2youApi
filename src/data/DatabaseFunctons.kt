package com.example.data

import com.example.data.collections.Restaurant
import com.example.data.collections.User
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

val client = KMongo.createClient().coroutine
val database = client.getDatabase("Food2youDB")
val users = database.getCollection<User>()
val restaurants = database.getCollection<Restaurant>()

suspend fun registerUser(user: User): Boolean {
    return users.insertOne(user).wasAcknowledged()
}

suspend fun isPasswordCorrect(email: String, givenPassword: String): Boolean {
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return actualPassword == givenPassword
}

suspend fun checkIfUserExists(email: String): Boolean {
    return users.findOne(User::email eq email) != null
}

suspend fun checkIfRestaurantExists(name: String): Boolean {
    return restaurants.findOne(Restaurant::name eq name) != null
}

suspend fun insertRestaurant(restaurant: Restaurant): Boolean {
    val restaurantExists = restaurants.findOne(Restaurant::name eq restaurant.name) != null
    if(!restaurantExists){
       return restaurants.insertOne(restaurant).wasAcknowledged()
    }
    return false
}

suspend fun deleteRestaurant(restaurantId: String): Boolean {
    return restaurants.deleteOne(Restaurant::id eq restaurantId).wasAcknowledged()
}









