package com.example.tdd.adapter.`in`.web

import com.example.tdd.adapter.out.redis.RedisLockManager
import com.example.tdd.application.port.`in`.QueueTokenResponse
import com.example.tdd.domain.model.ConcertDate
import com.example.tdd.domain.model.Seat
import com.example.tdd.domain.repository.ConcertDateRepository
import com.example.tdd.domain.repository.SeatRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ConcertReservationConcurrencyTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var concertDateRepository: ConcertDateRepository

    @Autowired
    private lateinit var seatRepository: SeatRepository

    @Autowired
    private lateinit var redisLockManager: RedisLockManager

    private lateinit var concertDate: ConcertDate
    private lateinit var seats: List<Seat>

    @BeforeEach
    fun setup() {
        // 테스트용 콘서트 날짜 생성
        concertDate = ConcertDate.create(
            date = LocalDate.now().plusDays(7),
            name = "동시성 테스트 콘서트",
            price = 50000L,
            totalSeats = 1 // 동시성 테스트를 위해 좌석 1개만 생성
        )
        val savedConcertDate = concertDateRepository.save(concertDate)

        // 테스트용 좌석 생성 (1개만)
        val seat = Seat.create(savedConcertDate.id, 1)
        seats = listOf(seatRepository.save(seat))
    }

    @Test
    @DisplayName("동시에 여러 사용자가 같은 좌석을 예약할 때 한 명만 성공해야 함")
    fun testConcurrentSeatReservation() {
        val concurrentUsers = 5
        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)
        val latch = CountDownLatch(concurrentUsers)
        val executor = Executors.newFixedThreadPool(concurrentUsers)

        // 모든 사용자의 토큰 미리 발급
        val userTokens = (1..concurrentUsers).map {
            val tokenResponse = mockMvc.perform(
                post("/api/queue/token")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

            objectMapper.readValue(tokenResponse, QueueTokenResponse::class.java)
        }

        // 동시에 좌석 예약 시도
        userTokens.forEach { tokenResponse ->
            executor.execute {
                try {
                    val reserveRequest = ReserveSeatRequest(
                        token = tokenResponse.token,
                        concertDateId = concertDate.id,
                        seatNumber = 1 // 모두 같은 좌석을 예약 시도
                    )

                    val result = mockMvc.perform(
                        post("/api/concerts/reserve")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(reserveRequest))
                    )

                    if (result.andReturn().response.status == 200) {
                        successCount.incrementAndGet()
                    } else {
                        failCount.incrementAndGet()
                    }
                } catch (e: Exception) {
                    failCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        // 모든 스레드 완료 대기
        latch.await(10, TimeUnit.SECONDS)
        executor.shutdown()

        // 검증: 정확히 1명만 예약에 성공해야 함
        assertEquals(1, successCount.get(), "정확히 한 명의 사용자만 좌석 예약에 성공해야 합니다.")
        assertEquals(concurrentUsers - 1, failCount.get(), "나머지 사용자는 예약에 실패해야 합니다.")

        // 데이터베이스에서 직접 확인
        val updatedConcertDate = concertDateRepository.findById(concertDate.id)
        assertEquals(0, updatedConcertDate?.availableSeats, "가용 좌석 수가 정확히 0이어야 합니다.")
    }
}
