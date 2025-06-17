package com.example.tdd.controller

import com.example.tdd.dto.ProductCreateRequest
import com.example.tdd.dto.ProductResponse
import com.example.tdd.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getAllProducts()
        return ResponseEntity.ok(products)
    }

    @GetMapping("/{productId}")
    fun getProductById(@PathVariable productId: Long): ResponseEntity<ProductResponse> {
        val product = productService.getProductById(productId)
        return ResponseEntity.ok(product)
    }

    @PostMapping
    fun createProduct(@Valid @RequestBody request: ProductCreateRequest): ResponseEntity<ProductResponse> {
        val product = productService.createProduct(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(product)
    }

    @GetMapping("/top-selling")
    fun getTopSellingProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getTopSellingProducts()
        return ResponseEntity.ok(products)
    }
}
