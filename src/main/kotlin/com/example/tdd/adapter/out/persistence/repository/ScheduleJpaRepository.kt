package com.example.tdd.adapter.out.persistence.repository

import com.example.tdd.adapter.out.persistence.entity.ScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

/**
 * 콘서트 일정 JPA 리포지토리
 */
interface ScheduleJpaRepository : JpaRepository<ScheduleEntity, Long> {
    /**
     * 현재 시간 이후의 사용 가능한 모든 콘서트 일정을 조회합니다.
     */
    @Query("SELECT s FROM ScheduleEntity s WHERE s.concertDate > :now ORDER BY s.concertDate ASC")
    fun findAllAvailableSchedules(now: LocalDateTime = LocalDateTime.now()): List<ScheduleEntity>
}
