package com.example.tdd.application.service

import com.example.tdd.application.exception.InvalidRequestException
import com.example.tdd.application.exception.ResourceNotFoundException
import com.example.tdd.application.port.`in`.BalanceManagementUseCase
import com.example.tdd.application.port.`in`.BalanceResponse
import com.example.tdd.application.port.`in`.ChargeBalanceCommand
import com.example.tdd.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class BalanceService(
    private val userRepository: UserRepository
) : BalanceManagementUseCase {

    @Transactional
    override fun chargeBalance(command: ChargeBalanceCommand): BalanceResponse {
        if (command.amount <= 0) {
            throw InvalidRequestException("충전 금액은 0보다 커야 합니다.")
        }

        val user = userRepository.findById(command.userId)
            ?: throw ResourceNotFoundException("User", command.userId.toString())

        val chargedUser = user.chargeBalance(command.amount)
        val savedUser = userRepository.save(chargedUser)

        return BalanceResponse(
            userId = savedUser.id,
            balance = savedUser.balance
        )
    }

    @Transactional(readOnly = true)
    override fun getBalance(userId: UUID): BalanceResponse {
        val user = userRepository.findById(userId)
            ?: throw ResourceNotFoundException("User", userId.toString())

        return BalanceResponse(
            userId = user.id,
            balance = user.balance
        )
    }
}
