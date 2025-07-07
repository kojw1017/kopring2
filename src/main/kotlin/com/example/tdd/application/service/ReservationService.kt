package com.example.tdd.application.service

import com.example.tdd.application.exception.InvalidRequestException
import com.example.tdd.application.exception.QueueException
import com.example.tdd.application.exception.ReservationException
import com.example.tdd.application.exception.ResourceNotFoundException
import com.example.tdd.application.port.`in`.ConcertDateResponse
import com.example.tdd.application.port.`in`.ConcertReservationUseCase
import com.example.tdd.application.port.`in`.ReservationResponse
import com.example.tdd.application.port.`in`.ReserveSeatCommand
import com.example.tdd.application.port.`in`.SeatResponse
import com.example.tdd.domain.model.QueueStatus
import com.example.tdd.domain.model.SeatStatus
import com.example.tdd.domain.repository.ConcertDateRepository
import com.example.tdd.domain.repository.QueueTokenRepository
import com.example.tdd.domain.repository.SeatRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class ReservationService(
    private val concertDateRepository: ConcertDateRepository,
    private val seatRepository: SeatRepository,
    private val queueTokenRepository: QueueTokenRepository,
    @Value("\${queue.temporary-reservation-minutes:5}")
    private val temporaryReservationMinutes: Int
) : ConcertReservationUseCase {

    @Transactional(readOnly = true)
    override fun getAvailableConcertDates(): List<ConcertDateResponse> {
        return concertDateRepository.findAllAvailable()
            .map { concertDate ->
                ConcertDateResponse(
                    id = concertDate.id,
                    date = concertDate.date,
                    name = concertDate.name,
                    price = concertDate.price,
                    availableSeats = concertDate.availableSeats,
                    totalSeats = concertDate.totalSeats
                )
            }
    }

    @Transactional(readOnly = true)
    override fun getAvailableSeats(concertDateId: UUID): List<SeatResponse> {
        val concertDate = concertDateRepository.findById(concertDateId)
            ?: throw ResourceNotFoundException("ConcertDate", concertDateId.toString())

        return seatRepository.findAllByConcertDateId(concertDateId)
            .map { seat ->
                SeatResponse(
                    id = seat.id,
                    seatNumber = seat.seatNumber,
                    status = seat.status.name
                )
            }
    }

    @Transactional
    override fun reserveSeat(command: ReserveSeatCommand): ReservationResponse {
        // 대기열 토큰 검증
        val queueToken = queueTokenRepository.findByToken(command.token)
            ?: throw QueueException("대기열 토큰을 찾을 수 없습니다.")

        if (queueToken.status != QueueStatus.ACTIVE) {
            throw QueueException("활성화된 대기열 토큰이 아닙니다.")
        }

        // 콘서트 정보 조회
        val concertDate = concertDateRepository.findById(command.concertDateId)
            ?: throw ResourceNotFoundException("ConcertDate", command.concertDateId.toString())

        if (concertDate.availableSeats <= 0) {
            throw ReservationException("예약 가능한 좌석이 없습니다.")
        }

        // 좌석 정보 조회
        val seat = seatRepository.findByConcertDateIdAndSeatNumber(command.concertDateId, command.seatNumber)
            ?: throw ResourceNotFoundException("Seat", "ConcertDate: ${command.concertDateId}, SeatNumber: ${command.seatNumber}")

        if (seat.status != SeatStatus.AVAILABLE) {
            throw ReservationException("이미 예약된 좌석입니다.")
        }

        // 임시 좌석 예약
        val reservedSeat = seatRepository.save(seat.temporaryReserve(queueToken.userId, temporaryReservationMinutes))

        // 콘서트 가용 좌석 수 감소
        concertDateRepository.save(concertDate.reserveSeat())

        return ReservationResponse(
            reservationId = reservedSeat.id,
            userId = queueToken.userId,
            concertDateId = concertDate.id,
            seatNumber = reservedSeat.seatNumber,
            price = concertDate.price,
            expiresAt = reservedSeat.temporaryReservationExpiresAt.toString()
        )
    }
}
