package com.example.tdd.dto

import java.math.BigDecimal

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: BigDecimal,
    val stockQuantity: Int
)

data class ProductCreateRequest(
    val name: String,
    val price: BigDecimal,
    val stockQuantity: Int
)
