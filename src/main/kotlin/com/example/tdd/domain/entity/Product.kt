package com.example.tdd.domain.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, precision = 19, scale = 2)
    val price: BigDecimal,

    @Column(nullable = false)
    private var stockQuantity: Int
) {
    fun getStockQuantity(): Int = stockQuantity

    @Version
    private var version: Long = 0

    @Synchronized
    fun decreaseStock(quantity: Int) {
        require(quantity > 0) { "수량은 0보다 커야 합니다." }
        require(stockQuantity >= quantity) { "재고가 부족합니다." }

        stockQuantity -= quantity
    }

    @Synchronized
    fun increaseStock(quantity: Int) {
        require(quantity > 0) { "수량은 0보다 커야 합니다." }

        stockQuantity += quantity
    }
}
