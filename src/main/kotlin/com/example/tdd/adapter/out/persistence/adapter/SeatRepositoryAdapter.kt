package com.example.tdd.adapter.out.persistence.adapter

import com.example.tdd.adapter.out.persistence.entity.SeatEntity
import com.example.tdd.adapter.out.persistence.repository.SeatJpaRepository
import com.example.tdd.domain.model.Seat
import com.example.tdd.domain.model.SeatStatus
import com.example.tdd.domain.repository.SeatRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class SeatRepositoryAdapter(
    private val seatJpaRepository: SeatJpaRepository
) : SeatRepository {
    override fun findById(id: UUID): Seat? {
        return seatJpaRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByConcertDateIdAndSeatNumber(concertDateId: UUID, seatNumber: Int): Seat? {
        return seatJpaRepository.findByConcertDateIdAndSeatNumber(concertDateId, seatNumber)?.toDomain()
    }

    override fun findAllByConcertDateId(concertDateId: UUID): List<Seat> {
        return seatJpaRepository.findAllByConcertDateId(concertDateId)
            .map { it.toDomain() }
    }

    override fun findAllByConcertDateIdAndStatus(concertDateId: UUID, status: SeatStatus): List<Seat> {
        return seatJpaRepository.findAllByConcertDateIdAndStatus(concertDateId, status)
            .map { it.toDomain() }
    }

    override fun findAllExpiredTemporaryReservations(): List<Seat> {
        return seatJpaRepository.findAllExpiredTemporaryReservations()
            .map { it.toDomain() }
    }

    override fun save(seat: Seat): Seat {
        val seatEntity = SeatEntity.fromDomain(seat)
        val savedEntity = seatJpaRepository.save(seatEntity)
        return savedEntity.toDomain()
    }

    override fun saveAll(seats: List<Seat>): List<Seat> {
        val seatEntities = seats.map { SeatEntity.fromDomain(it) }
        val savedEntities = seatJpaRepository.saveAll(seatEntities)
        return savedEntities.map { it.toDomain() }
    }
}
