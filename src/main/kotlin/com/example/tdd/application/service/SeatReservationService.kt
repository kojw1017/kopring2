package com.example.tdd.application.service

import com.example.tdd.domain.exception.SeatAlreadyReservedException
import com.example.tdd.domain.exception.SeatNotFoundException
import com.example.tdd.domain.exception.ScheduleNotFoundException
import com.example.tdd.application.port.`in`.ReservationCommand
import com.example.tdd.application.port.`in`.ReservationResponse
import com.example.tdd.application.port.`in`.SeatReservationUseCase
import com.example.tdd.application.port.out.LockManagerRepository
import com.example.tdd.application.port.out.ReservationRepository
import com.example.tdd.application.port.out.SeatRepository
import com.example.tdd.application.port.out.ScheduleRepository
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
    private val seatRepository: SeatRepository,
    private val reservationRepository: ReservationRepository,
    private val scheduleRepository: ScheduleRepository,
    private val lockManager: LockManagerRepository,
    @Value("\${queue.temporary-reservation-minutes:5}")
    private val tempReservationMinutes: Int = 5
) : SeatReservationUseCase {

    /**
     * 좌석 예약을 요청합니다.
     *
     * @throws SeatAlreadyReservedException 다른 사용자가 이미 예약한 경우
     * @throws SeatNotFoundException 요청한 좌석을 찾을 수 없는 경우
     * @throws ConcurrentModificationException 동시 접근으로 인한 충돌 발생
     */
    @Transactional
    override fun reserveSeat(command: ReservationCommand): ReservationResponse {
        val lockKey = "seat:${command.scheduleId}:${command.seatNumber}"
        val lockOwner = command.userId

        // 분산 락 획득 시도
        val lockAcquired = lockManager.acquireLock(lockKey, lockOwner, TimeUnit.SECONDS.toMillis(10))
        if (!lockAcquired) {
            throw ConcurrentModificationException("다른 사용자가 동일한 좌석을 예약 중입니다.")
        }

        try {
            // 좌석 조회 (scheduleId와 seatNumber로 조회)
            val seat = seatRepository.findByScheduleIdAndSeatNumber(command.scheduleId, command.seatNumber)
                ?: throw SeatNotFoundException("유효하지 않은 좌석입니다.")

            // 일정 조회
            val schedule = scheduleRepository.findById(command.scheduleId)
                ?: throw ScheduleNotFoundException("콘서트 일정을 찾을 수 없습니다.")

            // 임시 예약 ID 생성
            val reservationId = System.currentTimeMillis()

            // 도메인 서비스를 통한 좌석 예약
            val reservation = reservationService.createReservation(
                userId = command.userId,
                seat = seat,
                reservationId = reservationId
            )

            // 저장소에 예약 정보 저장
            val savedReservation = reservationRepository.save(reservation)
            val updatedSeat = seatRepository.save(seat)

            return ReservationResponse(
                reservationId = savedReservation.reservationId,
                userId = savedReservation.userId,
                seatId = updatedSeat.seatId,
                seatNumber = updatedSeat.seatNumber,
                concertName = schedule.concertName,
                concertDate = schedule.concertDate,
                price = updatedSeat.price,
                status = savedReservation.status.name,
                expiresAt = savedReservation.expiresAt
            )
        } finally {
            // 락 해제
            lockManager.releaseLock(lockKey, lockOwner)
        }
    }
}
