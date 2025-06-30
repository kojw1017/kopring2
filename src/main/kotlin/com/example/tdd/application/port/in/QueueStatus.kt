package com.example.tdd.application.port.`in`

/**
 * 대기열 상태를 나타내는 열거형
 */
enum class QueueStatus {
    /**
     * 대기 중 상태
     */
    WAITING,

    /**
     * 활성 상태 (서비스 이용 가능)
     */
    ACTIVE,

    /**
     * 만료된 상태
     */
    EXPIRED
}
