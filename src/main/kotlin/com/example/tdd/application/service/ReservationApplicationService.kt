package com.example.tdd.application.service

import com.example.tdd.application.port.`in`.ReservationUseCase
import com.example.tdd.application.port.`in`.ReserveSeatCommand
import com.example.tdd.application.port.`in`.CancelReservationCommand
import com.example.tdd.application.port.`in`.ReservationResponse
import com.example.tdd.application.port.out.UserRepository
import com.example.tdd.application.port.out.SeatRepository
import com.example.tdd.application.port.out.ReservationRepository
import com.example.tdd.application.port.out.ScheduleRepository
import com.example.tdd.application.port.out.QueueTokenRepository
import com.example.tdd.domain.model.Reservation
import com.example.tdd.domain.service.ReservationService
import com.example.tdd.domain.exception.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 예약 관리 애플리케이션 서비스
 */
@Service
class ReservationApplicationService(
    private val reservationRepository: ReservationRepository,
    private val seatRepository: SeatRepository,
    private val scheduleRepository: ScheduleRepository,
    private val userRepository: UserRepository,
    private val queueTokenRepository: QueueTokenRepository,
    private val reservationService: ReservationService
) : ReservationUseCase {

    @Transactional
    override fun reserveSeat(command: ReserveSeatCommand): ReservationResponse {
        // 토큰 유효성 검증
        if (!queueTokenRepository.isValidToken(command.token)) {
            throw InvalidTokenException("유효하지 않은 토큰입니다.")
        }

        // 사용자 존재 확인
        val user = userRepository.findById(command.userId)
            ?: throw UserNotFoundException("사용자를 찾을 수 없습니다.")

        // 좌석 조회
        val seat = seatRepository.findById(command.seatId)
            ?: throw SeatNotFoundException("좌석을 찾을 수 없습니다.")

        // 일정 조회
        val schedule = scheduleRepository.findById(seat.scheduleId)
            ?: throw ScheduleNotFoundException("콘서트 일정을 찾을 수 없습니다.")

        // 도메인 서비스를 통한 예약 처리
        val reservation = reservationService.createReservation(
            userId = command.userId,
            seat = seat,
            reservationId = generateReservationId()
        )

        // 저장
        val savedReservation = reservationRepository.save(reservation)
        val savedSeat = seatRepository.save(seat)

        return ReservationResponse(
            reservationId = savedReservation.reservationId,
            userId = savedReservation.userId,
            seatId = savedSeat.seatId,
            seatNumber = savedSeat.seatNumber,
            concertName = schedule.concertName,
            concertDate = schedule.concertDate,
            price = savedSeat.price,
            status = savedReservation.status.name,
            expiresAt = savedReservation.expiresAt
        )
    }

    @Transactional
    override fun cancelReservation(command: CancelReservationCommand) {
        val reservation = reservationRepository.findById(command.reservationId)
            ?: throw ReservationNotFoundException("예약을 찾을 수 없습니다.")

        if (reservation.userId != command.userId) {
            throw InvalidRequestException("본인의 예약만 취소할 수 있습니다.")
        }

        val seat = seatRepository.findById(reservation.seatId)
            ?: throw SeatNotFoundException("좌석을 찾을 수 없습니다.")

        // 도메인 서비스를 통한 예약 취소
        reservationService.cancelReservation(reservation, seat)

        // 저장
        seatRepository.save(seat)
        reservationRepository.deleteById(reservation.reservationId)
    }

    @Transactional(readOnly = true)
    override fun getUserReservations(userId: String): List<ReservationResponse> {
        val reservations = reservationRepository.findByUserId(userId)

        return reservations.map { reservation ->
            val seat = seatRepository.findById(reservation.seatId)!!
            val schedule = scheduleRepository.findById(seat.scheduleId)!!

            ReservationResponse(
                reservationId = reservation.reservationId,
                userId = reservation.userId,
                seatId = seat.seatId,
                seatNumber = seat.seatNumber,
                concertName = schedule.concertName,
                concertDate = schedule.concertDate,
                price = seat.price,
                status = reservation.status.name,
                expiresAt = reservation.expiresAt
            )
        }
    }

    private fun generateReservationId(): Long {
        return System.currentTimeMillis()
    }
}
