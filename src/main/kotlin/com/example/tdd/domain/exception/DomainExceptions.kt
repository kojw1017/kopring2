package com.example.tdd.domain.exception

/**
 * 도메인 계층의 기본 예외 클래스
 */
abstract class DomainException(message: String) : RuntimeException(message)

/**
 * 잔액 부족 예외
 */
class InsufficientBalanceException(message: String = "잔액이 부족합니다.") : DomainException(message)

/**
 * 유효하지 않은 요청 예외
 */
class InvalidRequestException(message: String) : DomainException(message)

/**
 * 예약 만료 예외
 */
class ReservationExpiredException(message: String = "예약이 만료되었습니다.") : DomainException(message)

/**
 * 이미 예약된 좌석 예외
 */
class SeatAlreadyReservedException(message: String = "이미 예약된 좌석입니다.") : DomainException(message)

/**
 * 이미 판매된 좌석 예외
 */
class SeatAlreadySoldException(message: String = "이미 판매된 좌석입니다.") : DomainException(message)

/**
 * 토큰 관련 예외
 */
class InvalidTokenException(message: String = "유효하지 않은 토큰입니다.") : DomainException(message)

class TokenExpiredException(message: String = "만료된 토큰입니다.") : DomainException(message)

/**
 * 사용자 관련 예외
 */
class UserNotFoundException(message: String = "사용자를 찾을 수 없습니다.") : DomainException(message)

/**
 * 예약 관련 예외
 */
class ReservationNotFoundException(message: String = "예약을 찾을 수 없습니다.") : DomainException(message)

/**
 * 콘서트 관련 예외
 */
class ConcertNotFoundException(message: String = "콘서트를 찾을 수 없습니다.") : DomainException(message)

class ScheduleNotFoundException(message: String = "콘서트 일정을 찾을 수 없습니다.") : DomainException(message)

class SeatNotFoundException(message: String = "좌석을 찾을 수 없습니다.") : DomainException(message)
