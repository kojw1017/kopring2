package com.example.tdd.application.port.out

import com.example.tdd.domain.model.Schedule
import com.example.tdd.domain.model.Seat
import com.example.tdd.domain.model.SeatStatus

/**
 * 콘서트 스케줄 엔티티에 대한 영속성 관리를 위한 아웃바운드 포트
 */
interface ScheduleRepositoryPort {
    /**
     * 사용 가능한 모든 콘서트 일정을 조회합니다.
     *
     * @return 콘서트 일정 목록
     */
    fun findAllAvailable(): List<Schedule>

    /**
     * ID로 콘서트 일정을 조회합니다.
     *
     * @param scheduleId 조회할 일정 ID
     * @return 찾은 일정 객체 또는 null
     */
    fun findById(scheduleId: Long): Schedule?
}

/**
 * 좌석 엔티티에 대한 영속성 관리를 위한 아웃바운드 포트
 */
interface SeatRepositoryPort {
    /**
     * 특정 일정의 모든 좌석을 조회합니다.
     *
     * @param scheduleId 조회할 일정 ID
     * @return 좌석 목록
     */
    fun findAllByScheduleId(scheduleId: Long): List<Seat>

    /**
     * 특정 일정과 좌석 번호로 좌석을 조회합니다.
     *
     * @param scheduleId 일정 ID
     * @param seatNumber 좌석 번호
     * @return 찾은 좌석 객체 또는 null
     */
    fun findByScheduleIdAndSeatNumber(scheduleId: Long, seatNumber: Int): Seat?

    /**
     * 좌석 ID로 좌석을 조회합니다.
     *
     * @param seatId 좌석 ID
     * @return 찾은 좌석 객체 또는 null
     */
    fun findById(seatId: Long): Seat?

    /**
     * 좌석 상태를 업데이트합니다.
     *
     * @param seatId 좌석 ID
     * @param status 새로운 상태
     * @return 업데이트 성공 여부
     */
    fun updateStatus(seatId: Long, status: SeatStatus): Boolean

    /**
     * 좌석 객체를 저장합니다.
     *
     * @param seat 저장할 좌석 객체
     * @return 저장된 좌석 객체
     */
    fun save(seat: Seat): Seat
}
