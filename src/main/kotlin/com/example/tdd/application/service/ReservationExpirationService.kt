package com.example.tdd.application.service

import com.example.tdd.adapter.`in`.web.exception.ConcurrentModificationException
import com.example.tdd.adapter.`in`.web.exception.ResourceNotFoundException
import com.example.tdd.application.port.out.ReservationRepositoryPort
import com.example.tdd.application.port.out.SeatRepositoryPort
import com.example.tdd.domain.model.ReservationStatus
import com.example.tdd.domain.service.ReservationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 예약 만료 처리 서비스
 * 만료된 예약을 찾아 처리하는 기능을 제공합니다.
 */
@Service
class ReservationExpirationService(
    private val reservationRepository: ReservationRepositoryPort,
    private val seatRepository: SeatRepositoryPort,
    private val reservationService: ReservationService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 만료된 예약을 찾아 처리합니다.
     */
    @Transactional
    fun processExpiredReservations() {
        val currentTime = LocalDateTime.now()
        val expiredReservations = reservationRepository.findExpiredReservations(currentTime)

        logger.info("만료된 예약 수: ${expiredReservations.size}")

        for (reservation in expiredReservations) {
            if (reservation.status == ReservationStatus.PENDING) {
                try {
                    // 좌석 정보 조회
                    val seat = seatRepository.findById(reservation.seatId)
                    if (seat == null) {
                        logger.warn("좌석 정보를 찾을 수 없음: seatId=${reservation.seatId}")
                        continue  // 다음 예약으로 넘어감
                    }

                    // 도메인 서비스를 통해 만료 처리
                    reservationService.handleExpiredReservation(reservation, seat)

                    // 변경된 상태 저장
                    reservationRepository.updateStatus(reservation.reservationId, reservation.status)
                    seatRepository.updateStatus(seat.seatId, seat.status)

                    logger.info("예약 만료 처리: reservationId=${reservation.reservationId}, seatId=${seat.seatId}")
                } catch (e: ConcurrentModificationException) {
                    logger.warn("예약 만료 처리 중 동시성 문제 발생: ${e.message}")
                } catch (e: ResourceNotFoundException) {
                    logger.warn("예약 만료 처리 중 리소스 찾을 수 없음: ${e.message}")
                } catch (e: Exception) {
                    logger.error("예약 만료 처리 중 오류 발생: ${e.message}", e)
                }
            }
        }
    }
}
