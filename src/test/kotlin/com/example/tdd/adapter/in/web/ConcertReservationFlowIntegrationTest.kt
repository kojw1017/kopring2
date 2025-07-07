package com.example.tdd.adapter.`in`.web

import com.example.tdd.IntegrationTest
import com.example.tdd.application.port.`in`.*
import com.example.tdd.domain.model.ConcertDate
import com.example.tdd.domain.model.Seat
import com.example.tdd.domain.model.User
import com.example.tdd.domain.repository.ConcertDateRepository
import com.example.tdd.domain.repository.SeatRepository
import com.example.tdd.domain.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.test.annotation.DirtiesContext

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ConcertReservationFlowIntegrationTest : IntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var concertDateRepository: ConcertDateRepository

    @Autowired
    private lateinit var seatRepository: SeatRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var concertDate: ConcertDate
    private lateinit var seats: List<Seat>

    @BeforeEach
    fun setup() {
        // 테스트용 콘서트 날짜 생성
        concertDate = ConcertDate.create(
            date = LocalDate.now().plusDays(7),
            name = "테스트 콘서트",
            price = 50000L,
            totalSeats = 5
        )
        val savedConcertDate = concertDateRepository.save(concertDate)

        // 테스트용 좌석 생성
        val seatList = mutableListOf<Seat>()
        for (i in 1..5) {
            val seat = Seat.create(savedConcertDate.id, i)
            seatList.add(seat)
        }
        seats = seatRepository.saveAll(seatList)
    }

    @Test
    @DisplayName("전체 콘서트 예약 흐름 (대기열 → 좌석 예약 → 결제) 테스트")
    fun testFullReservationFlow() {
        // 1. 대기열 토큰 발급
        val tokenResponse = mockMvc.perform(
            post("/api/queue/token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.userId").exists())
            .andReturn()
            .response
            .contentAsString

        val queueTokenResponse = objectMapper.readValue(tokenResponse, QueueTokenResponse::class.java)
        val token = queueTokenResponse.token
        val userId = queueTokenResponse.userId

        // 2. 대기 상태 조회
        mockMvc.perform(
            get("/api/queue/status")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value(token))
            .andExpect(jsonPath("$.queueNumber").exists())
            .andDo(print())

        // 3. 잔액 충전
        val chargeRequest = ChargeBalanceRequest(
            userId = userId,
            amount = 100000L
        )

        mockMvc.perform(
            post("/api/balance/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chargeRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.balance").value(100000L))
            .andDo(print())

        // 4. 콘서트 날짜 조회
        val concertDatesResponse = mockMvc.perform(
            get("/api/concerts/dates")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        val concertDates = objectMapper.readValue(concertDatesResponse, Array<ConcertDateResponse>::class.java)
        assertNotEquals(0, concertDates.size)

        val targetConcertDate = concertDates.first()

        // 5. 좌석 조회
        val seatsResponse = mockMvc.perform(
            get("/api/concerts/dates/${targetConcertDate.id}/seats")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        val availableSeats = objectMapper.readValue(seatsResponse, Array<SeatResponse>::class.java)
        assertNotEquals(0, availableSeats.size)

        val targetSeat = availableSeats.first()

        // 6. 좌석 예약
        val reserveRequest = ReserveSeatRequest(
            token = token,
            concertDateId = targetConcertDate.id,
            seatNumber = targetSeat.seatNumber
        )

        val reservationResponse = mockMvc.perform(
            post("/api/concerts/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserveRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.reservationId").exists())
            .andExpect(jsonPath("$.seatNumber").value(targetSeat.seatNumber))
            .andDo(print())
            .andReturn()
            .response
            .contentAsString

        val reservation = objectMapper.readValue(reservationResponse, ReservationResponse::class.java)

        // 7. 결제 처리
        val paymentRequest = ProcessPaymentRequest(
            token = token,
            reservationId = reservation.reservationId
        )

        mockMvc.perform(
            post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("COMPLETED"))
            .andDo(print())

        // 8. 잔액 확인 (결제 후 잔액 감소 확인)
        mockMvc.perform(
            get("/api/balance/${userId}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.balance").value(100000L - targetConcertDate.price))
            .andDo(print())
    }
}
