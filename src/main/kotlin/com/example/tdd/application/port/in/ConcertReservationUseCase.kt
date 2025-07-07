package com.example.tdd.application.port.`in`

import java.time.LocalDate
import java.util.UUID

/**
 * 콘서트 좌석 예약 유스케이스 인터페이스
 */
interface ConcertReservationUseCase {
    /**
     * 예약 가능한 콘서트 날짜 목록 조회
     */
    fun getAvailableConcertDates(): List<ConcertDateResponse>

    /**
     * 특정 날짜의 좌석 정보 조회
     */
    fun getAvailableSeats(concertDateId: UUID): List<SeatResponse>

    /**
     * 좌석 예약 요청 (임시 예약)
     */
    fun reserveSeat(command: ReserveSeatCommand): ReservationResponse
}

/**
 * 콘서트 날짜 응답 DTO
 */
data class ConcertDateResponse(
    val id: UUID,
    val date: LocalDate,
    val name: String,
    val price: Long,
    val availableSeats: Int,
    val totalSeats: Int
)

/**
 * 좌석 응답 DTO
 */
data class SeatResponse(
    val id: UUID,
    val seatNumber: Int,
    val status: String
)

/**
 * 좌석 예약 요청 커맨드 DTO
 */
data class ReserveSeatCommand(
    val token: String,
    val concertDateId: UUID,
    val seatNumber: Int
)

/**
 * 예약 응답 DTO
 */
data class ReservationResponse(
    val reservationId: UUID,
    val userId: UUID,
    val concertDateId: UUID,
    val seatNumber: Int,
    val price: Long,
    val expiresAt: String
)
