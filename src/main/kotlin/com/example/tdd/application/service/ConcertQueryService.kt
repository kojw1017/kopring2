package com.example.tdd.application.service

import com.example.tdd.application.port.`in`.ConcertQueryUseCase
import com.example.tdd.application.port.`in`.ScheduleResponse
import com.example.tdd.application.port.`in`.SeatResponse
import com.example.tdd.application.port.out.ScheduleRepository
import com.example.tdd.application.port.out.SeatRepository
import com.example.tdd.domain.exception.ScheduleNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 콘서트 조회 서비스
 * 콘서트 일정 및 좌석 정보를 조회하는 기능을 제공합니다.
 */
@Service
class ConcertQueryService(
    private val scheduleRepository: ScheduleRepository,
    private val seatRepository: SeatRepository
) : ConcertQueryUseCase {

    /**
     * 사용 가능한 콘서트 일정 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    override fun getAvailableDates(): List<ScheduleResponse> {
        val schedules = scheduleRepository.findAvailableSchedules()

        return schedules.map { schedule ->
            ScheduleResponse(
                scheduleId = schedule.scheduleId,
                concertName = schedule.concertName,
                concertDate = schedule.concertDate
            )
        }
    }

    /**
     * 특정 콘서트 일정의 좌석 현황을 조회합니다.
     */
    @Transactional(readOnly = true)
    override fun getSeats(scheduleId: Long): List<SeatResponse> {
        // 일정 존재 확인
        scheduleRepository.findById(scheduleId)
            ?: throw ScheduleNotFoundException("콘서트 일정을 찾을 수 없습니다. scheduleId: $scheduleId")

        val seats = seatRepository.findByScheduleId(scheduleId)

        return seats.map { seat ->
            SeatResponse(
                seatId = seat.seatId,
                seatNumber = seat.seatNumber,
                status = seat.status.name,
                price = seat.price
            )
        }
    }
}
