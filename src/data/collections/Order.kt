package com.example.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Order(
        val restaurant: String,
        val address: String,
        val food : List<FoodItem>,
        val price: Float,
        val timestamp: Long,
        @BsonId
        val id: String = ObjectId().toString()
)
