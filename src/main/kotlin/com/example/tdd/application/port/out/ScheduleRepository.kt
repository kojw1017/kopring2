package com.example.tdd.application.port.out

import com.example.tdd.domain.model.Schedule

/**
 * 콘서트 일정 데이터 접근을 위한 아웃바운드 포트
 */
interface ScheduleRepository {
    fun findAvailableSchedules(): List<Schedule>
    fun findById(scheduleId: Long): Schedule?
    fun save(schedule: Schedule): Schedule
}
