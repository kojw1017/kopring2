package com.example.tdd.domain.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * 좌석 도메인 모델
 */
class Seat private constructor(
    val id: UUID,
    val concertDateId: UUID,
    val seatNumber: Int,
    val status: SeatStatus,
    val reservedBy: UUID?,
    val reservedAt: LocalDateTime?,
    val temporaryReservationExpiresAt: LocalDateTime?
) {
    companion object {
        fun create(concertDateId: UUID, seatNumber: Int): Seat {
            return Seat(
                id = UUID.randomUUID(),
                concertDateId = concertDateId,
                seatNumber = seatNumber,
                status = SeatStatus.AVAILABLE,
                reservedBy = null,
                reservedAt = null,
                temporaryReservationExpiresAt = null
            )
        }
    }

    /**
     * 좌석 임시 예약
     */
    fun temporaryReserve(userId: UUID, temporaryReservationMinutes: Int): Seat {
        require(status == SeatStatus.AVAILABLE) { "이미 예약된 좌석입니다." }

        val now = LocalDateTime.now()
        return copy(
            status = SeatStatus.TEMPORARY_RESERVED,
            reservedBy = userId,
            reservedAt = now,
            temporaryReservationExpiresAt = now.plusMinutes(temporaryReservationMinutes.toLong())
        )
    }

    /**
     * 임시 예약 확정 (결제 완료)
     */
    fun confirmReservation(): Seat {
        require(status == SeatStatus.TEMPORARY_RESERVED) { "임시 예약 상태가 아닙니다." }
        require(!isTemporaryReservationExpired()) { "임시 예약이 만료되었습니다." }

        return copy(
            status = SeatStatus.RESERVED,
            temporaryReservationExpiresAt = null
        )
    }

    /**
     * 임시 예약 취소 또는 만료
     */
    fun cancelTemporaryReservation(): Seat {
        require(status == SeatStatus.TEMPORARY_RESERVED) { "임시 예약 상태가 아닙니다." }

        return copy(
            status = SeatStatus.AVAILABLE,
            reservedBy = null,
            reservedAt = null,
            temporaryReservationExpiresAt = null
        )
    }

    /**
     * 임시 예약이 만료되었는지 확인
     */
    fun isTemporaryReservationExpired(): Boolean {
        return status == SeatStatus.TEMPORARY_RESERVED &&
               temporaryReservationExpiresAt != null &&
               LocalDateTime.now().isAfter(temporaryReservationExpiresAt)
    }

    private fun copy(
        id: UUID = this.id,
        concertDateId: UUID = this.concertDateId,
        seatNumber: Int = this.seatNumber,
        status: SeatStatus = this.status,
        reservedBy: UUID? = this.reservedBy,
        reservedAt: LocalDateTime? = this.reservedAt,
        temporaryReservationExpiresAt: LocalDateTime? = this.temporaryReservationExpiresAt
    ): Seat {
        return Seat(id, concertDateId, seatNumber, status, reservedBy, reservedAt, temporaryReservationExpiresAt)
    }
}

/**
 * 좌석 상태
 */
enum class SeatStatus {
    AVAILABLE,           // 예약 가능
    TEMPORARY_RESERVED,  // 임시 예약 (결제 대기 중)
    RESERVED             // 예약 완료 (결제 완료)
}
