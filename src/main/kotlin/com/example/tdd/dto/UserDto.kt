package com.example.tdd.dto

import java.math.BigDecimal

data class UserChargeRequest(
    val amount: BigDecimal
)

data class UserBalanceResponse(
    val userId: Long,
    val balance: BigDecimal
)
