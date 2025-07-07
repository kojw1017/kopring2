package com.example.tdd.domain.repository

import com.example.tdd.domain.model.ConcertDate
import java.time.LocalDate
import java.util.UUID

/**
 * 콘서트 날짜 리포지토리 인터페이스
 */
interface ConcertDateRepository {
    /**
     * ID로 콘서트 날짜 조회
     */
    fun findById(id: UUID): ConcertDate?

    /**
     * 특정 날짜의 콘서트 조회
     */
    fun findByDate(date: LocalDate): ConcertDate?

    /**
     * 사용 가능한 모든 콘서트 날짜 조회
     */
    fun findAllAvailable(): List<ConcertDate>

    /**
     * 콘서트 날짜 저장
     */
    fun save(concertDate: ConcertDate): ConcertDate
}
