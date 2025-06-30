package com.example.tdd.domain.model

import java.time.LocalDateTime

/**
 * 콘서트 일정 도메인 모델
 */
data class Schedule(
    val scheduleId: Long,
    val concertName: String,
    val concertDate: LocalDateTime,
    val venue: String,
    val maxSeats: Int = 50
) {
    init {
        require(concertName.isNotBlank()) { "콘서트 이름은 비어있을 수 없습니다." }
        require(venue.isNotBlank()) { "공연장 정보는 비어있을 수 없습니다." }
        require(maxSeats > 0) { "최대 좌석 수는 0보다 커야 합니다." }
        require(concertDate.isAfter(LocalDateTime.now())) { "콘서트 일정은 현재 시간 이후여야 합니다." }
    }

    /**
     * 콘서트가 진행 가능한 상태인지 확인
     */
    fun isAvailable(): Boolean {
        return concertDate.isAfter(LocalDateTime.now())
    }
}
