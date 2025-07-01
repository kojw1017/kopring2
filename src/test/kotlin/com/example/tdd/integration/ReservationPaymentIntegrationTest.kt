package com.example.tdd.integration

import com.example.tdd.adapter.`in`.web.exception.ConcurrentModificationException
import com.example.tdd.adapter.`in`.web.exception.InsufficientBalanceException
import com.example.tdd.adapter.`in`.web.exception.ReservationExpiredException
import com.example.tdd.application.port.`in`.ReservationCommand
import com.example.tdd.application.port.`in`.PaymentCommand
import com.example.tdd.application.service.PaymentProcessingService
import com.example.tdd.application.service.SeatReservationService
import com.example.tdd.domain.model.*
import com.example.tdd.domain.service.PaymentService
import com.example.tdd.domain.service.ReservationService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 좌석 예약과 결제 프로세스에 대한 통합 테스트
 */
class ReservationPaymentIntegrationTest {

    // 도메인 서비스
    private lateinit var reservationService: ReservationService
    private lateinit var paymentService: PaymentService

    // 애플리케이션 서비스
    private lateinit var seatReservationService: SeatReservationService
    private lateinit var paymentProcessingService: PaymentProcessingService

    // 모의 포트
    private val seatRepository = mock<com.example.tdd.application.port.out.SeatRepositoryPort>()
    private val reservationRepository = mock<com.example.tdd.application.port.out.ReservationRepositoryPort>()
    private val userRepository = mock<com.example.tdd.application.port.out.UserRepositoryPort>()
    private val paymentRepository = mock<com.example.tdd.application.port.out.PaymentRepositoryPort>()
    private val queueManager = mock<com.example.tdd.application.port.out.QueueManagerPort>()
    private val lockManager = mock<com.example.tdd.application.port.out.LockManagerPort>()

    // 테스트 데이터
    private val userId = "test-user"
    private val scheduleId = 1L
    private val seatNumber = 1
    private val reservationId = 123L
    private val paymentId = 456L
    private val tempReservationMinutes = 5

    @BeforeEach
    fun setup() {
        // 도메인 서비스 초기화
        reservationService = ReservationService()
        paymentService = PaymentService()

        // 애플리케이션 서비스 초기화
        seatReservationService = SeatReservationService(
            reservationService = reservationService,
            seatRepository = seatRepository,
            reservationRepository = reservationRepository,
            lockManager = lockManager,
            tempReservationMinutes = tempReservationMinutes
        )

        paymentProcessingService = PaymentProcessingService(
            paymentService = paymentService,
            userRepository = userRepository,
            reservationRepository = reservationRepository,
            seatRepository = seatRepository,
            paymentRepository = paymentRepository,
            queueManager = queueManager,
            lockManager = lockManager
        )

        // 락 매니저 설정 - 기본적으로 락 획득 성공
        whenever(lockManager.acquireLock(any(), any(), any())).thenReturn(true)
        whenever(lockManager.releaseLock(any(), any())).thenReturn(true)
    }

    @Test
    fun `전체 예약 및 결제 성공 시나리오`() {
        // Given
        // 1. 좌석 데이터 준비
        val seat = Seat(
            seatId = 1L,
            scheduleId = scheduleId,
            seatNumber = seatNumber,
            price = BigDecimal("100000")
        )
        whenever(seatRepository.findByScheduleIdAndSeatNumber(scheduleId, seatNumber)).thenReturn(seat)
        whenever(seatRepository.save(any())).thenAnswer { invocation -> invocation.getArgument(0) }

        // 2. 예약 저장 설정
        whenever(reservationRepository.save(any())).thenAnswer { invocation ->
            val reservation = invocation.getArgument<Reservation>(0)
            reservation
        }

        // 3. 예약 조회 설정
        val capturedReservation = argumentCaptor<Reservation>()
        whenever(reservationRepository.save(capture(capturedReservation))).thenAnswer {
            capturedReservation.lastValue
        }

        // 4. 사용자 데이터 준비
        val user = User(userId, BigDecimal("200000"))
        whenever(userRepository.findByUserId(userId)).thenReturn(user)

        // 5. 결제 저장 설정
        whenever(paymentRepository.save(any())).thenAnswer { invocation -> invocation.getArgument(0) }

        // When
        // 1. 좌석 예약 진행
        val reservationCommand = ReservationCommand(userId, scheduleId, seatNumber)
        val reservationResponse = seatReservationService.reserveSeat(reservationCommand)

        // 2. 저장된 예약 확인
        val savedReservation = capturedReservation.lastValue
        whenever(reservationRepository.findById(savedReservation.reservationId)).thenReturn(savedReservation)
        whenever(seatRepository.findById(savedReservation.seatId)).thenReturn(seat)

        // 3. 결제 진행
        val paymentCommand = PaymentCommand(userId, savedReservation.reservationId)
        val paymentResponse = paymentProcessingService.processPayment(paymentCommand)

        // Then
        // 1. 예약 응답 검증
        assertEquals(savedReservation.reservationId, reservationResponse.reservationId)
        assertEquals(seatNumber, reservationResponse.seatNumber)
        assertEquals("PENDING", reservationResponse.status)

        // 2. 좌석 상태 검증
        assertEquals(SeatStatus.SOLD, seat.status)

        // 3. 사용자 잔액 검증
        assertEquals(BigDecimal("100000"), user.balance)

        // 4. 예약 상태 검증
        assertEquals(ReservationStatus.PAID, savedReservation.status)

        // 5. 결제 응답 검증
        assertEquals(savedReservation.reservationId, paymentResponse.reservationId)
        assertEquals("COMPLETED", paymentResponse.status)

        // 6. 대기열 토큰 비활성화 확인
        verify(queueManager).deactivateUser(userId)
    }

