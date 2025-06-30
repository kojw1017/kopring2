package com.example.tdd.domain.service

import com.example.tdd.domain.model.Reservation
import com.example.tdd.domain.model.Seat
import com.example.tdd.domain.exception.SeatAlreadyReservedException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * 예약 관련 도메인 서비스
 * 복잡한 예약 비즈니스 로직을 처리합니다.
 */
@Service
class ReservationService {

    /**
     * 좌석 예약을 생성합니다.
     *
     * @param userId 사용자 ID
     * @param seat 예약할 좌석
     * @param reservationId 예약 ID
     * @return 생성된 예약 객체
     * @throws SeatAlreadyReservedException 좌석이 이미 예약된 경우
     */
    fun createReservation(userId: String, seat: Seat, reservationId: Long): Reservation {
        // 좌석 예약 상태 변경
        seat.reserve()

        // 예약 생성 (5분 후 만료)
        val expiresAt = LocalDateTime.now().plusMinutes(5)

        return Reservation(
            reservationId = reservationId,
            userId = userId,
            seatId = seat.seatId,
            expiresAt = expiresAt
        )
    }

    /**
     * 예약을 취소합니다.
     *
     * @param reservation 취소할 예약
     * @param seat 예약된 좌석
     */
    fun cancelReservation(reservation: Reservation, seat: Seat) {
        // 예약 만료 처리
        reservation.expire()

        // 좌석 상태를 예약 가능으로 변경
        seat.cancelReservation()
    }

    /**
     * 만료된 예약들을 처리합니다.
     *
     * @param expiredReservations 만료된 예약 목록
     * @param seats 관련 좌석 목록
     */
    fun processExpiredReservations(expiredReservations: List<Reservation>, seats: List<Seat>) {
        expiredReservations.forEach { reservation ->
            reservation.expire()

            // 관련 좌석 찾아서 예약 취소
            seats.find { it.seatId == reservation.seatId }?.cancelReservation()
        }
    }
}
