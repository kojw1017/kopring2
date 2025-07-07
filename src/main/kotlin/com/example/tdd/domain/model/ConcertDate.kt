package com.example.tdd.domain.model

import java.time.LocalDate
import java.util.UUID

/**
 * 콘서트 날짜 도메인 모델
 */
class ConcertDate private constructor(
    val id: UUID,
    val date: LocalDate,
    val name: String,
    val price: Long,
    val totalSeats: Int,
    val availableSeats: Int
) {
    companion object {
        fun create(date: LocalDate, name: String, price: Long, totalSeats: Int): ConcertDate {
            return ConcertDate(
                id = UUID.randomUUID(),
                date = date,
                name = name,
                price = price,
                totalSeats = totalSeats,
                availableSeats = totalSeats
            )
        }
    }

    /**
     * 좌석 예약 시 가용 좌석 수 감소
     */
    fun reserveSeat(): ConcertDate {
        require(availableSeats > 0) { "예약 가능한 좌석이 없습니다." }
        return copy(availableSeats = this.availableSeats - 1)
    }

    /**
     * 좌석 예약 취소 시 가용 좌석 수 증가
     */
    fun cancelSeatReservation(): ConcertDate {
        require(availableSeats < totalSeats) { "모든 좌석이 이미 가용 상태입니다." }
        return copy(availableSeats = this.availableSeats + 1)
    }

    private fun copy(
        id: UUID = this.id,
        date: LocalDate = this.date,
        name: String = this.name,
        price: Long = this.price,
        totalSeats: Int = this.totalSeats,
        availableSeats: Int = this.availableSeats
    ): ConcertDate {
        return ConcertDate(id, date, name, price, totalSeats, availableSeats)
    }
}
