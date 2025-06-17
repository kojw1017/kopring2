package com.example.tdd.domain.repository

import com.example.tdd.domain.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import jakarta.persistence.LockModeType
import java.util.Optional

interface ProductRepository : JpaRepository<Product, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    fun findByIdWithPessimisticLock(@Param("id") id: Long): Optional<Product>

    @Query("""
        SELECT p FROM Product p
        JOIN OrderItem oi ON oi.product.id = p.id
        JOIN Order o ON oi.order.id = o.id
        WHERE o.orderDate >= CURRENT_DATE - 3
        GROUP BY p.id
        ORDER BY SUM(oi.quantity) DESC
    """)
    fun findTop5BestSellingProducts(): List<Product>
}
