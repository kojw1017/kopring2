package com.example.tdd.adapter.out.redis

import com.example.tdd.application.port.out.LockManagerRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * Redis를 활용한 분산 락 관리 어댑터
 */
@Component
class RedisLockManagerAdapter(
    private val redisTemplate: RedisTemplate<String, String>
) : LockManagerRepository {

    companion object {
        private const val LOCK_PREFIX = "lock:"
    }

    /**
     * 지정된 리소스에 대한 락을 획득합니다.
     */
    override fun acquireLock(resourceId: String, ownerId: String, timeoutMs: Long): Boolean {
        val lockKey = LOCK_PREFIX + resourceId
        val operations = redisTemplate.opsForValue()

        // SET NX (Not exists) 명령어를 사용하여 락 획득 시도
        // 이미 키가 존재하면 false를 반환하고, 없으면 값 설정 후 true 반환
        val success = operations.setIfAbsent(lockKey, ownerId, timeoutMs, TimeUnit.MILLISECONDS) ?: false

        return success
    }

    /**
     * 지정된 리소스의 락을 해제합니다.
     */
    override fun releaseLock(resourceId: String, ownerId: String): Boolean {
        val lockKey = LOCK_PREFIX + resourceId
        val operations = redisTemplate.opsForValue()

        // 현재 락 소유자 확인
        val currentOwner = operations.get(lockKey)

        // 현재 락 소유자가 요청자와 일치하는 경우에만 락 해제
        if (currentOwner == ownerId) {
            redisTemplate.delete(lockKey)
            return true
        }

        return false
    }
}
