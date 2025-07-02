package com.example.tdd.adapter.out.redis

import com.example.tdd.application.port.out.LockManagerPort
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * Redis를 활용한 분산 락 관리 어댑터
 */
@Component
class RedisLockManagerAdapter(
    private val redisTemplate: RedisTemplate<String, String>
) : LockManagerPort {

    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val LOCK_PREFIX = "lock:"
    }

    /**
     * 지정된 리소스에 대한 락을 획득합니다.
     */
    override fun acquireLock(resourceId: String, ownerId: String, timeoutMs: Long): Boolean {
        val lockKey = LOCK_PREFIX + resourceId
        return try {
            val success = redisTemplate.opsForValue().setIfAbsent(lockKey, ownerId, timeoutMs, TimeUnit.MILLISECONDS) ?: false
            if (success) {
                log.debug("Redis 락 획득 성공: key='{}', owner='{}'", lockKey, ownerId)
            } else {
                log.warn("Redis 락 획득 실패: key='{}'가 이미 존재합니다.", lockKey)
            }
            success
        } catch (e: Exception) {
            log.error("Redis 락 획득 중 예외 발생: key='{}'", lockKey, e)
            false
        }
    }

    /**
     * 지정된 리소스의 락을 해제합니다.
     */
    override fun releaseLock(resourceId: String, ownerId: String): Boolean {
        val lockKey = LOCK_PREFIX + resourceId
        return try {
            val currentOwner = redisTemplate.opsForValue().get(lockKey)

            if (currentOwner == ownerId) {
                redisTemplate.delete(lockKey)
                log.debug("Redis 락 해제 성공: key='{}', owner='{}'", lockKey, ownerId)
                true
            } else if (currentOwner != null) {
                log.warn("Redis 락 해제 실패: 소유자가 일치하지 않음. key='{}', expectedOwner='{}', actualOwner='{}'", lockKey, ownerId, currentOwner)
                false
            } else {
                log.warn("Redis 락 해제 실패: 락이 존재하지 않음. key='{}'", lockKey)
                false
            }
        } catch (e: Exception) {
            log.error("Redis 락 해제 중 예외 발생: key='{}'", lockKey, e)
            false
        }
    }
}
