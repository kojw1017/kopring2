package com.example.tdd.adapter.out.persistence.entity

import com.example.tdd.domain.model.QueueStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "queue_tokens")
class QueueTokenEntity(
    @Id
    val token: String,

    val userId: UUID,

    val queueNumber: Int,

    val issuedAt: LocalDateTime,

    val expiresAt: LocalDateTime,

    @Enumerated(EnumType.STRING)
    val status: QueueStatus
) {
    companion object {
        fun fromDomain(domain: com.example.tdd.domain.model.QueueToken): QueueTokenEntity {
            return QueueTokenEntity(
                token = domain.token,
                userId = domain.userId,
                queueNumber = domain.queueNumber,
                issuedAt = domain.issuedAt,
                expiresAt = domain.expiresAt,
                status = domain.status
            )
        }
    }

    fun toDomain(): com.example.tdd.domain.model.QueueToken {
        // 도메인 모델의 create 메서드를 사용한 후 상태를 업데이트하는 방식으로 변환
        // 이는 private 생성자로 인해 직접 생성이 불가능하기 때문
        val queueToken = com.example.tdd.domain.model.QueueToken.create(
            userId = userId,
            queueNumber = queueNumber,
            tokenValiditySeconds = java.time.Duration.between(issuedAt, expiresAt).seconds
        )

        return when (status) {
            QueueStatus.ACTIVE -> queueToken.activate()
            QueueStatus.EXPIRED -> queueToken.expire()
            else -> queueToken
        }
    }
}
