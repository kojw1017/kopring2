package com.example.tdd.adapter.out.persistence.repository

import com.example.tdd.adapter.out.persistence.entity.SeatEntity
import com.example.tdd.domain.model.SeatStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.UUID

interface SeatJpaRepository : JpaRepository<SeatEntity, UUID> {
    fun findByConcertDateIdAndSeatNumber(concertDateId: UUID, seatNumber: Int): SeatEntity?

    fun findAllByConcertDateId(concertDateId: UUID): List<SeatEntity>

    fun findAllByConcertDateIdAndStatus(concertDateId: UUID, status: SeatStatus): List<SeatEntity>

    @Query("SELECT s FROM SeatEntity s WHERE s.status = com.example.tdd.domain.model.SeatStatus.TEMPORARY_RESERVED AND s.temporaryReservationExpiresAt < :now")
    fun findAllExpiredTemporaryReservations(now: LocalDateTime = LocalDateTime.now()): List<SeatEntity>
}
