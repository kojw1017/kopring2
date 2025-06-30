package com.example.tdd.application.service

import com.example.tdd.application.port.`in`.BalanceResponse
import com.example.tdd.application.port.`in`.ChargeBalanceCommand
import com.example.tdd.application.port.`in`.UserBalanceUseCase
import com.example.tdd.application.port.out.UserRepository
import com.example.tdd.domain.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

/**
 * 사용자 잔액 관리 서비스
 * 사용자 잔액 충전 및 조회 기능을 제공합니다.
 */
@Service
class UserBalanceService(
    private val userRepository: UserRepository
) : UserBalanceUseCase {

    /**
     * 사용자 잔액을 충전합니다.
     */
    @Transactional
    override fun chargeBalance(command: ChargeBalanceCommand): BalanceResponse {
        // 사용자 조회 또는 생성
        val user = userRepository.findById(command.userId) ?: User(command.userId)

        // 잔액 충전
        user.charge(command.amount)

        // 저장 및 응답 반환
        val savedUser = userRepository.save(user)
        return BalanceResponse(
            userId = savedUser.userId,
            balance = savedUser.balance
        )
    }

    /**
     * 사용자 잔액을 조회합니다.
     */
    @Transactional(readOnly = true)
    override fun getBalance(userId: String): BalanceResponse {
        // 사용자 조회
        val user = userRepository.findById(userId)
            ?: return BalanceResponse(userId, BigDecimal.ZERO)

        return BalanceResponse(
            userId = user.userId,
            balance = user.balance
        )
    }
}
