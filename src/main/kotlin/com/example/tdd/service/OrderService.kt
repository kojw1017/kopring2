package com.example.tdd.service

import com.example.tdd.domain.entity.Order
import com.example.tdd.domain.entity.OrderItem
import com.example.tdd.domain.repository.OrderRepository
import com.example.tdd.domain.repository.ProductRepository
import com.example.tdd.domain.repository.UserRepository
import com.example.tdd.dto.OrderItemResponse
import com.example.tdd.dto.OrderRequest
import com.example.tdd.dto.OrderResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val userService: UserService,
    private val dataAnalyticsService: DataAnalyticsService
) {

    @Transactional
    fun createOrder(userId: Long, request: OrderRequest): OrderResponse {
        val user = userRepository.findByIdWithPessimisticLock(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다: $userId") }

        if (request.items.isEmpty()) {
            throw IllegalArgumentException("주문 항목은 비어있을 수 없습니다")
        }

        val orderItems = request.items.map { itemRequest ->
            val product = productRepository.findByIdWithPessimisticLock(itemRequest.productId)
                .orElseThrow { IllegalArgumentException("상품을 찾을 수 없습니다: ${itemRequest.productId}") }

            if (product.getStockQuantity() < itemRequest.quantity) {
                throw IllegalArgumentException("상품의 재고가 부족합니다: ${product.name}")
            }

            product.decreaseStock(itemRequest.quantity)

            OrderItem(
                product = product,
                quantity = itemRequest.quantity,
                price = product.price
            )
        }

        val totalAmount = orderItems.fold(BigDecimal.ZERO) { acc, item ->
            acc.add(item.price.multiply(BigDecimal.valueOf(item.quantity.toLong())))
        }

        // 결제 처리
        userService.pay(userId, totalAmount)

        val order = Order(
            user = user,
            totalAmount = totalAmount,
            orderDate = LocalDateTime.now()
        )

        orderItems.forEach { order.addOrderItem(it) }

        val savedOrder = orderRepository.save(order)

        // 주문 데이터를 분석 플랫폼으로 비동기 전송
        dataAnalyticsService.sendOrderData(mapToOrderResponse(savedOrder))

        return mapToOrderResponse(savedOrder)
    }

    private fun mapToOrderResponse(order: Order): OrderResponse {
        return OrderResponse(
            orderId = order.id ?: throw IllegalStateException("주문 ID가 없습니다"),
            userId = order.user.id ?: throw IllegalStateException("사용자 ID가 없습니다"),
            totalAmount = order.totalAmount,
            orderDate = order.orderDate,
            items = order.orderItems.map { item ->
                OrderItemResponse(
                    productId = item.product.id ?: throw IllegalStateException("상품 ID가 없습니다"),
                    productName = item.product.name,
                    quantity = item.quantity,
                    price = item.price
                )
            }
        )
    }
}
