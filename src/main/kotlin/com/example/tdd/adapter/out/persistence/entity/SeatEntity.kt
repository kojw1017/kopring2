package com.example.tdd.adapter.out.persistence.entity

import com.example.tdd.domain.model.SeatStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "seats")
class SeatEntity(
    @Id
    val id: UUID,

    val concertDateId: UUID,

    val seatNumber: Int,

    @Enumerated(EnumType.STRING)
    val status: SeatStatus,

    val reservedBy: UUID?,

    val reservedAt: LocalDateTime?,

    val temporaryReservationExpiresAt: LocalDateTime?
) {
    companion object {
        fun fromDomain(domain: com.example.tdd.domain.model.Seat): SeatEntity {
            return SeatEntity(
                id = domain.id,
                concertDateId = domain.concertDateId,
                seatNumber = domain.seatNumber,
                status = domain.status,
                reservedBy = domain.reservedBy,
                reservedAt = domain.reservedAt,
                temporaryReservationExpiresAt = domain.temporaryReservationExpiresAt
            )
        }
    }

    fun toDomain(): com.example.tdd.domain.model.Seat {
        // 도메인 모델의 create 메서드를 사용한 후 상태를 변경하는 방식으로 변환
        val seat = com.example.tdd.domain.model.Seat.create(
            concertDateId = concertDateId,
            seatNumber = seatNumber
        )

        return when (status) {
            SeatStatus.AVAILABLE -> seat
            SeatStatus.TEMPORARY_RESERVED -> {
                if (reservedBy != null) {
                    val temporaryReserved = seat.temporaryReserve(
                        userId = reservedBy,
                        temporaryReservationMinutes = if (temporaryReservationExpiresAt != null) {
                            val duration = java.time.Duration.between(LocalDateTime.now(), temporaryReservationExpiresAt)
                            duration.toMinutes().toInt().coerceAtLeast(1)
                        } else {
                            5 // 기본값
                        }
                    )
                    temporaryReserved
                } else {
                    seat
                }
            }
            SeatStatus.RESERVED -> {
                if (reservedBy != null) {
                    val temporaryReserved = seat.temporaryReserve(
                        userId = reservedBy,
                        temporaryReservationMinutes = 5 // 임의 값
                    )
                    temporaryReserved.confirmReservation()
                } else {
                    seat
                }
            }
        }
    }
}
