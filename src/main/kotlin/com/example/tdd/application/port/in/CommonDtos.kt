package com.example.tdd.application.port.`in`

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 공통으로 사용되는 DTO 클래스들
 */

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
    val seatId: Long,
    val seatNumber: Int,
    val status: String,
    val price: BigDecimal
)

/**
 * 예약 응답 데이터
 */
data class ReservationResponse(
    val reservationId: Long,
    val userId: String,
    val seatId: Long,
    val seatNumber: Int,
    val concertName: String,
    val concertDate: LocalDateTime,
    val price: BigDecimal,
    val status: String,
    val expiresAt: LocalDateTime
)

/**
 * 결제 응답 데이터
 */
data class PaymentResponse(
    val paymentId: Long,
    val reservationId: Long,
    val userId: String,
    val amount: BigDecimal,
    val paymentDate: LocalDateTime,
    val status: String
)
