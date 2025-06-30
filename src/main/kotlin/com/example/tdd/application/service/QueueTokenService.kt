package com.example.tdd.application.service

import com.example.tdd.application.port.`in`.QueueTokenUseCase
import com.example.tdd.application.port.`in`.IssueTokenCommand
import com.example.tdd.application.port.`in`.TokenResponse
import com.example.tdd.application.port.`in`.TokenStatusResponse
import com.example.tdd.application.port.`in`.TokenActivationResult
import com.example.tdd.application.port.out.QueueTokenRepository
import com.example.tdd.application.port.out.QueueRepository
import com.example.tdd.domain.exception.InvalidTokenException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 큐 토큰 관리 애플리케이션 서비스
 */
@Service
class QueueTokenService(
    private val queueTokenRepository: QueueTokenRepository,
    private val queueRepository: QueueRepository
) : QueueTokenUseCase {

    @Transactional
    override fun issueToken(command: IssueTokenCommand): TokenResponse {
        // 기존 활성 토큰 확인
        val existingTokens = queueTokenRepository.findActiveTokensByUserId(command.userId)
        if (existingTokens.isNotEmpty()) {
            val token = existingTokens.first()
            val position = queueRepository.getQueuePosition(token)
            return TokenResponse(
                token = token,
                queuePosition = position,
                estimatedWaitTime = calculateEstimatedWaitTime(position),
                isActive = position == 0L
            )
        }

        // 새 토큰 발급
        val token = queueTokenRepository.generateToken(command.userId)
        val position = queueRepository.addToQueue(command.userId, token)

        return TokenResponse(
            token = token,
            queuePosition = position,
            estimatedWaitTime = calculateEstimatedWaitTime(position),
            isActive = position == 0L
        )
    }

    @Transactional(readOnly = true)
    override fun getTokenStatus(token: String): TokenStatusResponse {
        val isValid = queueTokenRepository.isValidToken(token)
        if (!isValid) {
            throw InvalidTokenException("유효하지 않은 토큰입니다.")
        }

        val position = queueRepository.getQueuePosition(token)
        val ttl = queueTokenRepository.getTokenTtl(token)

        return TokenStatusResponse(
            token = token,
            isValid = true,
            isActive = position == 0L,
            queuePosition = position,
            estimatedWaitTime = calculateEstimatedWaitTime(position),
            ttl = ttl
        )
    }

    @Transactional
    override fun activateWaitingTokens(): TokenActivationResult {
        val activeTokenCount = queueRepository.getActiveTokenCount()
        val maxActiveTokens = 100L // 최대 활성 토큰 수

        val availableSlots = maxActiveTokens - activeTokenCount
        if (availableSlots <= 0) {
            return TokenActivationResult(0, queueRepository.getQueueSize())
        }

        val activatedTokens = queueRepository.activateWaitingTokens(availableSlots.toInt())
        activatedTokens.forEach { token ->
            queueTokenRepository.activateToken(token)
        }

        return TokenActivationResult(
            activatedCount = activatedTokens.size,
            totalWaitingCount = queueRepository.getQueueSize()
        )
    }

    private fun calculateEstimatedWaitTime(position: Long): Long {
        // 1분에 10명씩 처리된다고 가정
        return if (position <= 0) 0L else (position * 6) // 초 단위
    }
}
