package com.example.tdd.service

import com.example.tdd.domain.entity.Product
import com.example.tdd.domain.entity.User
import com.example.tdd.domain.repository.OrderRepository
import com.example.tdd.domain.repository.ProductRepository
import com.example.tdd.domain.repository.UserRepository
import com.example.tdd.dto.OrderItemRequest
import com.example.tdd.dto.OrderRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.util.Optional
import kotlin.test.assertEquals

class OrderServiceTest {

    private lateinit var orderRepository: OrderRepository
    private lateinit var userRepository: UserRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var userService: UserService
    private lateinit var dataAnalyticsService: DataAnalyticsService
    private lateinit var orderService: OrderService

    @BeforeEach
    fun setUp() {
        orderRepository = mock()
        userRepository = mock()
        productRepository = mock()
        userService = mock()
        dataAnalyticsService = mock()

        orderService = OrderService(
            orderRepository,
            userRepository,
            productRepository,
            userService,
            dataAnalyticsService
        )
    }

    @Test
    fun `주문 생성 성공 테스트`() {
        // given
        val userId = 1L
        val user = User(id = userId, username = "testUser", balance = BigDecimal.valueOf(10000))

        val product1 = spy(Product(id = 1L, name = "상품1", price = BigDecimal.valueOf(1000), stockQuantity = 10))
        val product2 = spy(Product(id = 2L, name = "상품2", price = BigDecimal.valueOf(2000), stockQuantity = 5))

        val orderRequest = OrderRequest(
            items = listOf(
                OrderItemRequest(productId = 1L, quantity = 2),
                OrderItemRequest(productId = 2L, quantity = 1)
            )
        )

        // 예상 총 금액: 1000 * 2 + 2000 * 1 = 4000
        val expectedTotalAmount = BigDecimal.valueOf(4000)

        whenever(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user))
        whenever(productRepository.findByIdWithPessimisticLock(1L)).thenReturn(Optional.of(product1))
        whenever(productRepository.findByIdWithPessimisticLock(2L)).thenReturn(Optional.of(product2))

        val orderCaptor = ArgumentCaptor.forClass(com.example.tdd.domain.entity.Order::class.java)
        whenever(orderRepository.save(capture(orderCaptor))).thenAnswer { it.arguments[0] }

        // when
        val result = orderService.createOrder(userId, orderRequest)

        // then
        verify(product1).decreaseStock(2)
        verify(product2).decreaseStock(1)
        verify(userService).pay(eq(userId), eq(expectedTotalAmount))
        verify(orderRepository).save(any())
        verify(dataAnalyticsService).sendOrderData(any())

        val savedOrder = orderCaptor.value
        assertEquals(user, savedOrder.user)
        assertEquals(expectedTotalAmount, savedOrder.totalAmount)
        assertEquals(2, savedOrder.orderItems.size)
    }

    @Test
    fun `주문 생성 - 재고 부족 시 예외 발생`() {
        // given
        val userId = 1L
        val user = User(id = userId, username = "testUser", balance = BigDecimal.valueOf(10000))

        val product = Product(id = 1L, name = "상품1", price = BigDecimal.valueOf(1000), stockQuantity = 5)

        val orderRequest = OrderRequest(
            items = listOf(
                OrderItemRequest(productId = 1L, quantity = 10) // 재고보다 많은 수량
            )
        )

        whenever(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user))
        whenever(productRepository.findByIdWithPessimisticLock(1L)).thenReturn(Optional.of(product))

        // when & then
        assertThrows<IllegalArgumentException> {
            orderService.createOrder(userId, orderRequest)
        }
    }

    @Test
    fun `주문 생성 - 잔액 부족 시 예외 발생`() {
        // given
        val userId = 1L
        val user = User(id = userId, username = "testUser", balance = BigDecimal.valueOf(500))

        val product = Product(id = 1L, name = "상품1", price = BigDecimal.valueOf(1000), stockQuantity = 10)

        val orderRequest = OrderRequest(
            items = listOf(
                OrderItemRequest(productId = 1L, quantity = 1)
            )
        )

        whenever(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user))
        whenever(productRepository.findByIdWithPessimisticLock(1L)).thenReturn(Optional.of(product))

        doThrow(IllegalArgumentException("잔액이 부족합니다."))
            .`when`(userService).pay(eq(userId), any())

        // when & then
        assertThrows<IllegalArgumentException> {
            orderService.createOrder(userId, orderRequest)
        }
    }

    @Test
    fun `주문 생성 - 빈 주문 항목 시 예외 발생`() {
        // given
        val userId = 1L
        val user = User(id = userId, username = "testUser", balance = BigDecimal.valueOf(10000))

        val orderRequest = OrderRequest(items = emptyList())

        whenever(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user))

        // when & then
        assertThrows<IllegalArgumentException> {
            orderService.createOrder(userId, orderRequest)
        }
    }
}
