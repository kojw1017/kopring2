package com.example.tdd.application.service

import com.example.tdd.adapter.`in`.web.exception.ConcurrentModificationException
import com.example.tdd.adapter.`in`.web.exception.ResourceNotFoundException
import com.example.tdd.application.port.`in`.ReservationCommand
import com.example.tdd.application.port.`in`.ReservationResponse
import com.example.tdd.application.port.`in`.SeatReservationUseCase
import com.example.tdd.application.port.out.LockManagerPort
import com.example.tdd.application.port.out.ReservationRepositoryPort
import com.example.tdd.application.port.out.SeatRepositoryPort
import com.example.tdd.domain.service.ReservationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

/**
 * 좌석 예약 서비스
 * 사용자의 좌석 예약 요청을 처리합니다.
 */
@Service
class SeatReservationService(
    private val reservationService: ReservationService,
    private val seatRepository: SeatRepositoryPort,
    private val reservationRepository: ReservationRepositoryPort,
    private val lockManager: LockManagerPort,
    @Value("\${queue.temporary-reservation-minutes}")
    private val tempReservationMinutes: Int
) : SeatReservationUseCase {

    /**
     * 좌석 예약을 요청합니다.
     * 분산 락을 사용하여 동시 예약을 방지합니다.
     *
     * @throws ConcurrentModificationException 다른 사용자가 같은 좌석을 선택 중이거나 이미 예약된 경우
     * @throws ResourceNotFoundException 요청한 좌석을 찾을 수 없는 경우
     */
    @Transactional
    override fun reserveSeat(command: ReservationCommand): ReservationResponse {
        val lockKey = "seat:${command.scheduleId}:${command.seatNumber}"
        val ownerId = command.userId

        // 분산 락 획득 시도 (3초 타임아웃)
        val lockAcquired = lockManager.acquireLock(lockKey, ownerId, TimeUnit.SECONDS.toMillis(3))

        if (!lockAcquired) {
            throw ConcurrentModificationException("현재 다른 사용자가 해당 좌석을 선택 중입니다. 잠시 후 다시 시도해주세요.",
                path = "/api/reservations")
        }

        try {
            // 좌석 조회
            val seat = seatRepository.findByScheduleIdAndSeatNumber(command.scheduleId, command.seatNumber)
                ?: throw ResourceNotFoundException("유효하지 않은 좌석입니다.", path = "/api/reservations")

            // 임시 예약 ID 생성 (실제 구현에서는 DB 시퀀스 등을 사용)
            val reservationId = System.currentTimeMillis()

            // 도메인 서비스를 통한 좌석 예약
            // ConcurrentModificationException이 발생할 수 있음 (이미 예약된 좌석)
            val reservation = reservationService.reserveSeat(
                userId = command.userId,
                seat = seat,
                reservationId = reservationId,
                expirationMinutes = tempReservationMinutes
            )

            // 저장소에 예약 정보 저장
            val savedReservation = reservationRepository.save(reservation)
            val updatedSeat = seatRepository.save(seat)

            return ReservationResponse(
                reservationId = savedReservation.reservationId,
                seatNumber = updatedSeat.seatNumber,
                status = savedReservation.status.name,
                expiresAt = savedReservation.expiresAt
            )
        } finally {
            // 락 해제
            lockManager.releaseLock(lockKey, ownerId)
        }
    }
}
