package com.example.tdd.adapter.out.persistence

import com.example.tdd.adapter.out.persistence.mapper.PersistenceMapper
import com.example.tdd.adapter.out.persistence.repository.ReservationJpaRepository
import com.example.tdd.adapter.out.persistence.repository.SeatJpaRepository
import com.example.tdd.application.port.out.ReservationRepository
import com.example.tdd.domain.model.Reservation
import com.example.tdd.domain.model.ReservationStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 예약 관련 영속성 어댑터
 * 아웃바운드 포트를 구현하여 도메인 모델과 데이터베이스 간의 상호작용을 담당합니다.
 */
@Component
class ReservationPersistenceAdapter(
    private val reservationJpaRepository: ReservationJpaRepository,
    private val seatJpaRepository: SeatJpaRepository,
    private val mapper: PersistenceMapper
) : ReservationRepository {

    /**
     * ID로 예약 정보를 조회합니다.
     */
    override fun findById(reservationId: Long): Reservation? {
        val reservationEntity = reservationJpaRepository.findById(reservationId).orElse(null) ?: return null
        return mapper.mapToDomainReservation(reservationEntity)
    }

    /**
     * 사용자 ID로 모든 예약 정보를 조회합니다.
     */
    override fun findByUserId(userId: String): List<Reservation> {
        val reservationEntities = reservationJpaRepository.findAllByUserId(userId)
        return reservationEntities.map { mapper.mapToDomainReservation(it) }
    }

    /**
     * 좌석 ID로 예약 정보를 조회합니다.
     */
    override fun findBySeatId(seatId: Long): Reservation? {
        val reservationEntity = reservationJpaRepository.findActiveBySeatId(seatId) ?: return null
        return mapper.mapToDomainReservation(reservationEntity)
    }

    /**
     * 만료 시간이 지난 임시 예약 목록을 조회합니다.
     */
    override fun findExpiredReservations(currentTime: LocalDateTime): List<Reservation> {
        val expiredEntities = reservationJpaRepository.findExpiredReservations(currentTime)
        return expiredEntities.map { mapper.mapToDomainReservation(it) }
    }

    /**
     * 상태별 예약 목록을 조회합니다.
     */
    override fun findByStatus(status: ReservationStatus): List<Reservation> {
        val reservationEntities = reservationJpaRepository.findByStatus(mapper.mapToEntityReservationStatus(status))
        return reservationEntities.map { mapper.mapToDomainReservation(it) }
    }

    /**
     * 예약을 삭제합니다.
     */
    @Transactional
    override fun deleteById(reservationId: Long) {
        reservationJpaRepository.deleteById(reservationId)
    }

    /**
     * 예약 객체를 저장합니다.
     */
    @Transactional
    override fun save(reservation: Reservation): Reservation {
        val seatEntity = seatJpaRepository.findById(reservation.seatId)
            .orElseThrow { IllegalArgumentException("좌석을 찾을 수 없습니다: ${reservation.seatId}") }

        val reservationEntity = mapper.mapToEntityReservation(reservation, seatEntity)
        val savedEntity = reservationJpaRepository.save(reservationEntity)

        return mapper.mapToDomainReservation(savedEntity)
    }
}