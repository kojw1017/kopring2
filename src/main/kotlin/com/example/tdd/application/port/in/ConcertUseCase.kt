package com.example.tdd.application.port.`in`

/**
 * 콘서트 일정 및 좌석 조회를 위한 인바운드 포트
 */
interface ConcertQueryUseCase {
    /**
     * 사용 가능한 콘서트 일정 목록을 조회합니다.
     *
     * @return 콘서트 일정 목록
     */
    fun getAvailableDates(): List<ScheduleResponse>

    /**
     * 특정 콘서트 일정의 좌석 현황을 조회합니다.
     *
     * @param scheduleId 콘서트 일정 ID
     * @return 좌석 목록 및 상태
     */
    fun getSeats(scheduleId: Long): List<SeatResponse>
}
