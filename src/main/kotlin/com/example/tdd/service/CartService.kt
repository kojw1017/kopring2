package com.example.tdd.service

import com.example.tdd.domain.entity.CartItem
import com.example.tdd.domain.repository.CartItemRepository
import com.example.tdd.domain.repository.ProductRepository
import com.example.tdd.domain.repository.UserRepository
import com.example.tdd.dto.CartItemRequest
import com.example.tdd.dto.CartItemResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartItemRepository: CartItemRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) {

    @Transactional
    fun addToCart(userId: Long, request: CartItemRequest): CartItemResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다: $userId") }

        val product = productRepository.findById(request.productId)
            .orElseThrow { IllegalArgumentException("상품을 찾을 수 없습니다: ${request.productId}") }

        if (request.quantity <= 0) {
            throw IllegalArgumentException("수량은 0보다 커야 합니다")
        }

        // 재고 확인
        if (product.getStockQuantity() < request.quantity) {
            throw IllegalArgumentException("상품의 재고가 부족합니다: ${product.name}")
        }

        // 이미 장바구니에 있는 상품인지 확인
        val cartItem = cartItemRepository.findByUserAndProduct(user, product)
            .map { existingItem ->
                existingItem.increaseQuantity(request.quantity)
                existingItem
            }
            .orElseGet {
                CartItem(
                    user = user,
                    product = product,
                    quantity = request.quantity
                )
            }

        val savedItem = cartItemRepository.save(cartItem)

        return CartItemResponse(
            id = savedItem.id ?: throw IllegalStateException("장바구니 아이템 ID가 없습니다"),
            productId = product.id ?: throw IllegalStateException("상품 ID가 없습니다"),
            productName = product.name,
            price = product.price,
            quantity = savedItem.quantity
        )
    }

    @Transactional
    fun removeFromCart(userId: Long, productId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다: $userId") }

        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("상품을 찾을 수 없습니다: $productId") }

        cartItemRepository.deleteByUserAndProduct(user, product)
    }

    @Transactional
    fun updateCartItemQuantity(userId: Long, productId: Long, quantity: Int): CartItemResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다: $userId") }

        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("상품을 찾을 수 없습니다: $productId") }

        if (quantity <= 0) {
            throw IllegalArgumentException("수량은 0보다 커야 합니다")
        }

        val cartItem = cartItemRepository.findByUserAndProduct(user, product)
            .orElseThrow { IllegalArgumentException("장바구니에 해당 상품이 없습니다: ${product.name}") }

        // 재고 확인
        if (product.getStockQuantity() < quantity) {
            throw IllegalArgumentException("상품의 재고가 부족합니다: ${product.name}")
        }

        cartItem.quantity = quantity
        val savedItem = cartItemRepository.save(cartItem)

        return CartItemResponse(
            id = savedItem.id ?: throw IllegalStateException("장바구니 아이템 ID가 없습니다"),
            productId = product.id ?: throw IllegalStateException("상품 ID가 없습니다"),
            productName = product.name,
            price = product.price,
            quantity = savedItem.quantity
        )
    }

    @Transactional(readOnly = true)
    fun getCartItems(userId: Long): List<CartItemResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다: $userId") }

        return cartItemRepository.findByUser(user).map { item ->
            CartItemResponse(
                id = item.id ?: throw IllegalStateException("장바구니 아이템 ID가 없습니다"),
                productId = item.product.id ?: throw IllegalStateException("상품 ID가 없습니다"),
                productName = item.product.name,
                price = item.product.price,
                quantity = item.quantity
            )
        }
    }
}
