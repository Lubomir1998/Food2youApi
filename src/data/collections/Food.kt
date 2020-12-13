package com.example.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Food(
    val name: String,
    val type: String,
    val weight: Int,
    val imgUrl: String,
    val price: Float,
    val restaurantName: String,
    @BsonId
    val id: String = ObjectId().toString()
)