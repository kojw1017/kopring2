package com.example.tdd.domain.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * 대기열 토큰 도메인 모델
 */
class QueueToken private constructor(
    val userId: UUID,
    val token: String,
    val queueNumber: Int,
    val issuedAt: LocalDateTime,
    val expiresAt: LocalDateTime,
    val status: QueueStatus
) {
    companion object {
        fun create(userId: UUID, queueNumber: Int, tokenValiditySeconds: Long): QueueToken {
            val now = LocalDateTime.now()
            return QueueToken(
                userId = userId,
                token = UUID.randomUUID().toString(),
                queueNumber = queueNumber,
                issuedAt = now,
                expiresAt = now.plusSeconds(tokenValiditySeconds),
                status = QueueStatus.WAITING
            )
        }
    }

    /**
     * 토큰이 유효한지 확인
     */
    fun isValid(): Boolean {
        return LocalDateTime.now().isBefore(expiresAt) && status == QueueStatus.WAITING
    }

    /**
     * 토큰 활성화 (대기열에서 빠져나와 서비스 이용 가능)
     */
    fun activate(): QueueToken {
        return copy(status = QueueStatus.ACTIVE)
    }

    /**
     * 토큰 만료 처리
     */
    fun expire(): QueueToken {
        return copy(status = QueueStatus.EXPIRED)
    }

    private fun copy(
        userId: UUID = this.userId,
        token: String = this.token,
        queueNumber: Int = this.queueNumber,
        issuedAt: LocalDateTime = this.issuedAt,
        expiresAt: LocalDateTime = this.expiresAt,
        status: QueueStatus = this.status
    ): QueueToken {
        return QueueToken(userId, token, queueNumber, issuedAt, expiresAt, status)
    }
}

/**
 * 대기열 상태
 */
enum class QueueStatus {
    WAITING,    // 대기 중
    ACTIVE,     // 활성화 (서비스 이용 가능)
    EXPIRED     // 만료됨
}
