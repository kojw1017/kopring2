package com.example.tdd.controller

import com.example.tdd.dto.CartItemRequest
import com.example.tdd.dto.CartItemResponse
import com.example.tdd.service.CartService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cart")
class CartController(private val cartService: CartService) {

    @GetMapping("/{userId}")
    fun getCartItems(@PathVariable userId: Long): ResponseEntity<List<CartItemResponse>> {
        val cartItems = cartService.getCartItems(userId)
        return ResponseEntity.ok(cartItems)
    }

    @PostMapping("/{userId}")
    fun addToCart(
        @PathVariable userId: Long,
        @Valid @RequestBody request: CartItemRequest
    ): ResponseEntity<CartItemResponse> {
        val cartItem = cartService.addToCart(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem)
    }

    @PutMapping("/{userId}/products/{productId}")
    fun updateCartItemQuantity(
        @PathVariable userId: Long,
        @PathVariable productId: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<CartItemResponse> {
        val cartItem = cartService.updateCartItemQuantity(userId, productId, quantity)
        return ResponseEntity.ok(cartItem)
    }

    @DeleteMapping("/{userId}/products/{productId}")
    fun removeFromCart(
        @PathVariable userId: Long,
        @PathVariable productId: Long
    ): ResponseEntity<Unit> {
        cartService.removeFromCart(userId, productId)
        return ResponseEntity.noContent().build()
    }
}
