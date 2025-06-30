package com.example.tdd.application.service

import com.example.tdd.application.port.`in`.PaymentUseCase
import com.example.tdd.application.port.`in`.ProcessPaymentCommand
import com.example.tdd.application.port.`in`.PaymentResponse
import com.example.tdd.application.port.out.*
import com.example.tdd.domain.service.PaymentService
import com.example.tdd.domain.exception.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 결제 처리 애플리케이션 서비스
 */
@Service
class PaymentProcessingService(
    private val paymentRepository: PaymentRepository,
    private val reservationRepository: ReservationRepository,
    private val seatRepository: SeatRepository,
    private val userRepository: UserRepository,
    private val queueTokenRepository: QueueTokenRepository,
    private val paymentService: PaymentService
) : PaymentUseCase {

    @Transactional
    override fun processPayment(command: ProcessPaymentCommand): PaymentResponse {
        // 토큰 유효성 검증
        if (!queueTokenRepository.isValidToken(command.token)) {
            throw InvalidTokenException("유효하지 않은 토큰입니다.")
        }

        // 중복 결제 방지
        if (paymentRepository.existsByReservationId(command.reservationId)) {
            throw InvalidRequestException("이미 결제된 예약입니다.")
        }

        // 엔티티 조회
        val user = userRepository.findById(command.userId)
            ?: throw UserNotFoundException("사용자를 찾을 수 없습니다.")

        val reservation = reservationRepository.findById(command.reservationId)
            ?: throw ReservationNotFoundException("예약을 찾을 수 없습니다.")

        if (reservation.userId != command.userId) {
            throw InvalidRequestException("본인의 예약만 결제할 수 있습니다.")
        }

        val seat = seatRepository.findById(reservation.seatId)
            ?: throw SeatNotFoundException("좌석을 찾을 수 없습니다.")

        // 도메인 서비스를 통한 결제 처리
        val payment = paymentService.processPayment(
            user = user,
            reservation = reservation,
            seat = seat,
            paymentId = generatePaymentId()
        )

        // 저장
        val savedPayment = paymentRepository.save(payment)
        userRepository.save(user)
        seatRepository.save(seat)
        reservationRepository.save(reservation)

        // 결제 완료 후 토큰 만료
        queueTokenRepository.expireToken(command.token)

        return PaymentResponse(
            paymentId = savedPayment.paymentId,
            reservationId = savedPayment.reservationId,
            userId = command.userId,
            amount = savedPayment.amount,
            paymentDate = savedPayment.paymentDate,
            status = "COMPLETED"
        )
    }

    @Transactional(readOnly = true)
    override fun getPayment(paymentId: Long): PaymentResponse {
        val payment = paymentRepository.findById(paymentId)
            ?: throw InvalidRequestException("결제 정보를 찾을 수 없습니다.")

        val reservation = reservationRepository.findById(payment.reservationId)!!

        return PaymentResponse(
            paymentId = payment.paymentId,
            reservationId = payment.reservationId,
            userId = reservation.userId,
            amount = payment.amount,
            paymentDate = payment.paymentDate,
            status = "COMPLETED"
        )
    }

    @Transactional(readOnly = true)
    override fun getUserPayments(userId: String): List<PaymentResponse> {
        val reservations = reservationRepository.findByUserId(userId)
        val payments = reservations.mapNotNull { reservation ->
            paymentRepository.findByReservationId(reservation.reservationId)
        }

        return payments.map { payment ->
            PaymentResponse(
                paymentId = payment.paymentId,
                reservationId = payment.reservationId,
                userId = userId,
                amount = payment.amount,
                paymentDate = payment.paymentDate,
                status = "COMPLETED"
            )
        }
    }

    private fun generatePaymentId(): Long {
        return System.currentTimeMillis()
    }
}
