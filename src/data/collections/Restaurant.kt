package com.example.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Restaurant(
    val name: String,
    val type: String,
    val kitchen: String,
    val deliveryPrice: Float,
    val deliveryTimeMinutes: Int,
    val minimalPrice: Float,
    val imgUrl: String,
    val previews: List<String>,
    val users: List<String>,
    val token: String,
    val owner: String,
    @BsonId
    val id: String = ObjectId().toString()
)