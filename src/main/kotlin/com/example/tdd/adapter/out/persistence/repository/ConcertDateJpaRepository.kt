package com.example.tdd.adapter.out.persistence.repository

import com.example.tdd.adapter.out.persistence.entity.ConcertDateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.UUID

interface ConcertDateJpaRepository : JpaRepository<ConcertDateEntity, UUID> {
    fun findByDate(date: LocalDate): ConcertDateEntity?

    @Query("SELECT c FROM ConcertDateEntity c WHERE c.date >= CURRENT_DATE ORDER BY c.date")
    fun findAllAvailable(): List<ConcertDateEntity>
}
