package com.example.tdd.application.service

import com.example.tdd.application.port.`in`.QueueStatus
import com.example.tdd.application.port.`in`.QueueStatusResponse
import com.example.tdd.application.port.`in`.QueueTokenUseCase
import com.example.tdd.application.port.`in`.TokenResponse
import com.example.tdd.application.port.out.QueueManagerPort
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

    /**
     * 사용자 대기열 토큰을 발급합니다.
     */
    override fun issueToken(userId: String): TokenResponse {
        // 사용자가 이미 활성 상태인지 확인
        val isActive = queueManager.isActive(userId)

        // 사용자를 대기열에 추가하고 순번을 가져옴 (이미 있으면 기존 순번 반환)
        val queuePosition = if (!isActive) queueManager.addToQueue(userId) else null

        // 활성 상태가 아니고 대기열 순번이 활성화 제한 내에 있으면 활성화
        if (!isActive && queuePosition != null && queuePosition <= activeUserLimit) {
            queueManager.activateUser(userId)
        }

        // 토큰 생성
        val status = if (isActive || (queuePosition != null && queuePosition <= activeUserLimit)) {
            QueueStatus.ACTIVE
        } else {
            QueueStatus.WAITING
        }

        val token = tokenProvider.createToken(userId, status)
        val expiresIn = tokenProvider.getTokenValidity(status)

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
        // 토큰 검증 및 사용자 ID 추출
        val claims = tokenProvider.validateToken(token)
        val userId = claims.subject

        // 대기열 상태 확인
        val isActive = queueManager.isActive(userId)
        val queuePosition = queueManager.getQueuePosition(userId)

        // 대기열 상태에 따른 응답 생성
        val status = if (isActive) {
            QueueStatus.ACTIVE
        } else if (queuePosition != null) {
            QueueStatus.WAITING
        } else {
            QueueStatus.EXPIRED
        }

        // 활성화 예상 시간 계산 (대기열 위치에 따라 대략적으로 계산)
        val estimatedActivationTime = if (queuePosition != null && queuePosition > activeUserLimit) {
            val waitingUsers = queuePosition - activeUserLimit
            // 대략 10초당 1명 활성화된다고 가정
            waitingUsers * 10L
        } else {
            null
        }

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
    /**
     * 토큰을 생성합니다.
     */
    fun createToken(userId: String, status: QueueStatus): String

    /**
     * 토큰을 검증하고 클레임을 반환합니다.
     */
    fun validateToken(token: String): Claims

    /**
     * 토큰 유효 시간을 반환합니다.
     */
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
