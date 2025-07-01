package com.example.tdd.domain.model

import com.example.tdd.adapter.`in`.web.exception.ConcurrentModificationException
import com.example.tdd.adapter.`in`.web.exception.InvalidRequestException
import java.math.BigDecimal

/**
 * 좌석 상태를 나타내는 Enum
 */
enum class SeatStatus {
    AVAILABLE,  // 예약 가능한 상태
    RESERVED,   // 임시 배정된 상태 (5분간 유지)
    SOLD        // 결제 완료되어 판매된 상태
}

/**
 * 좌석 도메인 모델
 * 좌석 번호, 상태, 가격 정보를 관리합니다.
 */
data class Seat(
    val seatId: Long,
    val scheduleId: Long,
    val seatNumber: Int,
    private var _status: SeatStatus = SeatStatus.AVAILABLE,
    val price: BigDecimal
) {
    // 좌석 상태 getter
    val status: SeatStatus
        get() = _status

    init {
        require(seatNumber in 1..50) { "좌석 번호는 1부터 50 사이의 값이어야 합니다." }
        require(price > BigDecimal.ZERO) { "좌석 가격은 0보다 커야 합니다." }
    }

    /**
     * 좌석을 임시 예약 상태로 변경합니다.
     * @throws ConcurrentModificationException 이미 예약되었거나 판매된 좌석인 경우
     */
    fun reserve() {
        if (_status != SeatStatus.AVAILABLE) {
            throw ConcurrentModificationException("이미 예약되었거나 판매된 좌석입니다.")
        }
        _status = SeatStatus.RESERVED
    }

    /**
     * 좌석 상태를 판매됨으로 변경합니다.
     * @throws InvalidRequestException 좌석이 임시 예약 상태가 아닌 경우
     */
    fun sell() {
        if (_status != SeatStatus.RESERVED) {
            throw InvalidRequestException("임시 예약된 좌석만 판매 가능합니다.")
        }
        _status = SeatStatus.SOLD
    }

    /**
     * 좌석 예약을 취소하고 상태를 예약 가능으로 변경합니다.
     * @throws InvalidRequestException 좌석이 이미 판매된 경우
     */
    fun cancelReservation() {
        if (_status == SeatStatus.SOLD) {
            throw InvalidRequestException("이미 판매된 좌석은 예약 취소가 불가능합니다.")
        }
        _status = SeatStatus.AVAILABLE
    }
}
