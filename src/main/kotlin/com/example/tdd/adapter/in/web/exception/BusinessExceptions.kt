package com.example.tdd.adapter.`in`.web.exception

/**
 * 비즈니스 로직 관련 기본 예외 클래스
 */
abstract class BusinessException(
    override val message: String,
    val path: String? = null
) : RuntimeException(message)

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 */
class ResourceNotFoundException(
    message: String = "요청한 리소스를 찾을 수 없습니다.",
    path: String? = null
) : BusinessException(message, path)

/**
 * 유효하지 않은 요청 데이터에 대한 예외
 */
class InvalidRequestException(
    message: String = "유효하지 않은 요청입니다.",
    path: String? = null
) : BusinessException(message, path)

/**
 * 동시성 문제로 인한 예외
 */
class ConcurrentModificationException(
    message: String = "다른 사용자가 같은 리소스를 수정하고 있습니다.",
    path: String? = null
) : BusinessException(message, path)

/**
 * 잔액 부족 시 발생하는 예외
 */
class InsufficientBalanceException(
    message: String = "잔액이 부족합니다.",
    path: String? = null
) : BusinessException(message, path)

/**
 * 토큰 관련 예외
 */
class TokenException(
    message: String = "토큰이 유효하지 않습니다.",
    path: String? = null
) : BusinessException(message, path)

/**
 * 예약 만료 예외
 */
class ReservationExpiredException(
    message: String = "예약이 만료되었습니다.",
    path: String? = null
) : BusinessException(message, path)

/**
 * 대기열 관련 예외
 */
class QueueException(
    message: String = "대기열 처리 중 오류가 발생했습니다.",
    path: String? = null
) : BusinessException(message, path)
