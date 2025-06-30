package com.example.tdd.domain.service

import com.example.tdd.domain.exception.ReservationExpiredException
import com.example.tdd.domain.exception.InvalidRequestException
import com.example.tdd.domain.exception.InsufficientBalanceException
import com.example.tdd.domain.model.Payment
import com.example.tdd.domain.model.Reservation
import com.example.tdd.domain.model.Seat
import com.example.tdd.domain.model.User
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * 결제 관련 도메인 서비스
 * 결제 처리 및 좌석/예약 상태 관리를 담당합니다.
 */
@Service
class PaymentService {

    /**
     * 결제를 진행합니다.
     * 사용자 잔액을 확인하고 차감하며, 좌석과 예약 상태를 업데이트합니다.
     *
     * @param user 결제를 진행하는 사용자
     * @param reservation 결제할 예약
     * @param seat 예약된 좌석
     * @param paymentId 생성할 결제 ID
     * @return 생성된 결제 객체
     * @throws ReservationExpiredException 예약이 만료된 경우
     * @throws InvalidRequestException 이미 결제된 경우
     * @throws InsufficientBalanceException 잔액이 부족한 경우
     */
    fun processPayment(user: User, reservation: Reservation, seat: Seat, paymentId: Long): Payment {
        // 예약 상태 확인
        if (reservation.isExpired()) {
            throw ReservationExpiredException("만료된 예약은 결제할 수 없습니다.")
        }

        // 결제 처리 - 각 도메인 모델 내부에서 적절한 예외 발생
        user.pay(seat.price)  // InsufficientBalanceException 발생 가능
        seat.sell()           // InvalidRequestException 발생 가능
        reservation.complete() // InvalidRequestException 또는 ReservationExpiredException 발생 가능

        // 결제 객체 생성 및 반환
        return Payment(
            paymentId = paymentId,
            reservationId = reservation.reservationId,
            amount = seat.price,
            paymentDate = LocalDateTime.now()
        )
    }
}
