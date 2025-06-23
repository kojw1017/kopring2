package com.example.tdd.application.port.`in`

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 콘서트 일정 및 좌석 조회를 위한 인바운드 포트
 */
interface ConcertQueryUseCase {
    /**
     * 사용 가능한 콘서트 일정 목록을 조회합니다.
     *
     * @return 콘서트 일정 목록
     */
    fun getAvailableDates(): List<ScheduleResponse>

    /**
     * 특정 콘서트 일정의 좌석 현황을 조회합니다.
     *
     * @param scheduleId 콘서트 일정 ID
     * @return 좌석 목록 및 상태
     */
    fun getSeats(scheduleId: Long): List<SeatResponse>
}

/**
 * 콘서트 일정 응답 데이터
 */
data class ScheduleResponse(
    val scheduleId: Long,
    val concertName: String,
    val concertDate: LocalDateTime
)

/**
 * 좌석 정보 응답 데이터
 */
data class SeatResponse(
    val seatNumber: Int,
    val status: String,
    val price: BigDecimal
)

/**
 * 좌석 예약을 위한 인바운드 포트
 */
interface SeatReservationUseCase {
    /**
     * 좌석 예약을 요청합니다.
     *
     * @param command 예약 요청 명령
     * @return 예약 결과
     */
    fun reserveSeat(command: ReservationCommand): ReservationResponse
}

/**
 * 좌석 예약 요청 명령 데이터
 */
data class ReservationCommand(
    val userId: String,
    val scheduleId: Long,
    val seatNumber: Int
)

/**
 * 좌석 예약 응답 데이터
 */
data class ReservationResponse(
    val reservationId: Long,
    val seatNumber: Int,
    val status: String,
    val expiresAt: LocalDateTime
)
