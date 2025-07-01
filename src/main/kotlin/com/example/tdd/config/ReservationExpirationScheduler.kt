package com.example.tdd.config

import com.example.tdd.application.service.ReservationExpirationService
import com.example.tdd.application.port.out.LockManagerPort
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * 예약 만료 처리를 위한 스케줄러
 * 일정 시간마다 실행되어 만료된 예약을 처리합니다.
 */
@Component
@EnableScheduling
class ReservationExpirationScheduler(
    private val reservationExpirationService: ReservationExpirationService,
    private val lockManager: LockManagerPort
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 30초마다 실행되어 만료된 예약을 처리합니다.
     * 분산 환경에서 중복 실행을 방지하기 위해 분산 락을 사용합니다.
     */
    @Scheduled(fixedRate = 30000) // 30초마다 실행
    fun processExpiredReservations() {
        val lockKey = "reservation-expiration-job"
        val lockOwner = "scheduler-${System.currentTimeMillis()}"

        // 분산 락 획득 시도
        val acquired = lockManager.acquireLock(lockKey, lockOwner, TimeUnit.SECONDS.toMillis(25))

        if (!acquired) {
            logger.debug("다른 서버 인스턴스에서 이미 작업 실행 중")
            return
        }

        try {
            logger.info("만료된 예약 처리 작업 시작")
            // 별도 서비스의 @Transactional 메서드 호출
            reservationExpirationService.processExpiredReservations()
            logger.info("만료된 예약 처리 작업 완료")
        } catch (e: Exception) {
            // 스케줄러에서 발생한 모든 예외를 로깅하지만, 애플리케이션은 계속 실행
            logger.error("예약 만료 처리 작업 중 오류 발생: ${e.message}", e)
        } finally {
            // 작업 완료 후 락 해제
            lockManager.releaseLock(lockKey, lockOwner)
        }
    }
}
