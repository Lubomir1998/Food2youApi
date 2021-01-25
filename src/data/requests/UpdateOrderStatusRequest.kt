package com.example.data.requests

data class UpdateOrderStatusRequest(
        val orderId: String,
        val newStatus: String
)
