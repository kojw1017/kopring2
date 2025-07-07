package com.example.tdd.adapter.out.redis

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier
import java.util.logging.Logger

/**
 * 로컬 환경에서 사용 가능한 락 매니저
 * Redis가 없는 환경에서 테스트 용도로 사용
 */
@Component
@ConditionalOnMissingBean(RedisLockManager::class)
@ConditionalOnProperty(name = ["spring.redis.enabled"], havingValue = "false", matchIfMissing = true)
class LocalLockManager {

    private val logger = Logger.getLogger(this::class.java.name)
    private val locks = ConcurrentHashMap<String, ReentrantLock>()

    /**
     * 지정된 키에 대한 락을 획득하고, 작업을 수행한 후 락을 해제합니다.
     *
     * @param key 락을 걸 키
     * @param waitTime 락 획득을 위해 대기할 시간(초)
     * @param leaseTime 락을 유지할 시간(초) - 로컬 락에서는 무시됨
     * @param supplier 락을 획득한 상태에서 실행할 작업
     * @return 작업 결과
     */
    fun <T> executeWithLock(
        key: String,
        waitTime: Long = 10,
        leaseTime: Long = 5, // 로컬 락에서는 사용되지 않음
        supplier: Supplier<T>
    ): T {
        logger.info("로컬 락 획득 시도: $key")
        val lock = locks.computeIfAbsent(key) { ReentrantLock() }

        try {
            // 락 획득 시도
            val isLocked = lock.tryLock(waitTime, TimeUnit.SECONDS)

            if (!isLocked) {
                throw IllegalStateException("락 획득에 실패했습니다: $key")
            }

            logger.info("로컬 락 획득 성공: $key")
            // 작업 수행
            return supplier.get()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IllegalStateException("락 획득 중 인터럽트 발생: $key", e)
        } finally {
            // 락이 현재 스레드에 의해 보유되고 있는 경우에만 해제
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
                logger.info("로컬 락 해제: $key")
            }
        }
    }
}
