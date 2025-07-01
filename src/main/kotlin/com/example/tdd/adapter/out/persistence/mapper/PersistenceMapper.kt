package com.example.tdd.adapter.out.persistence.mapper

import com.example.tdd.adapter.out.persistence.entity.PaymentEntity
import com.example.tdd.adapter.out.persistence.entity.ReservationEntity
import com.example.tdd.adapter.out.persistence.entity.ReservationStatusEntity
import com.example.tdd.adapter.out.persistence.entity.ScheduleEntity
import com.example.tdd.adapter.out.persistence.entity.SeatEntity
import com.example.tdd.adapter.out.persistence.entity.SeatStatusEntity
import com.example.tdd.adapter.out.persistence.entity.UserEntity
import com.example.tdd.domain.model.Payment
import com.example.tdd.domain.model.Reservation
import com.example.tdd.domain.model.ReservationStatus
import com.example.tdd.domain.model.Schedule
import com.example.tdd.domain.model.Seat
import com.example.tdd.domain.model.SeatStatus
import com.example.tdd.domain.model.User
import org.springframework.stereotype.Component

/**
 * 도메인 모델과 JPA 엔티티 간의 변환을 담당하는 매퍼
 */
@Component
class PersistenceMapper {

    // User 매핑 메서드
    fun mapToDomainUser(entity: UserEntity): User {
        return User(
            userId = entity.userId,
            _balance = entity.balance
        )
    }

    fun mapToEntityUser(domain: User): UserEntity {
        return UserEntity(
            userId = domain.userId,
            balance = domain.balance
        )
    }

    // Schedule 매핑 메서드
    fun mapToDomainSchedule(entity: ScheduleEntity): Schedule {
        return Schedule(
            scheduleId = entity.scheduleId,
            concertName = entity.concertName,
            concertDate = entity.concertDate
        )
    }

    fun mapToEntitySchedule(domain: Schedule): ScheduleEntity {
        return ScheduleEntity(
            scheduleId = domain.scheduleId,
            concertName = domain.concertName,
            concertDate = domain.concertDate
        )
    }

    // Seat 매핑 메서드
    fun mapToDomainSeat(entity: SeatEntity): Seat {
        return Seat(
            seatId = entity.seatId,
            scheduleId = entity.schedule.scheduleId,
            seatNumber = entity.seatNumber,
            _status = mapToDomainSeatStatus(entity.status),
            price = entity.price
        )
    }

    fun mapToEntitySeat(domain: Seat, scheduleEntity: ScheduleEntity): SeatEntity {
        return SeatEntity(
            seatId = domain.seatId,
            schedule = scheduleEntity,
            seatNumber = domain.seatNumber,
            status = mapToEntitySeatStatus(domain.status),
            price = domain.price
        )
    }

    // Reservation 매핑 메서드
    fun mapToDomainReservation(entity: ReservationEntity): Reservation {
        return Reservation(
            reservationId = entity.reservationId,
            userId = entity.userId,
            seatId = entity.seat.seatId,
            _status = mapToDomainReservationStatus(entity.status),
            expiresAt = entity.expiresAt
        )
    }

    fun mapToEntityReservation(domain: Reservation, seatEntity: SeatEntity): ReservationEntity {
        return ReservationEntity(
            reservationId = domain.reservationId,
            userId = domain.userId,
            seat = seatEntity,
            status = mapToEntityReservationStatus(domain.status),
            expiresAt = domain.expiresAt
        )
    }

    // Payment 매핑 메서드
    fun mapToDomainPayment(entity: PaymentEntity): Payment {
        return Payment(
            paymentId = entity.paymentId,
            reservationId = entity.reservation.reservationId,
            amount = entity.amount,
            paymentDate = entity.paymentDate
        )
    }

    fun mapToEntityPayment(domain: Payment, reservationEntity: ReservationEntity): PaymentEntity {
        return PaymentEntity(
            paymentId = domain.paymentId,
            reservation = reservationEntity,
            amount = domain.amount,
            paymentDate = domain.paymentDate
        )
    }

    // 상태 변환 메서드
    fun mapToDomainSeatStatus(entityStatus: SeatStatusEntity): SeatStatus {
        return when (entityStatus) {
            SeatStatusEntity.AVAILABLE -> SeatStatus.AVAILABLE
            SeatStatusEntity.RESERVED -> SeatStatus.RESERVED
            SeatStatusEntity.SOLD -> SeatStatus.SOLD
        }
    }

    fun mapToEntitySeatStatus(domainStatus: SeatStatus): SeatStatusEntity {
        return when (domainStatus) {
            SeatStatus.AVAILABLE -> SeatStatusEntity.AVAILABLE
            SeatStatus.RESERVED -> SeatStatusEntity.RESERVED
            SeatStatus.SOLD -> SeatStatusEntity.SOLD
        }
    }

    fun mapToDomainReservationStatus(entityStatus: ReservationStatusEntity): ReservationStatus {
        return when (entityStatus) {
            ReservationStatusEntity.PENDING -> ReservationStatus.PENDING
            ReservationStatusEntity.PAID -> ReservationStatus.PAID
            ReservationStatusEntity.EXPIRED -> ReservationStatus.EXPIRED
        }
    }

    fun mapToEntityReservationStatus(domainStatus: ReservationStatus): ReservationStatusEntity {
        return when (domainStatus) {
            ReservationStatus.PENDING -> ReservationStatusEntity.PENDING
            ReservationStatus.PAID -> ReservationStatusEntity.PAID
            ReservationStatus.EXPIRED -> ReservationStatusEntity.EXPIRED
        }
    }
}
