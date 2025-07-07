package com.example.tdd.adapter.`in`.web

import com.example.tdd.application.port.`in`.ConcertDateResponse
import com.example.tdd.application.port.`in`.ConcertReservationUseCase
import com.example.tdd.application.port.`in`.ReservationResponse
import com.example.tdd.application.port.`in`.ReserveSeatCommand
import com.example.tdd.application.port.`in`.SeatResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/concerts")
@Tag(name = "콘서트 예약", description = "콘서트 예약 관련 API")
class ConcertController(
    private val concertReservationUseCase: ConcertReservationUseCase
) {

    @GetMapping("/dates")
    @Operation(
        summary = "예약 가능한 콘서트 날짜 조회",
        description = "예약 가능한 모든 콘서트 날짜 목록을 조회합니다."
    )
    fun getAvailableConcertDates(): ResponseEntity<List<ConcertDateResponse>> {
        val response = concertReservationUseCase.getAvailableConcertDates()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/dates/{concertDateId}/seats")
    @Operation(
        summary = "좌석 조회",
        description = "특정 콘서트 날짜의 모든 좌석 상태를 조회합니다."
    )
    fun getAvailableSeats(
        @PathVariable concertDateId: UUID
    ): ResponseEntity<List<SeatResponse>> {
        val response = concertReservationUseCase.getAvailableSeats(concertDateId)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/reserve")
    @Operation(
        summary = "좌석 예약",
        description = "특정 콘서트 날짜의 좌석을 임시로 예약합니다."
    )
    fun reserveSeat(
        @Valid @RequestBody request: ReserveSeatRequest
    ): ResponseEntity<ReservationResponse> {
        val command = ReserveSeatCommand(
            token = request.token,
            concertDateId = request.concertDateId,
            seatNumber = request.seatNumber
        )
        val response = concertReservationUseCase.reserveSeat(command)
        return ResponseEntity.ok(response)
    }
}

data class ReserveSeatRequest(
    @field:NotBlank(message = "토큰이 필요합니다.")
    val token: String,

    @field:NotNull(message = "콘서트 날짜 ID가 필요합니다.")
    val concertDateId: UUID,

    @field:NotNull(message = "좌석 번호가 필요합니다.")
    val seatNumber: Int
)
