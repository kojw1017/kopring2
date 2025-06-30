package com.example.tdd.adapter.out.redis

import com.example.tdd.application.port.out.QueueRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Redis를 활용한 대기열 관리 어댑터
 */
@Component
class RedisQueueManagerAdapter(
    private val redisTemplate: RedisTemplate<String, String>
) : QueueRepository {

    companion object {
        private const val WAITING_QUEUE_KEY = "waiting-queue"
        private const val ACTIVE_TOKENS_KEY = "active-tokens"
    }

    override fun addToQueue(userId: String, token: String): Long {
        val operations = redisTemplate.opsForZSet()
        
        // 현재 타임스탬프를 점수로 사용하여 시간 순으로 정렬
        val score = Instant.now().toEpochMilli().toDouble()
        operations.add(WAITING_QUEUE_KEY, token, score)

        // 대기열 위치 반환 (1-based)
        return getQueuePosition(token)
    }

    override fun getQueuePosition(token: String): Long {
        // 활성 토큰인지 확인
        if (redisTemplate.opsForSet().isMember(ACTIVE_TOKENS_KEY, token) == true) {
            return 0L
        }

        val operations = redisTemplate.opsForZSet()
        return operations.rank(WAITING_QUEUE_KEY, token)?.plus(1) ?: Long.MAX_VALUE
    }

    override fun activateWaitingTokens(count: Int): List<String> {
        val zSetOperations = redisTemplate.opsForZSet()
        val setOperations = redisTemplate.opsForSet()
        
        // 대기열에서 상위 N개 토큰 가져오기
        val tokens = zSetOperations.range(WAITING_QUEUE_KEY, 0, count - 1L) ?: emptySet()
        
        val activatedTokens = mutableListOf<String>()
        tokens.forEach { token ->
            // 대기열에서 제거
            zSetOperations.remove(WAITING_QUEUE_KEY, token)
            // 활성 토큰으로 추가
            setOperations.add(ACTIVE_TOKENS_KEY, token)
            activatedTokens.add(token)
        }
        
        return activatedTokens
    }

    override fun removeFromQueue(token: String) {
        redisTemplate.opsForZSet().remove(WAITING_QUEUE_KEY, token)
        redisTemplate.opsForSet().remove(ACTIVE_TOKENS_KEY, token)
    }

    override fun getQueueSize(): Long {
        return redisTemplate.opsForZSet().size(WAITING_QUEUE_KEY) ?: 0L
    }

    override fun getActiveTokenCount(): Long {
        return redisTemplate.opsForSet().size(ACTIVE_TOKENS_KEY) ?: 0L
    }
}