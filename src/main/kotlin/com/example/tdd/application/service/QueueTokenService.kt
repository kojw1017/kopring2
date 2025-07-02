package com.example.tdd.application.service

import com.example.tdd.application.port.`in`.QueueStatus
import com.example.tdd.application.port.`in`.QueueStatusResponse
import com.example.tdd.application.port.`in`.QueueTokenUseCase
import com.example.tdd.application.port.`in`.TokenResponse
import com.example.tdd.application.port.out.QueueManagerPort
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

/**
 * 토큰 발급 및 대기열 관리 서비스
 */
@Service
class QueueTokenService(
    private val queueManager: QueueManagerPort,
    private val tokenProvider: TokenProvider,
    @Value("\${queue.active-user-limit}")
    private val activeUserLimit: Int
) : QueueTokenUseCase {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 사용자 대기열 토큰을 발급합니다.
     */
    override fun issueToken(userId: String): TokenResponse {
        log.info("토큰 발급 요청: userId={}", userId)

        val isActive = queueManager.isActive(userId)
        if (isActive) {
            log.info("사용자 '{}'는 이미 활성 상태입니다.", userId)
        }

        val queuePosition = if (!isActive) queueManager.addToQueue(userId) else null
        if (queuePosition != null) {
            log.info("사용자 '{}'를 대기열에 추가했습니다. 순번: {}", userId, queuePosition)
        }

        if (!isActive && queuePosition != null && queuePosition <= activeUserLimit) {
            queueManager.activateUser(userId)
            log.info("사용자 '{}'를 활성 상태로 변경했습니다. (순번: {} <= 활성 제한: {})", userId, queuePosition, activeUserLimit)
        }

        val status = if (isActive || (queuePosition != null && queuePosition <= activeUserLimit)) {
            QueueStatus.ACTIVE
        } else {
            QueueStatus.WAITING
        }

        val token = tokenProvider.createToken(userId, status)
        val expiresIn = tokenProvider.getTokenValidity(status)

        log.info("토큰 발급 완료: userId={}, status={}, rank={}", userId, status, queuePosition)
        return TokenResponse(
            token = token,
            status = status,
            rank = queuePosition,
            expiresIn = expiresIn
        )
    }

    /**
     * 사용자의 대기열 상태를 조회합니다.
     */
    override fun getQueueStatus(token: String): QueueStatusResponse {
        log.debug("대기열 상태 조회 요청")
        val claims = try {
            tokenProvider.validateToken(token)
        } catch (e: Exception) {
            log.warn("토큰 검증 실패", e)
            throw e
        }
        val userId = claims.subject
        log.info("대기열 상태 조회: userId={}", userId)

        val isActive = queueManager.isActive(userId)
        val queuePosition = queueManager.getQueuePosition(userId)

        val status = if (isActive) {
            QueueStatus.ACTIVE
        } else if (queuePosition != null) {
            QueueStatus.WAITING
        } else {
            QueueStatus.EXPIRED
        }

        val estimatedActivationTime = if (queuePosition != null && queuePosition > activeUserLimit) {
            val waitingUsers = queuePosition - activeUserLimit
            waitingUsers * 10L // 대략적인 예상 시간
        } else {
            null
        }

        log.info("대기열 상태 조회 완료: userId={}, status={}, rank={}", userId, status, queuePosition)
        return QueueStatusResponse(
            userId = userId,
            status = status,
            rank = queuePosition,
            estimatedActivationTime = estimatedActivationTime
        )
    }
}

/**
 * 토큰 관리를 위한 인터페이스
 */
interface TokenProvider {
    fun createToken(userId: String, status: QueueStatus): String
    fun validateToken(token: String): Claims
    fun getTokenValidity(status: QueueStatus): Int
}

/**
 * JWT 클레임을 위한 간단한 인터페이스
 */
interface Claims {
    val subject: String
    val expiration: Date
    fun get(key: String): Any?
}
