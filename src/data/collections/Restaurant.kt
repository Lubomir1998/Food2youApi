package com.example.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Restaurant(
    val name: String,
    val type: String,
    val kitchen: String,
    val deliveryPrice: Float,
    val deliveryTimeMinutes: Int,
    val minimalPrice: Int,
    val imgUrl: String,
    val previews: List<String>,
    val users: List<String>,
    val owners: List<String>,
    @BsonId
    val id: String = ObjectId().toString()
)