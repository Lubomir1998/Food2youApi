package com.example.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Order(
        val restaurant: String,
        val address: String,
        val recipient: String,
        val email: String,
        val phoneNumber: String,
        val food : List<FoodItem>,
        val price: Float,
        val timestamp: Long,
        val status: String,
        val resImgUrl: String,
        val restaurantName: String,
        var resId: String = "",
        @BsonId
        val id: String = ObjectId().toString()
)
