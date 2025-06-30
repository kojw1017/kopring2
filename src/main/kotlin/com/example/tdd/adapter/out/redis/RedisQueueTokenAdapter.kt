package com.example.tdd.adapter.out.redis

import com.example.tdd.application.port.out.QueueTokenRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Redis 기반 큐 토큰 Repository 구현
 */
@Component
class RedisQueueTokenAdapter(
    private val redisTemplate: RedisTemplate<String, String>
) : QueueTokenRepository {

    companion object {
        private const val TOKEN_PREFIX = "token:"
        private const val USER_TOKEN_PREFIX = "user_token:"
        private const val TOKEN_TTL_HOURS = 1L
    }

    override fun generateToken(userId: String): String {
        val token = UUID.randomUUID().toString()
        val tokenKey = TOKEN_PREFIX + token
        val userTokenKey = USER_TOKEN_PREFIX + userId

        // 토큰 정보 저장 (1시간 TTL)
        redisTemplate.opsForValue().set(tokenKey, userId, Duration.ofHours(TOKEN_TTL_HOURS))
        redisTemplate.opsForValue().set(userTokenKey, token, Duration.ofHours(TOKEN_TTL_HOURS))

        return token
    }

    override fun isValidToken(token: String): Boolean {
        val tokenKey = TOKEN_PREFIX + token
        return redisTemplate.hasKey(tokenKey)
    }

    override fun getTokenPosition(token: String): Long {
        // 이 메서드는 QueueRepository에서 처리하므로 기본값 반환
        return 0L
    }

    override fun activateToken(token: String) {
        val tokenKey = TOKEN_PREFIX + token
        val userId = redisTemplate.opsForValue().get(tokenKey) ?: return

        // 활성 토큰으로 마킹 (TTL 연장)
        redisTemplate.opsForValue().set(tokenKey, userId, Duration.ofHours(TOKEN_TTL_HOURS))
    }

    override fun expireToken(token: String) {
        val tokenKey = TOKEN_PREFIX + token
        val userId = redisTemplate.opsForValue().get(tokenKey)

        if (userId != null) {
            val userTokenKey = USER_TOKEN_PREFIX + userId
            redisTemplate.delete(tokenKey)
            redisTemplate.delete(userTokenKey)
        }
    }

    override fun findActiveTokensByUserId(userId: String): List<String> {
        val userTokenKey = USER_TOKEN_PREFIX + userId
        val token = redisTemplate.opsForValue().get(userTokenKey)

        return if (token != null && isValidToken(token)) {
            listOf(token)
        } else {
            emptyList()
        }
    }

    override fun getTokenTtl(token: String): Long {
        val tokenKey = TOKEN_PREFIX + token
        return redisTemplate.getExpire(tokenKey, TimeUnit.SECONDS)
    }
}
