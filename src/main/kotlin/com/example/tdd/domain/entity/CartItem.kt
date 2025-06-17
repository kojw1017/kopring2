package com.example.tdd.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "cart_items")
class CartItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(nullable = false)
    var quantity: Int
) {
    fun increaseQuantity(amount: Int) {
        require(amount > 0) { "증가량은 0보다 커야 합니다." }
        quantity += amount
    }

    fun decreaseQuantity(amount: Int) {
        require(amount > 0) { "감소량은 0보다 커야 합니다." }
        require(quantity >= amount) { "장바구니 상품 수량이 부족합니다." }

        quantity -= amount
    }
}
