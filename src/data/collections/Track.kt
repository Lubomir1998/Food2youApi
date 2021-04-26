package com.example.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Track(
    val orderId: String,
    val coordinates: List<Coordinates>,
    @BsonId
    val id: String = ObjectId().toString()
)
