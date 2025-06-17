package com.example.tdd.service

import com.example.tdd.domain.entity.User
import com.example.tdd.domain.repository.UserRepository
import com.example.tdd.dto.UserChargeRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.util.Optional
import kotlin.test.assertEquals

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        userService = UserService(userRepository)
    }

    @Test
    fun `잔액 충전 성공 테스트`() {
        // given
        val userId = 1L
        val initialBalance = BigDecimal.ZERO
        val chargeAmount = BigDecimal.valueOf(10000)
        val user = spy(User(id = userId, username = "testUser", balance = initialBalance))

        whenever(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user))

        // when
        val result = userService.chargeBalance(userId, UserChargeRequest(chargeAmount))

        // then
        verify(user).charge(chargeAmount)
        assertEquals(userId, result.userId)
        assertEquals(chargeAmount, result.balance)
    }

    @Test
    fun `잔액 충전 - 사용자가 존재하지 않을 경우 예외 발생`() {
        // given
        val userId = 1L
        val chargeAmount = BigDecimal.valueOf(10000)

        whenever(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.empty())

        // when & then
        assertThrows<IllegalArgumentException> {
            userService.chargeBalance(userId, UserChargeRequest(chargeAmount))
        }
    }

    @Test
    fun `잔액 조회 테스트`() {
        // given
        val userId = 1L
        val balance = BigDecimal.valueOf(5000)
        val user = User(id = userId, username = "testUser", balance = balance)

        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))

        // when
        val result = userService.getBalance(userId)

        // then
        assertEquals(userId, result.userId)
        assertEquals(balance, result.balance)
    }

    @Test
    fun `결제 성공 테스트`() {
        // given
        val userId = 1L
        val initialBalance = BigDecimal.valueOf(10000)
        val payAmount = BigDecimal.valueOf(5000)
        val user = spy(User(id = userId, username = "testUser", balance = initialBalance))

        whenever(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user))

        // when
        userService.pay(userId, payAmount)

        // then
        verify(user).pay(payAmount)
    }

    @Test
    fun `결제 - 잔액 부족 시 예외 발생`() {
        // given
        val userId = 1L
        val initialBalance = BigDecimal.valueOf(1000)
        val payAmount = BigDecimal.valueOf(5000)
        val user = User(id = userId, username = "testUser", balance = initialBalance)

        whenever(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user))

        // when & then
        assertThrows<IllegalArgumentException> {
            userService.pay(userId, payAmount)
        }
    }
}
