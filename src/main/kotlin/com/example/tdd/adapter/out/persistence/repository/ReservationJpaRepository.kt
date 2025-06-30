package com.example.tdd.adapter.out.persistence.repository

import com.example.tdd.adapter.out.persistence.entity.ReservationEntity
import com.example.tdd.adapter.out.persistence.entity.ReservationStatusEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

/**
 * 예약 JPA 리포지토리
 */
interface ReservationJpaRepository : JpaRepository<ReservationEntity, Long> {
    /**
     * 사용자 ID로 모든 예약 정보를 조회합니다.
     */
    fun findAllByUserId(userId: String): List<ReservationEntity>

    /**
     * 좌석 ID로 진행 중인 예약이 있는지 확인합니다.
     */
    @Query("SELECT r FROM ReservationEntity r WHERE r.seat.seatId = :seatId AND r.status = 'PENDING'")
    fun findActiveBySeatId(seatId: Long): ReservationEntity?

    /**
     * 만료 시간이 지난 임시 예약 목록을 조회합니다.
     */
    @Query("SELECT r FROM ReservationEntity r WHERE r.status = 'PENDING' AND r.expiresAt < :currentTime")
    fun findExpiredReservations(currentTime: LocalDateTime): List<ReservationEntity>

    /**
     * 예약 상태를 업데이트합니다.
     */
    @Modifying
    @Query("UPDATE ReservationEntity r SET r.status = :status WHERE r.reservationId = :reservationId")
    fun updateStatus(reservationId: Long, status: ReservationStatusEntity): Int

    /**
     * 상태별 예약 목록을 조회합니다.
     */
    fun findByStatus(status: ReservationStatusEntity): List<ReservationEntity>

    /**
     * 예약 ID로 예약을 조회합니다.
     */
    fun findByReservationId(reservationId: Long): ReservationEntity?
}
