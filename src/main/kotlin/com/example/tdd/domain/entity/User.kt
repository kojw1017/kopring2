package com.example.tdd.domain.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false, precision = 19, scale = 2)
    private var balance: BigDecimal = BigDecimal.ZERO
) {
    fun getBalance(): BigDecimal = balance

    @Synchronized
    fun charge(amount: BigDecimal): BigDecimal {
        require(amount > BigDecimal.ZERO) { "충전 금액은 0보다 커야 합니다." }

        balance = balance.add(amount)
        return balance
    }

    @Synchronized
    fun pay(amount: BigDecimal): BigDecimal {
        require(amount > BigDecimal.ZERO) { "결제 금액은 0보다 커야 합니다." }
        require(balance >= amount) { "잔액이 부족합니다." }

        balance = balance.subtract(amount)
        return balance
    }
}
