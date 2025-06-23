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

    /**
     * 예약에 대한 결제를 진행합니다.
     *
     * @throws ConcurrentModificationException 다른 결제 시도가 진행 중인 경우
     * @throws ResourceNotFoundException 예약, 사용자, 좌석 정보를 찾을 수 없는 경우
     * @throws InvalidRequestException 예약자와 결제 요청자가 불일치하거나 이미 결제된 경우
     * @throws ReservationExpiredException 예약이 만료된 경우
     */
    @Transactional
    override fun processPayment(command: PaymentCommand): PaymentResponse {
        val lockKey = "payment:${command.reservationId}"
        val ownerId = command.userId

        // 분산 락 획득 시도 (3초 타임아웃)
        val lockAcquired = lockManager.acquireLock(lockKey, ownerId, TimeUnit.SECONDS.toMillis(3))

        if (!lockAcquired) {
            throw ConcurrentModificationException(
                "현재 해당 예약에 대한 다른 결제 시도가 진행 중입니다. 잠시 후 다시 시도해주세요.",
                path = "/api/payments"
            )
        }

        try {
            // 예약 정보 조회
            val reservation = reservationRepository.findById(command.reservationId)
                ?: throw ResourceNotFoundException("유효하지 않은 예약입니다.", path = "/api/payments")

            // 예약자와 결제 요청자가 일치하는지 확인
            if (reservation.userId != command.userId) {
                throw InvalidRequestException("해당 예약에 대한 결제 권한이 없습니다.", path = "/api/payments")
            }

            // 예약 만료 여부 확인
            if (reservation.isExpired()) {
                throw ReservationExpiredException("예약이 만료되었습니다. 다시 예약해주세요.", path = "/api/payments")
            }

            // 이미 결제된 예약인지 확인
            if (reservation.status == ReservationStatus.PAID) {
                throw InvalidRequestException("이미 결제가 완료된 예약입니다.", path = "/api/payments")
            }

            // 사용자와 좌석 정보 조회
            val user = userRepository.findByUserId(command.userId)
                ?: throw ResourceNotFoundException("사용자 정보를 찾을 수 없습니다.", path = "/api/payments")

            val seat = seatRepository.findById(reservation.seatId)
                ?: throw ResourceNotFoundException("좌석 정보를 찾을 수 없습니다.", path = "/api/payments")

            // 결제 ID 생성 (실제 구현에서는 DB 시퀀스 등을 사용)
            val paymentId = System.currentTimeMillis()

            // 도메인 서비스를 통한 결제 처리
            // 여기서 InsufficientBalanceException, InvalidRequestException 등이 발생할 수 있음
            val payment = paymentService.processPayment(
                user = user,
                reservation = reservation,
                seat = seat,
                paymentId = paymentId
            )

            // 저장소에 결제 정보 저장
            val savedPayment = paymentRepository.save(payment)

            // 사용자 대기열 상태 비활성화 (이미 좌석을 예약했으므로)
            queueManager.deactivateUser(command.userId)

            return PaymentResponse(
                paymentId = savedPayment.paymentId,
                reservationId = savedPayment.reservationId,
                amount = savedPayment.amount,
                status = "COMPLETED"
            )
        } finally {
            // 락 해제
            lockManager.releaseLock(lockKey, ownerId)
        }
    }
}
