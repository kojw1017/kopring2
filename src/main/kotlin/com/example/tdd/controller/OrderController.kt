package com.example.tdd.controller

import com.example.tdd.dto.OrderRequest
import com.example.tdd.dto.OrderResponse
import com.example.tdd.service.OrderService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderService: OrderService) {

    @PostMapping("/{userId}")
    fun createOrder(
        @PathVariable userId: Long,
        @Valid @RequestBody request: OrderRequest
    ): ResponseEntity<OrderResponse> {
        val order = orderService.createOrder(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(order)
    }
}
