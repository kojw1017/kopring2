package com.example.tdd.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

@DisplayName("QueueToken 도메인 모델 테스트")
class QueueTokenTest {

    @Test
    @DisplayName("대기열 토큰 생성 성공")
    fun createQueueToken() {
        // given
        val userId = UUID.randomUUID()
        val queueNumber = 5
        val tokenValiditySeconds = 3600L // 1시간

        // when
        val queueToken = QueueToken.create(userId, queueNumber, tokenValiditySeconds)

        // then
        assertEquals(userId, queueToken.userId)
        assertEquals(queueNumber, queueToken.queueNumber)
        assertEquals(QueueStatus.WAITING, queueToken.status)
        assertNotNull(queueToken.token)
        assertTrue(queueToken.issuedAt.isBefore(queueToken.expiresAt))
        assertTrue(queueToken.expiresAt.isAfter(LocalDateTime.now()))
    }

    @Test
    @DisplayName("만료되지 않은 대기 상태 토큰은 유효함")
    fun validToken() {
        // given
        val userId = UUID.randomUUID()
        val queueToken = QueueToken.create(userId, 1, 3600L)

        // when
        val isValid = queueToken.isValid()

        // then
        assertTrue(isValid)
    }

    @Test
    @DisplayName("만료된 토큰은 유효하지 않음")
    fun expiredTokenIsInvalid() {
        // given
        val userId = UUID.randomUUID()
        // 토큰 유효 기간을 -1초로 설정하여 이미 만료된 상태로 생성
        val queueToken = QueueToken.create(userId, 1, -1L)

        // when
        val isValid = queueToken.isValid()

        // then
        assertFalse(isValid)
    }

    @Test
    @DisplayName("활성화된 토큰은 상태가 ACTIVE로 변경됨")
    fun activateToken() {
        // given
        val userId = UUID.randomUUID()
        val queueToken = QueueToken.create(userId, 1, 3600L)

        // when
        val activatedToken = queueToken.activate()

        // then
        assertEquals(QueueStatus.ACTIVE, activatedToken.status)
        assertEquals(QueueStatus.WAITING, queueToken.status) // 원본 객체는 변경되지 않음 (불변성 확인)
    }

    @Test
    @DisplayName("만료 처리된 토큰은 상태가 EXPIRED로 변경됨")
    fun expireToken() {
        // given
        val userId = UUID.randomUUID()
        val queueToken = QueueToken.create(userId, 1, 3600L)

        // when
        val expiredToken = queueToken.expire()

        // then
        assertEquals(QueueStatus.EXPIRED, expiredToken.status)
        assertEquals(QueueStatus.WAITING, queueToken.status) // 원본 객체는 변경되지 않음 (불변성 확인)
    }
}
