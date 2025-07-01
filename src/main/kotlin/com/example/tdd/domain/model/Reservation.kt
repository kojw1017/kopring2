package com.example.tdd.domain.model

import com.example.tdd.adapter.`in`.web.exception.InvalidRequestException
import com.example.tdd.adapter.`in`.web.exception.ReservationExpiredException
import java.time.LocalDateTime

/**
 * 예약 상태를 나타내는 Enum
 */
enum class ReservationStatus {
    PENDING,  // 임시 예약 상태 (결제 대기)
    PAID,     // 결제 완료 상태
    EXPIRED   // 결제 시간 초과로 만료된 상태
}

/**
 * 예약 도메인 모델
 * 유저의 좌석 예약 정보를 관리합니다.
 */
data class Reservation(
    val reservationId: Long,
    val userId: String,
    val seatId: Long,
    private var _status: ReservationStatus = ReservationStatus.PENDING,
    val expiresAt: LocalDateTime
) {
    // 예약 상태 getter
    val status: ReservationStatus
        get() = _status

    init {
        require(expiresAt.isAfter(LocalDateTime.now())) { "만료 시간은 현재 시간 이후여야 합니다." }
    }

    /**
     * 예약 상태를 결제 완료로 변경합니다.
     * @throws InvalidRequestException 예약이 이미 결제 완료된 경우
     * @throws ReservationExpiredException 예약이 이미 만료된 경우
     */
    fun complete() {
        if (_status == ReservationStatus.PAID) {
            throw InvalidRequestException("이미 결제 완료된 예약입니다.")
        }
        if (_status == ReservationStatus.EXPIRED || isExpired()) {
            throw ReservationExpiredException("만료된 예약은 결제 완료 처리할 수 없습니다.")
        }
        _status = ReservationStatus.PAID
    }

    /**
     * 예약 상태를 만료로 변경합니다.
     * @throws InvalidRequestException 예약이 이미 결제 완료된 경우
     */
    fun expire() {
        if (_status == ReservationStatus.PAID) {
            throw InvalidRequestException("결제 완료된 예약은 만료 처리할 수 없습니다.")
        }
        _status = ReservationStatus.EXPIRED
    }

    /**
     * 예약이 만료되었는지 확인합니다.
     * @return 현재 시간이 만료 시간을 초과했는지 여부
     */
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt) || _status == ReservationStatus.EXPIRED
    }
}
