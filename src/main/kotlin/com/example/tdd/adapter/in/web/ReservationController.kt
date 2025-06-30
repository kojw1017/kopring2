package com.example.tdd.adapter.`in`.web

import com.example.tdd.application.port.`in`.ReservationUseCase
import com.example.tdd.application.port.`in`.ReserveSeatCommand
import com.example.tdd.application.port.`in`.CancelReservationCommand
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 예약 관리 컨트롤러
 */
@RestController
@RequestMapping("/api/reservations")
class ReservationController(
    private val reservationUseCase: ReservationUseCase
) {

    /**
     * 좌석 예약 API
     */
    @PostMapping
    fun reserveSeat(
        @RequestBody request: ReserveSeatRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<ReservationDto> {
        val command = ReserveSeatCommand(
            userId = request.userId,
            seatId = request.seatId,
            token = token.removePrefix("Bearer ")
        )

        val reservation = reservationUseCase.reserveSeat(command)

        return ResponseEntity.ok(
            ReservationDto(
                reservationId = reservation.reservationId,
                userId = reservation.userId,
                seatNumber = reservation.seatNumber,
                concertName = reservation.concertName,
                concertDate = reservation.concertDate,
                price = reservation.price,
                status = reservation.status,
                expiresAt = reservation.expiresAt
            )
        )
    }

    /**
     * 예약 취소 API
     */
    @DeleteMapping("/{reservationId}")
    fun cancelReservation(
        @PathVariable reservationId: Long,
        @RequestParam userId: String
    ): ResponseEntity<Void> {
        val command = CancelReservationCommand(
            reservationId = reservationId,
            userId = userId
        )

        reservationUseCase.cancelReservation(command)
        return ResponseEntity.noContent().build()
    }

    /**
     * 사용자 예약 목록 조회 API
     */
    @GetMapping
    fun getUserReservations(@RequestParam userId: String): ResponseEntity<List<ReservationDto>> {
        val reservations = reservationUseCase.getUserReservations(userId)

        return ResponseEntity.ok(
            reservations.map { reservation ->
                ReservationDto(
                    reservationId = reservation.reservationId,
                    userId = reservation.userId,
                    seatNumber = reservation.seatNumber,
                    concertName = reservation.concertName,
                    concertDate = reservation.concertDate,
                    price = reservation.price,
                    status = reservation.status,
                    expiresAt = reservation.expiresAt
                )
            }
        )
    }
}

/**
 * 좌석 예약 요청 DTO
 */
data class ReserveSeatRequest(
    val userId: String,
    val seatId: Long
)

/**
 * 예약 정보 DTO
 */
data class ReservationDto(
    val reservationId: Long,
    val userId: String,
    val seatNumber: Int,
    val concertName: String,
    val concertDate: LocalDateTime,
    val price: BigDecimal,
    val status: String,
    val expiresAt: LocalDateTime
)
