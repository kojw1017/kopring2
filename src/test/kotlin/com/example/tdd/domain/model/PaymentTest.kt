package com.example.tdd.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

@DisplayName("Payment 도메인 모델 테스트")
class PaymentTest {

    @Test
    @DisplayName("결제 생성 성공")
    fun createPayment() {
        // given
        val userId = UUID.randomUUID()
        val seatId = UUID.randomUUID()
        val amount = 50000L

        // when
        val payment = Payment.create(userId, seatId, amount)

        // then
        assertNotNull(payment.id)
        assertEquals(userId, payment.userId)
        assertEquals(seatId, payment.seatId)
        assertEquals(amount, payment.amount)
        assertEquals(PaymentStatus.PENDING, payment.status)
        assertNotNull(payment.createdAt)
        assertNull(payment.completedAt)
    }

    @Test
    @DisplayName("결제 완료 처리 성공")
    fun completePaymentSuccess() {
        // given
        val userId = UUID.randomUUID()
        val seatId = UUID.randomUUID()
        val amount = 50000L
        val payment = Payment.create(userId, seatId, amount)

        // when
        val completedPayment = payment.complete()

        // then
        assertEquals(PaymentStatus.COMPLETED, completedPayment.status)
        assertNotNull(completedPayment.completedAt)
        assertEquals(PaymentStatus.PENDING, payment.status) // 원본 객체는 변경되지 않음 (불변성 확인)
        assertNull(payment.completedAt) // 원본 객체는 변경되지 않음 (불변성 확인)
    }

    @Test
    @DisplayName("대기 중이 아닌 결제를 완료 처리 시 예외 발생")
    fun completeNonPendingPayment() {
        // given
        val userId = UUID.randomUUID()
        val seatId = UUID.randomUUID()
        val amount = 50000L
        val payment = Payment.create(userId, seatId, amount)
        val completedPayment = payment.complete()

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            completedPayment.complete()
        }
        assertEquals("대기 중인 결제만 완료 처리할 수 있습니다.", exception.message)
    }

    @Test
    @DisplayName("결제 실패 처리 성공")
    fun failPaymentSuccess() {
        // given
        val userId = UUID.randomUUID()
        val seatId = UUID.randomUUID()
        val amount = 50000L
        val payment = Payment.create(userId, seatId, amount)

        // when
        val failedPayment = payment.fail()

        // then
        assertEquals(PaymentStatus.FAILED, failedPayment.status)
        assertNotNull(failedPayment.completedAt)
        assertEquals(PaymentStatus.PENDING, payment.status) // 원본 객체는 변경되지 않음 (불변성 확인)
        assertNull(payment.completedAt) // 원본 객체는 변경되지 않음 (불변성 확인)
    }

    @Test
    @DisplayName("대기 중이 아닌 결제를 실패 처리 시 예외 발생")
    fun failNonPendingPayment() {
        // given
        val userId = UUID.randomUUID()
        val seatId = UUID.randomUUID()
        val amount = 50000L
        val payment = Payment.create(userId, seatId, amount)
        val failedPayment = payment.fail()

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            failedPayment.fail()
        }
        assertEquals("대기 중인 결제만 실패 처리할 수 있습니다.", exception.message)
    }
}
