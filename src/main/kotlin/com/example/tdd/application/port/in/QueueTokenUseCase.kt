package com.example.tdd.application.port.`in`

/**
 * 대기열 토큰 발급 및 관리를 위한 인바운드 포트
 */
interface QueueTokenUseCase {
    /**
     * 사용자 대기열 토큰을 발급합니다.
     *
     * @param userId 사용자 ID
     * @return 발급된 토큰 정보
     */
    fun issueToken(userId: String): TokenResponse

    /**
     * 사용자의 대기열 상태를 조회합니다.
     *
     * @param token 대기열 토큰
     * @return 현재 대기열 상태 정보
     */
    fun getQueueStatus(token: String): QueueStatusResponse
}

/**
 * 토큰 발급 응답 데이터
 */
data class TokenResponse(
    val token: String,
    val status: QueueStatus,
    val rank: Int?,
    val expiresIn: Int
)

/**
 * 대기열 상태 응답 데이터
 */
data class QueueStatusResponse(
    val userId: String,
    val status: QueueStatus,
    val rank: Int?,
    val estimatedActivationTime: Long?
)

/**
 * 대기열 상태 Enum
 */
enum class QueueStatus {
    WAITING,   // 대기 중
    ACTIVE,    // 활성화 상태 (서비스 이용 가능)
    EXPIRED    // 만료됨
}
