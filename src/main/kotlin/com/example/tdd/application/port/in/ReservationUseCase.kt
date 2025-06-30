package com.example.tdd.application.port.`in`

/**
 * 예약 관련 인바운드 포트
 */
interface ReservationUseCase {
    /**
     * 좌석을 예약합니다.
     */
    fun reserveSeat(command: ReserveSeatCommand): ReservationResponse

    /**
     * 예약을 취소합니다.
     */
    fun cancelReservation(command: CancelReservationCommand)

    /**
     * 사용자의 예약 목록을 조회합니다.
     */
    fun getUserReservations(userId: String): List<ReservationResponse>
}

/**
 * 좌석 예약 명령
 */
data class ReserveSeatCommand(
    val userId: String,
    val seatId: Long,
    val token: String
)

/**
 * 예약 취소 명령
 */
data class CancelReservationCommand(
    val reservationId: Long,
    val userId: String
)
