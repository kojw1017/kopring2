package com.example.tdd.adapter.`in`.web

import com.example.tdd.application.port.`in`.ReservationCommand
import com.example.tdd.application.port.`in`.SeatReservationUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

/**
 * 좌석 예약 컨트롤러
 */
@RestController
@RequestMapping("/api/reservations")
class ReservationController(
    private val seatReservationUseCase: SeatReservationUseCase
) {

    /**
     * 좌석 예약 요청 API
     */
    @PostMapping
    fun reserveSeat(@RequestBody request: ReservationRequest): ResponseEntity<ReservationResponse> {
        // 요청 데이터를 Command 객체로 변환
        val command = ReservationCommand(
            userId = request.userId,
            scheduleId = request.scheduleId,
            seatNumber = request.seatNumber
        )

        // 유스케이스 호출
        val result = seatReservationUseCase.reserveSeat(command)

        // 응답 생성
        return ResponseEntity.ok(
            ReservationResponse(
                reservationId = result.reservationId,
                seatNumber = result.seatNumber,
                status = result.status,
                expiresAt = result.expiresAt
            )
        )
    }
}

/**
 * 좌석 예약 요청 DTO
 */
data class ReservationRequest(
    val userId: String,
    val scheduleId: Long,
    val seatNumber: Int
)

/**
 * 좌석 예약 응답 DTO
 */
data class ReservationResponse(
    val reservationId: Long,
    val seatNumber: Int,
    val status: String,
    val expiresAt: LocalDateTime
)
