package com.example.tdd.application.service

import com.example.tdd.application.exception.PaymentException
import com.example.tdd.application.exception.QueueException
import com.example.tdd.application.exception.ReservationException
import com.example.tdd.application.exception.ResourceNotFoundException
import com.example.tdd.application.port.`in`.PaymentResponse
import com.example.tdd.application.port.`in`.PaymentUseCase
import com.example.tdd.application.port.`in`.ProcessPaymentCommand
import com.example.tdd.domain.model.Payment
import com.example.tdd.domain.model.SeatStatus
import com.example.tdd.domain.repository.PaymentRepository
import com.example.tdd.domain.repository.QueueTokenRepository
import com.example.tdd.domain.repository.SeatRepository
import com.example.tdd.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val seatRepository: SeatRepository,
    private val userRepository: UserRepository,
    private val queueTokenRepository: QueueTokenRepository
) : PaymentUseCase {

    @Transactional
    override fun processPayment(command: ProcessPaymentCommand): PaymentResponse {
        // 대기열 토큰 검증
        val queueToken = queueTokenRepository.findByToken(command.token)
            ?: throw QueueException("대기열 토큰을 찾을 수 없습니다.")

        // 좌석 조회
        val seat = seatRepository.findById(command.reservationId)
            ?: throw ResourceNotFoundException("Seat", command.reservationId.toString())

        // 좌석이 임시 예약 상태인지 확인
        if (seat.status != SeatStatus.TEMPORARY_RESERVED) {
            throw ReservationException("임시 예약 상태의 좌석이 아닙니다.")
        }

        // 임시 예약이 만료되었는지 확인
        if (seat.isTemporaryReservationExpired()) {
            // 만료된 경우 좌석 상태를 가용 상태로 변경
            seatRepository.save(seat.cancelTemporaryReservation())
            throw ReservationException("임시 예약이 만료되었습니다.")
        }

        // 임시 예약한 사용자와 동일한 사용자인지 확인
        if (seat.reservedBy != queueToken.userId) {
            throw ReservationException("해당 좌석을 예약한 사용자가 아닙니다.")
        }

        // 사용자 조회
        val user = userRepository.findById(queueToken.userId)
            ?: throw ResourceNotFoundException("User", queueToken.userId.toString())

        // 콘서트 데이터에서 가격 가져오기
        // 여기서는 간단히 하기 위해 좌석 번호 * 10000으로 가격 책정
        val amount = seat.seatNumber * 10000L

        // 결제 생성
        val payment = Payment.create(queueToken.userId, seat.id, amount)

        try {
            // 사용자 잔액 확인 및 결제
            val paidUser = user.pay(amount)
            userRepository.save(paidUser)

            // 결제 완료 처리
            val completedPayment = payment.complete()
            val savedPayment = paymentRepository.save(completedPayment)

            // 좌석 예약 확정
            val confirmedSeat = seat.confirmReservation()
            seatRepository.save(confirmedSeat)

            // 대기열 토큰 만료 처리
            queueTokenRepository.save(queueToken.expire())

            return PaymentResponse(
                paymentId = savedPayment.id,
                userId = savedPayment.userId,
                concertDateId = seat.concertDateId,
                seatNumber = seat.seatNumber,
                amount = savedPayment.amount,
                status = savedPayment.status.name,
                completedAt = savedPayment.completedAt
            )
        } catch (e: IllegalArgumentException) {
            // 잔액 부족 등의 오류 처리
            val failedPayment = payment.fail()
            paymentRepository.save(failedPayment)
            throw PaymentException("결제에 실패했습니다: ${e.message}", e)
        }
    }
}