    @Test
    fun `잔액 부족으로 결제 실패 시나리오`() {
        // Given
        // 1. 좌석 데이터 준비
        val seat = Seat(
            seatId = 1L,
            scheduleId = scheduleId,
            seatNumber = seatNumber,
            price = BigDecimal("100000")
        )
        whenever(seatRepository.findByScheduleIdAndSeatNumber(scheduleId, seatNumber)).thenReturn(seat)
        whenever(seatRepository.save(any())).thenAnswer { invocation -> invocation.getArgument(0) }

        // 2. 예약 저장 설정
        val reservation = Reservation(
            reservationId = reservationId,
            userId = userId,
            seatId = seat.seatId,
            expiresAt = LocalDateTime.now().plusMinutes(5)
        )
        whenever(reservationRepository.save(any())).thenReturn(reservation)
        whenever(reservationRepository.findById(reservationId)).thenReturn(reservation)

        // 3. 잔액 부족한 사용자 데이터
        val user = User(userId, BigDecimal("50000")) // 잔액 부족
        whenever(userRepository.findByUserId(userId)).thenReturn(user)

        // 4. 좌석 조회 설정
        whenever(seatRepository.findById(seat.seatId)).thenReturn(seat)

        // When & Then
        // 1. 좌석 예약은 성공
        val reservationCommand = ReservationCommand(userId, scheduleId, seatNumber)
        val reservationResponse = seatReservationService.reserveSeat(reservationCommand)

        // 2. 하지만 결제는 잔액 부족으로 실패해야 함
        val paymentCommand = PaymentCommand(userId, reservation.reservationId)
        assertThrows<InsufficientBalanceException> {
            paymentProcessingService.processPayment(paymentCommand)
        }

        // 3. 좌석과 예약 상태는 변경되지 않아야 함
        assertEquals(SeatStatus.RESERVED, seat.status)
        assertEquals(ReservationStatus.PENDING, reservation.status)
        assertEquals(BigDecimal("50000"), user.balance) // 잔액 그대로
    }

    @Test
    fun `동시 예약 시도 시 한 명만 성공하는 시나리오`() {
        // Given
        // 1. 좌석 데이터 준비
        val seat = Seat(
            seatId = 1L,
            scheduleId = scheduleId,
            seatNumber = seatNumber,
            price = BigDecimal("100000")
        )
        whenever(seatRepository.findByScheduleIdAndSeatNumber(scheduleId, seatNumber)).thenReturn(seat)
        whenever(seatRepository.save(any())).thenAnswer { invocation -> invocation.getArgument(0) }

        // 2. 첫 번째 사용자는 락 획득 성공
        val firstUserId = "user-1"
        whenever(lockManager.acquireLock(eq("seat:$scheduleId:$seatNumber"), eq(firstUserId), any())).thenReturn(true)

        // 3. 두 번째 사용자는 락 획득 실패
        val secondUserId = "user-2"
        whenever(lockManager.acquireLock(eq("seat:$scheduleId:$seatNumber"), eq(secondUserId), any())).thenReturn(false)

        // When & Then
        // 1. 첫 번째 사용자의 예약 시도는 성공해야 함
        val firstCommand = ReservationCommand(firstUserId, scheduleId, seatNumber)
        val reservationResponse = seatReservationService.reserveSeat(firstCommand)
        assertNotNull(reservationResponse)

        // 2. 두 번째 사용자의 예약 시도는 실패해야 함
        val secondCommand = ReservationCommand(secondUserId, scheduleId, seatNumber)
        assertThrows<ConcurrentModificationException> {
            seatReservationService.reserveSeat(secondCommand)
        }

        // 3. 좌석은 첫 번째 사용자에 의해 예약됨
        assertEquals(SeatStatus.RESERVED, seat.status)
    }
}
