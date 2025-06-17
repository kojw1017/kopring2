package com.example.tdd.service

import com.example.tdd.domain.entity.Product
import com.example.tdd.domain.repository.ProductRepository
import com.example.tdd.dto.ProductCreateRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.util.Optional
import kotlin.test.assertEquals

class ProductServiceTest {

    private lateinit var productRepository: ProductRepository
    private lateinit var productService: ProductService

    @BeforeEach
    fun setUp() {
        productRepository = mock()
        productService = ProductService(productRepository)
    }

    @Test
    fun `전체 상품 조회 테스트`() {
        // given
        val product1 = Product(id = 1L, name = "상품1", price = BigDecimal.valueOf(1000), stockQuantity = 10)
        val product2 = Product(id = 2L, name = "상품2", price = BigDecimal.valueOf(2000), stockQuantity = 20)
        val products = listOf(product1, product2)

        whenever(productRepository.findAll()).thenReturn(products)

        // when
        val result = productService.getAllProducts()

        // then
        assertEquals(2, result.size)
        assertEquals("상품1", result[0].name)
        assertEquals(BigDecimal.valueOf(1000), result[0].price)
        assertEquals(10, result[0].stockQuantity)
    }

    @Test
    fun `ID로 상품 조회 테스트`() {
        // given
        val productId = 1L
        val product = Product(id = productId, name = "테스트 상품", price = BigDecimal.valueOf(1500), stockQuantity = 15)

        whenever(productRepository.findById(productId)).thenReturn(Optional.of(product))

        // when
        val result = productService.getProductById(productId)

        // then
        assertEquals(productId, result.id)
        assertEquals("테스트 상품", result.name)
        assertEquals(BigDecimal.valueOf(1500), result.price)
        assertEquals(15, result.stockQuantity)
    }

    @Test
    fun `ID로 상품 조회 - 상품이 존재하지 않을 경우 예외 발생`() {
        // given
        val productId = 999L

        whenever(productRepository.findById(productId)).thenReturn(Optional.empty())

        // when & then
        assertThrows<IllegalArgumentException> {
            productService.getProductById(productId)
        }
    }

    @Test
    fun `상품 생성 테스트`() {
        // given
        val request = ProductCreateRequest(
            name = "새 상품",
            price = BigDecimal.valueOf(2500),
            stockQuantity = 30
        )

        val savedProduct = Product(
            id = 1L,
            name = request.name,
            price = request.price,
            stockQuantity = request.stockQuantity
        )

        whenever(productRepository.save(any())).thenReturn(savedProduct)

        // when
        val result = productService.createProduct(request)

        // then
        assertEquals(1L, result.id)
        assertEquals("새 상품", result.name)
        assertEquals(BigDecimal.valueOf(2500), result.price)
        assertEquals(30, result.stockQuantity)
    }

    @Test
    fun `인기 상품 조회 테스트`() {
        // given
        val topProducts = listOf(
            Product(id = 1L, name = "인기 상품1", price = BigDecimal.valueOf(1000), stockQuantity = 5),
            Product(id = 2L, name = "인기 상품2", price = BigDecimal.valueOf(2000), stockQuantity = 10),
            Product(id = 3L, name = "인기 상품3", price = BigDecimal.valueOf(3000), stockQuantity = 15),
            Product(id = 4L, name = "인기 상품4", price = BigDecimal.valueOf(4000), stockQuantity = 20),
            Product(id = 5L, name = "인기 상품5", price = BigDecimal.valueOf(5000), stockQuantity = 25)
        )

        whenever(productRepository.findTop5BestSellingProducts()).thenReturn(topProducts)

        // when
        val result = productService.getTopSellingProducts()

        // then
        assertEquals(5, result.size)
        assertEquals("인기 상품1", result[0].name)
        assertEquals("인기 상품5", result[4].name)
    }
}
