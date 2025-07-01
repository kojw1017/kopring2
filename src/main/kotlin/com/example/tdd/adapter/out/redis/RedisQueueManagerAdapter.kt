package com.example.tdd.adapter.out.redis

import com.example.tdd.application.port.out.QueueManagerPort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Redis를 활용한 대기열 관리 어댑터
 */
@Component
class RedisQueueManagerAdapter(
    private val redisTemplate: RedisTemplate<String, String>
) : QueueManagerPort {

    companion object {
        private const val WAITING_QUEUE_KEY = "waiting-queue"
        private const val ACTIVE_USERS_KEY = "active-users"
    }

    /**
     * 사용자를 대기열에 추가합니다.
     * Redis Sorted Set을 활용하여 사용자를 추가하고 현재 순위를 반환합니다.
     */
    override fun addToQueue(userId: String): Int {
        val operations = redisTemplate.opsForZSet()

        // 이미 대기열에 있는지 확인
        val currentRank = operations.rank(WAITING_QUEUE_KEY, userId)
        if (currentRank != null) {
            // 이미 대기열에 있는 경우 현재 순위 반환 (0-based를 1-based로 변환)
            return currentRank.toInt() + 1
        }

        // 현재 타임스탬프를 점수로 사용하여 시간 순으로 정렬
        val score = Instant.now().toEpochMilli().toDouble()
        operations.add(WAITING_QUEUE_KEY, userId, score)

        // 새로 추가된 사용자의 순위 조회 (0-based를 1-based로 변환)
        return operations.rank(WAITING_QUEUE_KEY, userId)?.toInt()?.plus(1) ?: -1
    }

    /**
     * 사용자의 현재 대기열 순번을 조회합니다.
     */
    override fun getQueuePosition(userId: String): Int? {
        // 이미 활성화되었는지 확인
        if (isActive(userId)) {
            return null
        }

        val operations = redisTemplate.opsForZSet()
        // 대기열에서의 순위 조회 (0-based를 1-based로 변환)
        return operations.rank(WAITING_QUEUE_KEY, userId)?.toInt()?.plus(1)
    }

    /**
     * 사용자가 활성 상태인지 확인합니다.
     */
    override fun isActive(userId: String): Boolean {
        val operations = redisTemplate.opsForSet()
        return operations.isMember(ACTIVE_USERS_KEY, userId) ?: false
    }

    /**
     * 사용자를 활성 상태로 전환합니다.
     */
    override fun activateUser(userId: String): Boolean {
        val operations = redisTemplate.opsForSet()
        val added = operations.add(ACTIVE_USERS_KEY, userId) ?: 0
        return added > 0
    }

    /**
     * 사용자의 활성 상태를 해제합니다.
     */
    override fun deactivateUser(userId: String): Boolean {
        val setOperations = redisTemplate.opsForSet()
        val zSetOperations = redisTemplate.opsForZSet()

        // 활성 유저 목록에서 제거
        val removed = setOperations.remove(ACTIVE_USERS_KEY, userId) ?: 0

        // 대기열에서도 제거
        zSetOperations.remove(WAITING_QUEUE_KEY, userId)

        return removed > 0
    }

    /**
     * 대기열에서 다음 활성화할 사용자 목록을 가져옵니다.
     */
    override fun getNextActiveUsers(count: Int): List<String> {
        val operations = redisTemplate.opsForZSet()

        // 대기열의 상위 N명을 조회
        val users = operations.range(WAITING_QUEUE_KEY, 0, count - 1.toLong())

        return users?.toList() ?: emptyList()
    }
}
