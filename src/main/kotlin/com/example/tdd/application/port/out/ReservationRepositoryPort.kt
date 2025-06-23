package com.example.tdd.application.port.out

import com.example.tdd.domain.model.Payment
import com.example.tdd.domain.model.Reservation
import com.example.tdd.domain.model.ReservationStatus
import java.time.LocalDateTime

/**
 * 예약 엔티티에 대한 영속성 관리를 위한 아웃바운드 포트
 */
interface ReservationRepositoryPort {
    /**
     * ID로 예약 정보를 조회합니다.
     *
     * @param reservationId 조회할 예약 ID
     * @return 찾은 예약 객체 또는 null
     */
    fun findById(reservationId: Long): Reservation?

    /**
     * 사용자 ID로 모든 예약 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 예약 목록
     */
    fun findAllByUserId(userId: String): List<Reservation>

    /**
     * 좌석 ID로 진행 중인 예약이 있는지 확인합니다.
     *
     * @param seatId 좌석 ID
     * @return 활성 예약 객체 또는 null
     */
    fun findActiveBySeatId(seatId: Long): Reservation?

    /**
     * 만료 시간이 지난 임시 예약 목록을 조회합니다.
     *
     * @param currentTime 현재 시간
     * @return 만료된 예약 목록
     */
    fun findExpiredReservations(currentTime: LocalDateTime): List<Reservation>

    /**
     * 예약 상태를 업데이트합니다.
     *
     * @param reservationId 예약 ID
     * @param status 새로운 상태
     * @return 업데이트 성공 여부
     */
    fun updateStatus(reservationId: Long, status: ReservationStatus): Boolean

    /**
     * 예약 객체를 저장합니다.
     *
     * @param reservation 저장할 예약 객체
     * @return 저장된 예약 객체
     */
    fun save(reservation: Reservation): Reservation
}

/**
 * 결제 엔티티에 대한 영속성 관리를 위한 아웃바운드 포트
 */
interface PaymentRepositoryPort {
    /**
     * ID로 결제 정보를 조회합니다.
     *
     * @param paymentId 조회할 결제 ID
     * @return 찾은 결제 객체 또는 null
     */
    fun findById(paymentId: Long): Payment?

    /**
     * 예약 ID로 결제 정보를 조회합니다.
     *
     * @param reservationId 예약 ID
     * @return 찾은 결제 객체 또는 null
     */
    fun findByReservationId(reservationId: Long): Payment?

    /**
     * 결제 객체를 저장합니다.
     *
     * @param payment 저장할 결제 객체
     * @return 저장된 결제 객체
     */
    fun save(payment: Payment): Payment
}
