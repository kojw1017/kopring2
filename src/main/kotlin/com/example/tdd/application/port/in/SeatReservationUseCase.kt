package com.example.tdd.application.port.`in`

/**
 * 좌석 예약 유스케이스
 */
interface SeatReservationUseCase {
    /**
     * 좌석을 예약합니다.
     */
    fun reserveSeat(command: ReservationCommand): ReservationResponse
}

/**
 * 좌석 예약 요청 명령 데이터
 */
data class ReservationCommand(
    val userId: String,
    val scheduleId: Long,
    val seatNumber: Int,
    val token: String? = null
)
