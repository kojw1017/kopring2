package com.example.tdd.adapter.out.scheduler

import com.example.tdd.domain.model.Seat
import com.example.tdd.domain.repository.ConcertDateRepository
import com.example.tdd.domain.repository.SeatRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 만료된 임시 예약을 자동으로 취소하는 스케줄러
 */
@Component
class ReservationExpiryScheduler(
    private val seatRepository: SeatRepository,
    private val concertDateRepository: ConcertDateRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 1분마다 만료된 임시 예약을 확인하고 취소합니다.
     */
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    fun cancelExpiredReservations() {
        log.info("만료된 임시 예약 취소 작업 시작")

        val expiredReservations = seatRepository.findAllExpiredTemporaryReservations()
        log.info("만료된 임시 예약 수: {}", expiredReservations.size)

        if (expiredReservations.isEmpty()) {
            return
        }

        expiredReservations.forEach { seat ->
            // 좌석 상태를 가용 상태로 변경
            val canceledSeat = seat.cancelTemporaryReservation()
            seatRepository.save(canceledSeat)

            // 콘서트 가용 좌석 수 증가
            val concertDate = concertDateRepository.findById(seat.concertDateId)
            if (concertDate != null) {
                val updatedConcertDate = concertDate.cancelSeatReservation()
                concertDateRepository.save(updatedConcertDate)
            }

            log.info("만료된 임시 예약 취소: 좌석 ID = {}, 콘서트 날짜 ID = {}, 좌석 번호 = {}",
                seat.id, seat.concertDateId, seat.seatNumber)
        }

        log.info("만료된 임시 예약 취소 작업 완료")
    }
}
