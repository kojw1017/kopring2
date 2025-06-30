package com.example.tdd.adapter.`in`.web

import com.example.tdd.application.port.`in`.ConcertQueryUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 콘서트 일정 및 좌석 조회 컨트롤러
 */
@RestController
@RequestMapping("/api/concerts")
class ConcertController(
    private val concertQueryUseCase: ConcertQueryUseCase
) {

    /**
     * 예약 가능한 콘서트 날짜 목록 조회 API
     */
    @GetMapping("/dates")
    fun getAvailableDates(): ResponseEntity<DatesResponse> {
        val dates = concertQueryUseCase.getAvailableDates()

        return ResponseEntity.ok(
            DatesResponse(
                dates = dates.map { schedule ->
                    ScheduleDto(
                        scheduleId = schedule.scheduleId,
                        concertName = schedule.concertName,
                        concertDate = schedule.concertDate
                    )
                }
            )
        )
    }

    /**
     * 특정 날짜의 좌석 목록 조회 API
     */
    @GetMapping("/{scheduleId}/seats")
    fun getSeats(@PathVariable scheduleId: Long): ResponseEntity<SeatsResponse> {
        val seats = concertQueryUseCase.getSeats(scheduleId)

        return ResponseEntity.ok(
            SeatsResponse(
                seats = seats.map { seat ->
                    SeatDto(
                        seatId = seat.seatId,
                        seatNumber = seat.seatNumber,
                        status = seat.status,
                        price = seat.price
                    )
                }
            )
        )
    }
}

/**
 * 날짜 목록 응답 DTO
 */
data class DatesResponse(
    val dates: List<ScheduleDto>
)

/**
 * 콘서트 일정 DTO
 */
data class ScheduleDto(
    val scheduleId: Long,
    val concertName: String,
    val concertDate: LocalDateTime
)

/**
 * 좌석 목록 응답 DTO
 */
data class SeatsResponse(
    val seats: List<SeatDto>
)

/**
 * 좌석 정보 DTO
 */
data class SeatDto(
    val seatId: Long,
    val seatNumber: Int,
    val status: String,
    val price: BigDecimal
)
