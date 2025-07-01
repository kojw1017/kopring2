package com.example.tdd.domain.model

import java.time.LocalDateTime

/**
 * 콘서트 스케줄 도메인 모델
 * 콘서트 이름과 날짜 정보를 관리합니다.
 */
class Schedule(
    val scheduleId: Long,
    val concertName: String,
    val concertDate: LocalDateTime
) {
    init {
        require(concertName.isNotBlank()) { "콘서트 이름은 비어있을 수 없습니다." }
        require(concertDate.isAfter(LocalDateTime.now())) { "콘서트 날짜는 현재 시간 이후여야 합니다." }
    }
}
