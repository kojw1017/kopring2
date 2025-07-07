package com.example.tdd.domain.repository

import com.example.tdd.domain.model.QueueToken
import java.util.UUID

/**
 * 대기열 토큰 리포지토리 인터페이스
 */
interface QueueTokenRepository {
    /**
     * 토큰으로 대기열 토큰 조회
     */
    fun findByToken(token: String): QueueToken?

    /**
     * 사용자 ID로 대기열 토큰 조회
     */
    fun findByUserId(userId: UUID): QueueToken?

    /**
     * 다음 대기 번호 조회
     */
    fun getNextQueueNumber(): Int

    /**
     * 대기열 토큰 저장
     */
    fun save(queueToken: QueueToken): QueueToken

    /**
     * 활성화된 대기열 토큰 수 조회
     */
    fun countActiveTokens(): Int
}
