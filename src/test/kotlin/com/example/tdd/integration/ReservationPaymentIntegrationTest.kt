package com.example.tdd.integration

import com.example.tdd.adapter.`in`.web.PaymentRequest
import com.example.tdd.adapter.`in`.web.ReservationRequest
import com.example.tdd.adapter.out.persistence.entity.ReservationStatusEntity
import com.example.tdd.adapter.out.persistence.entity.ScheduleEntity
import com.example.tdd.adapter.out.persistence.entity.SeatEntity
import com.example.tdd.adapter.out.persistence.entity.SeatStatusEntity
import com.example.tdd.adapter.out.persistence.entity.UserEntity
import com.example.tdd.adapter.out.persistence.repository.ReservationJpaRepository
import com.example.tdd.adapter.out.persistence.repository.ScheduleJpaRepository
import com.example.tdd.adapter.out.persistence.repository.SeatJpaRepository
import com.example.tdd.adapter.out.persistence.repository.UserJpaRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.assertEquals

/**
 * 좌석 예약과 결제 프로세스의 E2E 통합 테스트
 * 실제 데이터베이스와 Redis를 사용하여 전체 플로우를 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional // 테스트 후 데이터 롤백
class ReservationPaymentIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    @Autowired
    private lateinit var scheduleJpaRepository: ScheduleJpaRepository

    @Autowired
    private lateinit var seatJpaRepository: SeatJpaRepository

    @Autowired
    private lateinit var reservationJpaRepository: ReservationJpaRepository

    companion object {
        @Container
        private val redisContainer = GenericContainer("redis:5.0.3-alpine").apply {
            withExposedPorts(6379)
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379).toString() }
        }
    }

    private lateinit var user: UserEntity
    private lateinit var schedule: ScheduleEntity
    private lateinit var seat: SeatEntity

    @BeforeEach
    fun setUp() {
        // 테스트용 사용자와 스케줄, 좌석 데이터 생성
        user = userJpaRepository.save(UserEntity(userId = "test-user", balance = BigDecimal("200000")))

        // 스케줄 생성
        schedule = scheduleJpaRepository.save(
            ScheduleEntity(
                title = "테스트 공연",
                startDateTime = LocalDateTime.now().plusDays(1)
            )
        )

        // 좌석 생성
        seat = seatJpaRepository.save(
            SeatEntity(
                schedule = schedule,
                seatNumber = 1,
                price = BigDecimal("100000")
            )
        )
    }

    @Test
    @DisplayName("[E2E] 전체 예약 및 결제 성공 시나리오")
    fun `full reservation and payment success scenario`() {
        // 1. 좌석 예약 API 호출
        val reservationRequest = ReservationRequest(user.userId, schedule.scheduleId, seat.seatNumber)

        val reservationResult = mockMvc.post("/api/reservations") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(reservationRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.status") { value("PENDING") }
        }.andReturn()

        val reservationResponse = objectMapper.readTree(reservationResult.response.contentAsString)
        val reservationId = reservationResponse.get("reservationId").asLong()

        // 2. 결제 API 호출
        val paymentRequest = PaymentRequest(user.userId, reservationId)

        mockMvc.post("/api/payments") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(paymentRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.status") { value("COMPLETED") }
            jsonPath("$.amount") { value(seat.price) }
        }

        // 3. DB 상태 검증
        val updatedSeat = seatJpaRepository.findById(seat.seatId).orElseThrow()
        assertEquals(SeatStatusEntity.SOLD, updatedSeat.status)

        val updatedUser = userJpaRepository.findById(user.id!!).orElseThrow()
        assertEquals(0, updatedUser.balance.compareTo(BigDecimal("100000")))

        val reservation = reservationJpaRepository.findById(reservationId).orElseThrow()
        assertEquals(ReservationStatusEntity.PAID, reservation.status)
    }

    @Test
    @DisplayName("[E2E] 잔액 부족으로 결제 실패 시나리오")
    fun `payment failure scenario due to insufficient balance`() {
        // Given: 잔액이 부족한 사용자
        val poorUser = userJpaRepository.save(UserEntity(userId = "poor-user", balance = BigDecimal("50000")))

        // 1. 좌석 예약 API 호출
        val reservationRequest = ReservationRequest(poorUser.userId, schedule.scheduleId, seat.seatNumber)
        val reservationResult = mockMvc.post("/api/reservations") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(reservationRequest)
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val reservationResponse = objectMapper.readTree(reservationResult.response.contentAsString)
        val reservationId = reservationResponse.get("reservationId").asLong()

        // 2. 결제 API 호출 -> 잔액 부족(400 Bad Request) 예상
        val paymentRequest = PaymentRequest(poorUser.userId, reservationId)
        mockMvc.post("/api/payments") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(paymentRequest)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") { value("잔액이 부족합니다.") }
        }

        // 3. DB 상태 검증 (롤백되었는지 확인)
        val finalSeat = seatJpaRepository.findById(seat.seatId).orElseThrow()
        assertEquals(SeatStatusEntity.RESERVED, finalSeat.status)

        val finalUser = userJpaRepository.findById(poorUser.id!!).orElseThrow()
        assertEquals(0, finalUser.balance.compareTo(BigDecimal("50000")))
    }

    @Test
    @DisplayName("[E2E] 예약 만료 후 결제 시도 시나리오")
    fun `payment attempt after reservation expiration`() {
        // 1. 좌석 예약 API 호출
        val reservationRequest = ReservationRequest(user.userId, schedule.scheduleId, seat.seatNumber)
        val reservationResult = mockMvc.post("/api/reservations") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(reservationRequest)
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val reservationResponse = objectMapper.readTree(reservationResult.response.contentAsString)
        val reservationId = reservationResponse.get("reservationId").asLong()

        // 2. 수동으로 예약 만료 처리 (테스트 목적)
        val reservation = reservationJpaRepository.findById(reservationId).orElseThrow()
        reservation.status = ReservationStatusEntity.EXPIRED
        reservationJpaRepository.save(reservation)

        // 3. 결제 API 호출 -> 예약 만료(400 Bad Request) 예상
        val paymentRequest = PaymentRequest(user.userId, reservationId)
        mockMvc.post("/api/payments") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(paymentRequest)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") { value("예약이 만료되었습니다.") }
        }

        // 4. DB 상태 확인
        val finalSeat = seatJpaRepository.findById(seat.seatId).orElseThrow()
        assertEquals(SeatStatusEntity.RESERVED, finalSeat.status) // 좌석 상태는 예약됨 상태로 유지

        val finalUser = userJpaRepository.findById(user.id!!).orElseThrow()
        assertEquals(0, finalUser.balance.compareTo(BigDecimal("200000"))) // 사용자 잔액 변동 없음
    }

    @Test
    @DisplayName("[E2E] 동시에 같은 좌석 예약 시도 시나리오")
    fun `concurrent reservation attempts for the same seat`() {
        // 동시 요청을 위한 설정
        val numberOfThreads = 5
        val executor = Executors.newFixedThreadPool(numberOfThreads)
        val latch = CountDownLatch(numberOfThreads)
        val users = (1..numberOfThreads).map {
            userJpaRepository.save(UserEntity(userId = "user-$it", balance = BigDecimal("200000")))
        }

        var successCount = 0
        var failureCount = 0

        // 여러 스레드에서 동시에 같은 좌석 예약
        users.forEachIndexed { index, userEntity ->
            executor.submit {
                try {
                    val reservationRequest = ReservationRequest(userEntity.userId, schedule.scheduleId, seat.seatNumber)
                    val result = mockMvc.post("/api/reservations") {
                        contentType = MediaType.APPLICATION_JSON
                        content = objectMapper.writeValueAsString(reservationRequest)
                    }.andReturn()

                    if (result.response.status == 200) {
                        synchronized(this) { successCount++ }
                    } else {
                        synchronized(this) { failureCount++ }
                    }
                } catch (e: Exception) {
                    synchronized(this) { failureCount++ }
                } finally {
                    latch.countDown()
                }
            }
        }

        // 모든 스레드 완료 대기
        latch.await()
        executor.shutdown()

        // 검증: 정확히 한 명만 성공해야 함
        assertEquals(1, successCount, "한 명의 사용자만 예약에 성공해야 합니다")
        assertEquals(numberOfThreads - 1, failureCount, "나머지 사용자는 모두 실패해야 합니다")

        // 좌석 상태 확인
        val finalSeat = seatJpaRepository.findById(seat.seatId).orElseThrow()
        assertEquals(SeatStatusEntity.RESERVED, finalSeat.status)
    }
}