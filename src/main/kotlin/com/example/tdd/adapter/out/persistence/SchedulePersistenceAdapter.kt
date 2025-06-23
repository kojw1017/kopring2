package com.example.tdd.adapter.out.persistence

import com.example.tdd.adapter.out.persistence.mapper.PersistenceMapper
import com.example.tdd.adapter.out.persistence.repository.ScheduleJpaRepository
import com.example.tdd.application.port.out.ScheduleRepositoryPort
import com.example.tdd.domain.model.Schedule
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 콘서트 스케줄 관련 영속성 어댑터
 * 아웃바운드 포트를 구현하여 도메인 모델과 데이터베이스 간의 상호작용을 담당합니다.
 */
@Component
class SchedulePersistenceAdapter(
    private val scheduleJpaRepository: ScheduleJpaRepository,
    private val mapper: PersistenceMapper
) : ScheduleRepositoryPort {

    /**
     * 사용 가능한 모든 콘서트 일정을 조회합니다.
     */
    override fun findAllAvailable(): List<Schedule> {
        val scheduleEntities = scheduleJpaRepository.findAllAvailableSchedules(LocalDateTime.now())
        return scheduleEntities.map { mapper.mapToDomainSchedule(it) }
    }

    /**
     * ID로 콘서트 일정을 조회합니다.
     */
    override fun findById(scheduleId: Long): Schedule? {
        val scheduleEntity = scheduleJpaRepository.findById(scheduleId).orElse(null) ?: return null
        return mapper.mapToDomainSchedule(scheduleEntity)
    }
}
