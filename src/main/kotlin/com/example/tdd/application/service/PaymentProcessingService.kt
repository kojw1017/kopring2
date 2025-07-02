package com.example.tdd.application.service

import com.example.tdd.adapter.`in`.web.exception.ConcurrentModificationException
import com.example.tdd.adapter.`in`.web.exception.InvalidRequestException
import com.example.tdd.adapter.`in`.web.exception.ReservationExpiredException
import com.example.tdd.adapter.`in`.web.exception.ResourceNotFoundException
import com.example.tdd.application.port.`in`.PaymentCommand
import com.example.tdd.application.port.`in`.PaymentResponse
import com.example.tdd.application.port.`in`.PaymentUseCase
import com.example.tdd.application.port.out.LockManagerPort
import com.example.tdd.application.port.out.PaymentRepositoryPort
import com.example.tdd.application.port.out.QueueManagerPort
import com.example.tdd.application.port.out.ReservationRepositoryPort
import com.example.tdd.application.port.out.SeatRepositoryPort
import com.example.tdd.application.port.out.UserRepositoryPort
import com.example.tdd.domain.model.ReservationStatus
import com.example.tdd.domain.service.PaymentService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

/**
 * 결제 처리 서비스
 * 좌석 예약에 대한 결제를 처리합니다.
 */
@Service
class PaymentProcessingService(
    private val paymentService: PaymentService,
    private val userRepository: UserRepositoryPort,
    private val reservationRepository: ReservationRepositoryPort,
    private val seatRepository: SeatRepositoryPort,
    private val paymentRepository: PaymentRepositoryPort,
    private val queueManager: QueueManagerPort,
    private val lockManager: LockManagerPort
) : PaymentUseCase {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 예약에 대한 결제를 진행합니다.
     */
    @Transactional
    override fun processPayment(command: PaymentCommand): PaymentResponse {
        val lockKey = "payment:${command.reservationId}"
        val ownerId = command.userId

        log.info("결제 시도: userId={}, reservationId={}", command.userId, command.reservationId)

        if (!lockManager.acquireLock(lockKey, ownerId, TimeUnit.SECONDS.toMillis(3))) {
            log.warn("결제 락 획득 실패: 다른 결제 시도 진행 중. lockKey={}", lockKey)
            throw ConcurrentModificationException(
                "현재 해당 예약에 대한 다른 결제 시도가 진행 중입니다. 잠시 후 다시 시도해주세요.",
                path = "/api/payments"
            )
        }

        log.debug("결제 락 획득 성공: lockKey={}", lockKey)

        try {
            val reservation = reservationRepository.findById(command.reservationId)
                ?: throw ResourceNotFoundException("유효하지 않은 예약입니다.", path = "/api/payments")
            log.debug("예약 정보 조회 성공: reservationId={}", reservation.reservationId)

            if (reservation.userId != command.userId) {
                log.warn("결제 권한 없음: reservation.userId={}, command.userId={}", reservation.userId, command.userId)
                throw InvalidRequestException("해당 예약에 대한 결제 권한이 없습니다.", path = "/api/payments")
            }

            if (reservation.isExpired()) {
                log.warn("결제 실패: 예약 만료. reservationId={}, expiresAt={}", reservation.reservationId, reservation.expiresAt)
                throw ReservationExpiredException("예약이 만료되었습니다. 다시 예약해주세요.", path = "/api/payments")
            }

            if (reservation.status == ReservationStatus.PAID) {
                log.warn("결제 실패: 이미 결제 완료된 예약. reservationId={}", reservation.reservationId)
                throw InvalidRequestException("이미 결제가 완료된 예약입니다.", path = "/api/payments")
            }

            val user = userRepository.findByUserId(command.userId)
                ?: throw ResourceNotFoundException("사용자 정보를 찾을 수 없습니다.", path = "/api/payments")
            val seat = seatRepository.findById(reservation.seatId)
                ?: throw ResourceNotFoundException("좌석 정보를 찾을 수 없습니다.", path = "/api/payments")
            log.debug("사용자 및 좌석 정보 조회 성공: userId={}, seatId={}", user.userId, seat.seatId)

            val paymentId = System.currentTimeMillis()

            log.info("도메인 서비스 호출: processPayment. userId={}, reservationId={}, seatPrice={}", user.userId, reservation.reservationId, seat.price)
            val payment = paymentService.processPayment(
                user = user,
                reservation = reservation,
                seat = seat,
                paymentId = paymentId
            )

            val savedPayment = paymentRepository.save(payment)
            log.info("결제 정보 저장 성공: paymentId={}", savedPayment.paymentId)

            queueManager.deactivateUser(command.userId)
            log.info("사용자 대기열 비활성화: userId={}", command.userId)

            log.info("결제 성공: paymentId={}, reservationId={}", savedPayment.paymentId, savedPayment.reservationId)
            return PaymentResponse(
                paymentId = savedPayment.paymentId,
                reservationId = savedPayment.reservationId,
                amount = savedPayment.amount,
                status = "COMPLETED"
            )
        } catch (e: Exception) {
            log.error("결제 처리 중 예외 발생: reservationId={}", command.reservationId, e)
            throw e
        } finally {
            lockManager.releaseLock(lockKey, ownerId)
            log.debug("결제 락 해제: lockKey={}", lockKey)
        }
    }
}
