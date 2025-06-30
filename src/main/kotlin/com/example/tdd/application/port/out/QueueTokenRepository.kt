package com.example.tdd.application.port.out

/**
 * 큐 토큰 관리를 위한 아웃바운드 포트
 */
interface QueueTokenRepository {
    fun generateToken(userId: String): String
    fun isValidToken(token: String): Boolean
    fun getTokenPosition(token: String): Long
    fun activateToken(token: String)
    fun expireToken(token: String)
    fun findActiveTokensByUserId(userId: String): List<String>
    fun getTokenTtl(token: String): Long
}
