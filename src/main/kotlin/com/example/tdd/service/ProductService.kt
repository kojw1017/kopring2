package com.example.tdd.service

import com.example.tdd.domain.entity.Product
import com.example.tdd.domain.repository.ProductRepository
import com.example.tdd.dto.ProductCreateRequest
import com.example.tdd.dto.ProductResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(private val productRepository: ProductRepository) {

    @Transactional(readOnly = true)
    fun getAllProducts(): List<ProductResponse> {
        return productRepository.findAll().map { mapToProductResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getProductById(productId: Long): ProductResponse {
        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("상품을 찾을 수 없습니다: $productId") }

        return mapToProductResponse(product)
    }

    @Transactional
    fun createProduct(request: ProductCreateRequest): ProductResponse {
        val product = Product(
            name = request.name,
            price = request.price,
            stockQuantity = request.stockQuantity
        )

        val savedProduct = productRepository.save(product)
        return mapToProductResponse(savedProduct)
    }

    @Transactional(readOnly = true)
    fun getTopSellingProducts(): List<ProductResponse> {
        return productRepository.findTop5BestSellingProducts()
            .take(5)
            .map { mapToProductResponse(it) }
    }

    private fun mapToProductResponse(product: Product): ProductResponse {
        return ProductResponse(
            id = product.id ?: throw IllegalStateException("상품 ID가 없습니다"),
            name = product.name,
            price = product.price,
            stockQuantity = product.getStockQuantity()
        )
    }
}
