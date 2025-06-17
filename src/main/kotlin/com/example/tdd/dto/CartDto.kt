package com.example.tdd.dto

data class CartItemRequest(
    val productId: Long,
    val quantity: Int
)

data class CartItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val price: java.math.BigDecimal,
    val quantity: Int
)
