package com.example.tdd.adapter.out.persistence

import com.example.tdd.adapter.out.persistence.mapper.PersistenceMapper
import com.example.tdd.adapter.out.persistence.repository.ScheduleJpaRepository
import com.example.tdd.adapter.out.persistence.repository.SeatJpaRepository
import com.example.tdd.application.port.out.SeatRepository
import com.example.tdd.domain.model.Seat
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 좌석 관련 영속성 어댑터
 * 아웃바운드 포트를 구현하여 도메인 모델과 데이터베이스 간의 상호작용을 담당합니다.
 */
@Component
class SeatPersistenceAdapter(
    private val seatJpaRepository: SeatJpaRepository,
    private val scheduleJpaRepository: ScheduleJpaRepository,
    private val mapper: PersistenceMapper
) : SeatRepository {

    /**
     * 특정 일정의 모든 좌석을 조회합니다.
     */
    override fun findByScheduleId(scheduleId: Long): List<Seat> {
        val seatEntities = seatJpaRepository.findAllByScheduleId(scheduleId)
        return seatEntities.map { mapper.mapToDomainSeat(it) }
    }

    /**
     * 좌석 ID로 좌석을 조회합니다.
     */
    override fun findById(seatId: Long): Seat? {
        val seatEntity = seatJpaRepository.findById(seatId).orElse(null) ?: return null
        return mapper.mapToDomainSeat(seatEntity)
    }

    /**
     * 특정 일정의 특정 좌석 번호로 좌석을 조회합니다.
     */
    override fun findByScheduleIdAndSeatNumber(scheduleId: Long, seatNumber: Int): Seat? {
        val seatEntity = seatJpaRepository.findByScheduleIdAndSeatNumber(scheduleId, seatNumber) ?: return null
        return mapper.mapToDomainSeat(seatEntity)
    }

    /**
     * 좌석 객체를 저장합니다.
     */
    @Transactional
    override fun save(seat: Seat): Seat {
        val scheduleEntity = scheduleJpaRepository.findById(seat.scheduleId)
            .orElseThrow { IllegalArgumentException("일정을 찾을 수 없습니다: ${seat.scheduleId}") }

        val seatEntity = mapper.mapToEntitySeat(seat, scheduleEntity)
        val savedEntity = seatJpaRepository.save(seatEntity)

        return mapper.mapToDomainSeat(savedEntity)
    }

    /**
     * 여러 좌석을 한 번에 저장합니다.
     */
    @Transactional
    override fun saveAll(seats: List<Seat>): List<Seat> {
        return seats.map { save(it) }
    }

    /**
     * 특정 일정의 예약 가능한 좌석들을 조회합니다.
     */
    override fun findAvailableSeats(scheduleId: Long): List<Seat> {
        val seatEntities = seatJpaRepository.findAllByScheduleId(scheduleId)
            .filter { it.status.name == "AVAILABLE" }
        return seatEntities.map { mapper.mapToDomainSeat(it) }
    }
}