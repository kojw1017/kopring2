package com.example.tdd.adapter.out.persistence.repository

import com.example.tdd.adapter.out.persistence.entity.QueueTokenEntity
import com.example.tdd.domain.model.QueueStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface QueueTokenJpaRepository : JpaRepository<QueueTokenEntity, String> {
    fun findByUserId(userId: UUID): QueueTokenEntity?

    @Query("SELECT COUNT(q) FROM QueueTokenEntity q WHERE q.status = :status")
    fun countByStatus(status: QueueStatus): Int

    @Query("SELECT MAX(q.queueNumber) FROM QueueTokenEntity q")
    fun findMaxQueueNumber(): Int?
}
