package com.example.tdd.application.port.`in`

import java.util.UUID

/**
 * 대기열 토큰 발급 유스케이스 인터페이스
 */
interface IssueQueueTokenUseCase {
    /**
     * 유저 대기열 토큰 발급
     * @return 발급된 토큰 정보
     */
    fun issueToken(): QueueTokenResponse

    /**
     * 대기 번호 조회
     * @param token 대기열 토큰
     * @return 대기 번호 정보
     */
    fun getQueueStatus(token: String): QueueStatusResponse
}

/**
 * 대기열 토큰 응답 DTO
 */
data class QueueTokenResponse(
    val userId: UUID,
    val token: String,
    val queueNumber: Int,
    val estimatedWaitTimeMinutes: Int
)

/**
 * 대기열 상태 응답 DTO
 */
data class QueueStatusResponse(
    val token: String,
    val queueNumber: Int,
    val isActive: Boolean,
    val estimatedWaitTimeMinutes: Int,
    val position: Int
)
