package com.example.tdd.domain.service

import com.example.tdd.adapter.`in`.web.exception.ConcurrentModificationException
import com.example.tdd.domain.model.Reservation
import com.example.tdd.domain.model.ReservationStatus
import com.example.tdd.domain.model.Seat
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * 예약 관련 도메인 서비스
 * 좌석 예약 처리 및 상태 관리를 담당합니다.
 */
@Service
class ReservationService {

    /**
     * 좌석 예약을 처리합니다.
     * 좌석을 임시 예약 상태로 변경하고 새로운 예약을 생성합니다.
     *
     * @param userId 예약하는 사용자 ID
     * @param seat 예약할 좌석
     * @param reservationId 생성할 예약 ID
     * @param expirationMinutes 예약 만료 시간(분)
     * @return 생성된 예약 객체
     * @throws ConcurrentModificationException 좌석이 이미 예약되었거나 판매된 경우
     */
    fun reserveSeat(userId: String, seat: Seat, reservationId: Long, expirationMinutes: Int): Reservation {
        // 좌석을 예약 상태로 변경 - ConcurrentModificationException 발생 가능
        seat.reserve()

        // 예약 만료 시간 계산 (현재 시간 + 지정된 분)
        val expiresAt = LocalDateTime.now().plusMinutes(expirationMinutes.toLong())

        // 예약 객체 생성 및 반환
        return Reservation(
            reservationId = reservationId,
            userId = userId,
            seatId = seat.seatId,
            expiresAt = expiresAt
        )
    }

    /**
     * 만료된 예약을 처리합니다.
     * 예약 상태를 만료로 변경하고, 해당 좌석을 다시 예약 가능 상태로 변경합니다.
     *
     * @param reservation 처리할 예약
     * @param seat 예약에 연결된 좌석
     */
    fun handleExpiredReservation(reservation: Reservation, seat: Seat) {
        if (reservation.status == ReservationStatus.PENDING && reservation.isExpired()) {
            reservation.expire()  // InvalidRequestException 발생 가능
            seat.cancelReservation()  // InvalidRequestException 발생 가능
        }
    }
}
