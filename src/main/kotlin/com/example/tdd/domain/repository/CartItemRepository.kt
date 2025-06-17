package com.example.tdd.domain.repository

import com.example.tdd.domain.entity.CartItem
import com.example.tdd.domain.entity.Product
import com.example.tdd.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CartItemRepository : JpaRepository<CartItem, Long> {
    fun findByUserAndProduct(user: User, product: Product): Optional<CartItem>

    fun findByUser(user: User): List<CartItem>

    fun deleteByUserAndProduct(user: User, product: Product)
}
