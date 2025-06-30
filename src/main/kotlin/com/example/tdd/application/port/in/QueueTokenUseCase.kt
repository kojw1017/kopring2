package com.example.tdd.application.port.`in`

import java.time.LocalDateTime

/**
 * 큐 토큰 관리 인바운드 포트
 */
interface QueueTokenUseCase {
    /**
     * 큐 토큰을 발급합니다.
     */
    fun issueToken(command: IssueTokenCommand): TokenResponse

    /**
     * 토큰 상태를 조회합니다.
     */
    fun getTokenStatus(token: String): TokenStatusResponse

    /**
     * 대기 중인 토큰들을 활성화합니다.
     */
    fun activateWaitingTokens(): TokenActivationResult
}

/**
 * 토큰 발급 명령
 */
data class IssueTokenCommand(
    val userId: String
)

/**
 * 토큰 응답 데이터
 */
data class TokenResponse(
    val token: String,
    val queuePosition: Long,
    val estimatedWaitTime: Long,
    val isActive: Boolean
)

/**
 * 토큰 상태 응답 데이터
 */
data class TokenStatusResponse(
    val token: String,
    val isValid: Boolean,
    val isActive: Boolean,
    val queuePosition: Long,
    val estimatedWaitTime: Long,
    val ttl: Long
)

/**
 * 토큰 활성화 결과
 */
data class TokenActivationResult(
    val activatedCount: Int,
    val totalWaitingCount: Long
)
