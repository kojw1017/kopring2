package com.example.tdd.adapter.out.persistence.adapter

import com.example.tdd.adapter.out.persistence.entity.ConcertDateEntity
import com.example.tdd.adapter.out.persistence.repository.ConcertDateJpaRepository
import com.example.tdd.domain.model.ConcertDate
import com.example.tdd.domain.repository.ConcertDateRepository
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.UUID

@Component
class ConcertDateRepositoryAdapter(
    private val concertDateJpaRepository: ConcertDateJpaRepository
) : ConcertDateRepository {
    override fun findById(id: UUID): ConcertDate? {
        return concertDateJpaRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByDate(date: LocalDate): ConcertDate? {
        return concertDateJpaRepository.findByDate(date)?.toDomain()
    }

    override fun findAllAvailable(): List<ConcertDate> {
        return concertDateJpaRepository.findAllAvailable()
            .map { it.toDomain() }
    }

    override fun save(concertDate: ConcertDate): ConcertDate {
        val concertDateEntity = ConcertDateEntity.fromDomain(concertDate)
        val savedEntity = concertDateJpaRepository.save(concertDateEntity)
        return savedEntity.toDomain()
    }
}
