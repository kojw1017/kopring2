package com.example.tdd.application.service

import com.example.tdd.application.port.out.ReservationRepository
import com.example.tdd.application.port.out.SeatRepository
import com.example.tdd.domain.service.ReservationService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 예약 만료 처리 서비스
 * 주기적으로 만료된 예약을 처리합니다.
 */
@Service
class ReservationExpirationService(
    private val reservationRepository: ReservationRepository,
    private val seatRepository: SeatRepository,
    private val reservationService: ReservationService
) {

    /**
     * 매 30초마다 만료된 예약을 확인하고 처리합니다.
     */
    @Scheduled(fixedDelay = 30000) // 30초마다 실행
    @Transactional
    fun processExpiredReservations() {
        val currentTime = LocalDateTime.now()
        val expiredReservations = reservationRepository.findExpiredReservations(currentTime)

        if (expiredReservations.isEmpty()) {
            return
        }

        // 만료된 예약과 관련된 좌석들 조회
        val seatIds = expiredReservations.map { it.seatId }
        val seats = seatIds.mapNotNull { seatRepository.findById(it) }

        // 도메인 서비스를 통한 만료 처리
        reservationService.processExpiredReservations(expiredReservations, seats)

        // 변경사항 저장
        expiredReservations.forEach { reservation ->
            reservationRepository.save(reservation)
        }

        seats.forEach { seat ->
            seatRepository.save(seat)
        }

        println("처리된 만료 예약 수: ${expiredReservations.size}")
    }
}
