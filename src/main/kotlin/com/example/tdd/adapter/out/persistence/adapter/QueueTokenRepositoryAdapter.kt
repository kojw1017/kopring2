package com.example.tdd.adapter.out.persistence.adapter

import com.example.tdd.adapter.out.persistence.entity.QueueTokenEntity
import com.example.tdd.adapter.out.persistence.repository.QueueTokenJpaRepository
import com.example.tdd.domain.model.QueueStatus
import com.example.tdd.domain.model.QueueToken
import com.example.tdd.domain.repository.QueueTokenRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class QueueTokenRepositoryAdapter(
    private val queueTokenJpaRepository: QueueTokenJpaRepository
) : QueueTokenRepository {
    override fun findByToken(token: String): QueueToken? {
        return queueTokenJpaRepository.findById(token)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByUserId(userId: UUID): QueueToken? {
        return queueTokenJpaRepository.findByUserId(userId)?.toDomain()
    }

    override fun getNextQueueNumber(): Int {
        val maxQueueNumber = queueTokenJpaRepository.findMaxQueueNumber() ?: 0
        return maxQueueNumber + 1
    }

    override fun save(queueToken: QueueToken): QueueToken {
        val queueTokenEntity = QueueTokenEntity.fromDomain(queueToken)
        val savedEntity = queueTokenJpaRepository.save(queueTokenEntity)
        return savedEntity.toDomain()
    }

    override fun countActiveTokens(): Int {
        return queueTokenJpaRepository.countByStatus(QueueStatus.ACTIVE)
    }
}
