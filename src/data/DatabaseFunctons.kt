package com.example.data

import com.example.data.collections.*
import com.example.security.checkHashForPassword
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

val client = KMongo.createClient().coroutine
val database = client.getDatabase("Food2youDB")

val users = database.getCollection<User>()
val restaurants = database.getCollection<Restaurant>()
val restaurantAccounts = database.getCollection<RestaurantAccount>()
val foods = database.getCollection<Food>()
val orders = database.getCollection<Order>()

suspend fun registerUser(user: User): Boolean {
    return users.insertOne(user).wasAcknowledged()
}

suspend fun registerRestaurant(restaurantAccount: RestaurantAccount): Boolean {
    return restaurantAccounts.insertOne(restaurantAccount).wasAcknowledged()
}

suspend fun checkIfPasswordIsCorrect(email: String, givenPassword: String): Boolean {
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return checkHashForPassword(givenPassword, actualPassword)
}

suspend fun checkIfPasswordIsCorrectRestaurants(email: String, givenPassword: String): Boolean {
    val actualPassword = restaurantAccounts.findOne(RestaurantAccount::email eq email)?.password ?: return false
    return checkHashForPassword(givenPassword, actualPassword)
}

suspend fun checkIfUserExists(email: String): Boolean {
    return users.findOne(User::email eq email) != null
}

suspend fun checkIfRestaurantAccountExists(username: String): Boolean {
    return restaurantAccounts.findOne(User::email eq username) != null
}

suspend fun insertRestaurant(restaurant: Restaurant): Boolean {
    val restaurantExists = restaurants.findOneById(restaurant.id) != null
    if(!restaurantExists){
       return restaurants.insertOne(restaurant).wasAcknowledged()
    }
    return restaurants.updateOneById(restaurant.id, restaurant).wasAcknowledged()
}

suspend fun deleteRestaurant(restaurantId: String): Boolean {
    return restaurants.deleteOne(Restaurant::id eq restaurantId).wasAcknowledged()
}

suspend fun deleteFood(foodId: String): Boolean {
    return foods.deleteOne(Food::id eq foodId).wasAcknowledged()
}

suspend fun addReviewToRestaurant(id: String, preview: String): Boolean {
    val previews = restaurants.findOneById(id)?.previews ?: return false
    return restaurants.updateOneById(id, setValue(Restaurant::previews, previews + preview)).wasAcknowledged()
}


suspend fun addFoodToRestaurant(food: Food): Boolean {
    val foodExists = foods.findOneById(food.id) != null
    if(!foodExists) {
        return foods.insertOne(food).wasAcknowledged()
    }
    return foods.updateOneById(food.id, food).wasAcknowledged()
}

suspend fun getAllFoodForARestaurant(restaurantName: String): List<Food> {
    return foods.find(Food::restaurantName eq restaurantName).toList()
}

suspend fun getRestaurantOfOwner(owner: String): Restaurant? {
    return restaurants.findOne(Restaurant::owner eq owner)
}

suspend fun getFavouriteRestaurants(email: String): List<Restaurant> {
    return restaurants.find(Restaurant::users contains email).toList()
}

suspend fun getAllRestaurants(): List<Restaurant> {
    return restaurants.find().toList()
}

suspend fun getRestaurantsByType(type: String): List<Restaurant> {
    return restaurants.find(Restaurant::type eq type).toList()
}

suspend fun likeRestaurant(restaurantId: String, user: String): Boolean {
    val restaurant = restaurants.findOneById(restaurantId) ?: return false
    return restaurants.updateOneById(restaurantId, setValue(Restaurant::users, restaurant.users + user)).wasAcknowledged()
}

suspend fun dislikeRestaurant(restaurantId: String, user: String): Boolean {
    val restaurant = restaurants.findOneById(restaurantId) ?: return false
    return restaurants.updateOneById(restaurantId, setValue(Restaurant::users, restaurant.users - user)).wasAcknowledged()
}

suspend fun insertOrder(order: Order): Boolean {
    return orders.insertOne(order).wasAcknowledged()
}

suspend fun getAllOrdersForARestaurant(restaurant: String): List<Order> {
    return orders.find(Order::restaurant eq restaurant).toList()
}

suspend fun registerUserToken(userEmail: String, token: String): Boolean {
    val user = users.findOne(User::email eq userEmail) ?: return false
    return users.updateOneById(user.id, setValue(User::token, token)).wasAcknowledged()
}

suspend fun registerOwnerToken(ownerEmail: String, token: String): Boolean {
    val owner = restaurantAccounts.findOne(RestaurantAccount::email eq ownerEmail) ?: return false
    return restaurantAccounts.updateOneById(owner.id, setValue(RestaurantAccount::token, token)).wasAcknowledged()
}

suspend fun changeRestaurantToken(ownerEmail: String, token: String): Boolean {
    val restaurant = restaurants.findOne(Restaurant::owner eq ownerEmail) ?: return false
    return restaurants.updateOneById(restaurant.id, setValue(Restaurant::token, token)).wasAcknowledged()
}

suspend fun updateOrderStatus(orderId: String, newStatus: String): Boolean {
    return orders.updateOneById(orderId, setValue(Order::status, newStatus)).wasAcknowledged()
}

suspend fun getAllWaitingOrdersForUser(email: String): List<Order> {
    return orders.find(Order::email eq email).toList()
}

suspend fun changeOrderRecipientToken(email: String, token: String): Boolean {
    return orders.updateMany(Order::email eq email, setValue(Order::recipient, token)).wasAcknowledged()
}





