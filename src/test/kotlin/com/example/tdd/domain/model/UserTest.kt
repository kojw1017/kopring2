package com.example.tdd.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

@DisplayName("User 도메인 모델 테스트")
class UserTest {

    @Test
    @DisplayName("새로운 사용자 생성 시 UUID와 잔액 0으로 초기화됨")
    fun createNewUser() {
        // when
        val user = User.create()

        // then
        assertNotNull(user.id)
        assertEquals(0, user.balance)
    }

    @Test
    @DisplayName("기존 ID와 잔액으로 사용자 생성")
    fun createUserWithIdAndBalance() {
        // given
        val id = UUID.randomUUID()
        val balance = 10000L

        // when
        val user = User.of(id, balance)

        // then
        assertEquals(id, user.id)
        assertEquals(balance, user.balance)
    }

    @Test
    @DisplayName("잔액 충전 성공")
    fun chargeBalanceSuccess() {
        // given
        val user = User.create()
        val chargeAmount = 5000L

        // when
        val chargedUser = user.chargeBalance(chargeAmount)

        // then
        assertEquals(5000L, chargedUser.balance)
        // 원본 객체는 변경되지 않음 (불변성 확인)
        assertEquals(0L, user.balance)
    }

    @Test
    @DisplayName("0 이하의 금액으로 충전 시 예외 발생")
    fun chargeBalanceWithZeroOrNegativeAmount() {
        // given
        val user = User.create()

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            user.chargeBalance(0)
        }
        assertEquals("충전 금액은 0보다 커야 합니다.", exception.message)

        val negativeException = assertThrows<IllegalArgumentException> {
            user.chargeBalance(-1000)
        }
        assertEquals("충전 금액은 0보다 커야 합니다.", negativeException.message)
    }

    @Test
    @DisplayName("결제 성공")
    fun paySuccess() {
        // given
        val initialBalance = 10000L
        val user = User.of(UUID.randomUUID(), initialBalance)
        val paymentAmount = 3000L

        // when
        val paidUser = user.pay(paymentAmount)

        // then
        assertEquals(initialBalance - paymentAmount, paidUser.balance)
        // 원본 객체는 변경되지 않음 (불변성 확인)
        assertEquals(initialBalance, user.balance)
    }

    @Test
    @DisplayName("0 이하의 금액으로 결제 시 예외 발생")
    fun payWithZeroOrNegativeAmount() {
        // given
        val user = User.of(UUID.randomUUID(), 10000L)

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            user.pay(0)
        }
        assertEquals("결제 금액은 0보다 커야 합니다.", exception.message)

        val negativeException = assertThrows<IllegalArgumentException> {
            user.pay(-1000)
        }
        assertEquals("결제 금액은 0보다 커야 합니다.", negativeException.message)
    }

    @Test
    @DisplayName("잔액이 부족할 경우 결제 시 예외 발생")
    fun payWithInsufficientBalance() {
        // given
        val initialBalance = 1000L
        val user = User.of(UUID.randomUUID(), initialBalance)
        val paymentAmount = 2000L

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            user.pay(paymentAmount)
        }
        assertEquals("잔액이 부족합니다.", exception.message)
    }
}
