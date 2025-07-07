package com.example.tdd.adapter.out.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "concert_dates")
class ConcertDateEntity(
    @Id
    val id: UUID,

    val date: LocalDate,

    val name: String,

    val price: Long,

    val totalSeats: Int,

    val availableSeats: Int
) {
    companion object {
        fun fromDomain(domain: com.example.tdd.domain.model.ConcertDate): ConcertDateEntity {
            return ConcertDateEntity(
                id = domain.id,
                date = domain.date,
                name = domain.name,
                price = domain.price,
                totalSeats = domain.totalSeats,
                availableSeats = domain.availableSeats
            )
        }
    }

    fun toDomain(): com.example.tdd.domain.model.ConcertDate {
        // 도메인 모델의 create 메서드를 사용한 후, 가용 좌석 수 조정 방식으로 변환
        val concertDate = com.example.tdd.domain.model.ConcertDate.create(
            date = date,
            name = name,
            price = price,
            totalSeats = totalSeats
        )

        // 예약된 좌석 수만큼 좌석 예약 처리
        var resultDate = concertDate
        val reservedSeats = totalSeats - availableSeats
        repeat(reservedSeats) {
            resultDate = resultDate.reserveSeat()
        }

        return resultDate
    }
}
