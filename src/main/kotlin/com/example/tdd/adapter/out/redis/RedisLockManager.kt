package com.example.tdd.adapter.out.redis

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

@Component
@ConditionalOnBean(RedissonClient::class)
@ConditionalOnProperty(name = ["spring.redis.enabled"], havingValue = "true", matchIfMissing = false)
class RedisLockManager(
    private val redissonClient: RedissonClient
) {
    /**
     * 지정된 키에 대한 락을 획득하고, 작업을 수행한 후 락을 해제합니다.
     *
     * @param key 락을 걸 키
     * @param waitTime 락 획득을 위해 대기할 시간(초)
     * @param leaseTime 락을 유지할 시간(초)
     * @param supplier 락을 획득한 상태에서 실행할 작업
     * @return 작업 결과
     */
    fun <T> executeWithLock(
        key: String,
        waitTime: Long = 10,
        leaseTime: Long = 5,
        supplier: Supplier<T>
    ): T {
        val lock: RLock = redissonClient.getLock("lock:$key")

        try {
            // 락 획득 시도
            val isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)

            if (!isLocked) {
                throw IllegalStateException("락 획득에 실패했습니다: $key")
            }

            // 작업 수행
            return supplier.get()
        } finally {
            // 락이 현재 스레드에 의해 보유되고 있는 경우에만 해제
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}
