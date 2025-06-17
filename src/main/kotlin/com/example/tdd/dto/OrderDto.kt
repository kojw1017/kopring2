package com.example.tdd.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
)

data class OrderRequest(
    val items: List<OrderItemRequest>
)

data class OrderItemResponse(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: BigDecimal
)

data class OrderResponse(
    val orderId: Long,
    val userId: Long,
    val totalAmount: BigDecimal,
    val orderDate: LocalDateTime,
    val items: List<OrderItemResponse>
)
