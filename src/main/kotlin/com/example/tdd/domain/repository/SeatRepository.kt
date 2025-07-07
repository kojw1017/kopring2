package com.example.tdd.domain.repository

import com.example.tdd.domain.model.Seat
import com.example.tdd.domain.model.SeatStatus
import java.util.UUID

/**
 * 좌석 리포지토리 인터페이스
 */
interface SeatRepository {
    /**
     * ID로 좌석 조회
     */
    fun findById(id: UUID): Seat?

    /**
     * 콘서트 날짜 ID와 좌석 번호로 좌석 조회
     */
    fun findByConcertDateIdAndSeatNumber(concertDateId: UUID, seatNumber: Int): Seat?

    /**
     * 콘서트 날짜 ID로 모든 좌석 조회
     */
    fun findAllByConcertDateId(concertDateId: UUID): List<Seat>

    /**
     * 콘서트 날짜 ID와 상태로 좌석 조회
     */
    fun findAllByConcertDateIdAndStatus(concertDateId: UUID, status: SeatStatus): List<Seat>

    /**
     * 만료된 임시 예약 좌석 조회
     */
    fun findAllExpiredTemporaryReservations(): List<Seat>

    /**
     * 좌석 저장
     */
    fun save(seat: Seat): Seat

    /**
     * 좌석 목록 저장
     */
    fun saveAll(seats: List<Seat>): List<Seat>
}
