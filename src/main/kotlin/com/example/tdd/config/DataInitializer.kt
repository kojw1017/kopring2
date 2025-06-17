package com.example.tdd.config

import com.example.tdd.domain.entity.Product
import com.example.tdd.domain.entity.User
import com.example.tdd.domain.repository.ProductRepository
import com.example.tdd.domain.repository.UserRepository
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Configuration
class DataInitializer(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) {

    @PostConstruct
    fun init() {
        initData()
    }

    @Transactional
    fun initData() {
        // 기본 사용자 생성
        if (userRepository.count() == 0L) {
            val user1 = User(username = "user1")
            val user2 = User(username = "user2")

            userRepository.saveAll(listOf(user1, user2))

            // 초기 잔액 충전
            user1.charge(BigDecimal.valueOf(50000))
            user2.charge(BigDecimal.valueOf(30000))

            userRepository.saveAll(listOf(user1, user2))
        }

        // 기본 상품 생성
        if (productRepository.count() == 0L) {
            val products = listOf(
                Product(name = "노트북", price = BigDecimal.valueOf(1500000), stockQuantity = 10),
                Product(name = "스마트폰", price = BigDecimal.valueOf(1000000), stockQuantity = 20),
                Product(name = "태블릿", price = BigDecimal.valueOf(800000), stockQuantity = 15),
                Product(name = "이어폰", price = BigDecimal.valueOf(300000), stockQuantity = 30),
                Product(name = "스마트워치", price = BigDecimal.valueOf(500000), stockQuantity = 25),
                Product(name = "블루투스 스피커", price = BigDecimal.valueOf(200000), stockQuantity = 40),
                Product(name = "마우스", price = BigDecimal.valueOf(50000), stockQuantity = 100),
                Product(name = "키보드", price = BigDecimal.valueOf(150000), stockQuantity = 50)
            )

            productRepository.saveAll(products)
        }
    }
}
