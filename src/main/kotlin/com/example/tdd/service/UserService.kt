package com.example.tdd.service

import com.example.tdd.domain.entity.User
import com.example.tdd.domain.repository.UserRepository
import com.example.tdd.dto.UserBalanceResponse
import com.example.tdd.dto.UserChargeRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional
    fun chargeBalance(userId: Long, request: UserChargeRequest): UserBalanceResponse {
        val user = userRepository.findByIdWithPessimisticLock(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다: $userId") }

        val newBalance = user.charge(request.amount)
        return UserBalanceResponse(userId, newBalance)
    }

    @Transactional(readOnly = true)
    fun getBalance(userId: Long): UserBalanceResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다: $userId") }

        return UserBalanceResponse(userId, user.getBalance())
    }

    @Transactional
    fun pay(userId: Long, amount: BigDecimal) {
        val user = userRepository.findByIdWithPessimisticLock(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다: $userId") }

        user.pay(amount)
    }

    @Transactional
    fun createUser(username: String): User {
        return userRepository.save(User(username = username))
    }
}
