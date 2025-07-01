package com.example.tdd.application.service

import com.example.tdd.application.port.`in`.ConcertQueryUseCase
import com.example.tdd.application.port.`in`.ScheduleResponse
import com.example.tdd.application.port.`in`.SeatResponse
import com.example.tdd.application.port.out.ScheduleRepositoryPort
import com.example.tdd.application.port.out.SeatRepositoryPort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * 콘서트 조회 서비스
 * 콘서트 일정 및 좌석 정보를 조회하는 기능을 제공합니다.
 */
@Service
class ConcertQueryService(
    private val scheduleRepository: ScheduleRepositoryPort,
    private val seatRepository: SeatRepositoryPort
) : ConcertQueryUseCase {

    /**
     * 사용 가능한 콘서트 일정 목록을 조회합니다.
     */
    override fun getAvailableDates(): List<ScheduleResponse> {
        val schedules = scheduleRepository.findAllAvailable()

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
    override fun getSeats(scheduleId: Long): List<SeatResponse> {
        val seats = seatRepository.findAllByScheduleId(scheduleId)

        return seats.map { seat ->
            SeatResponse(
                seatNumber = seat.seatNumber,
                status = seat.status.name,
                price = seat.price
            )
        }
    }
}
