package com.example.tdd.application.port.out

/**
 * 대기열 관리를 위한 아웃바운드 포트
 */
interface QueueRepository {
    fun addToQueue(userId: String, token: String): Long
    fun getQueuePosition(token: String): Long
    fun activateWaitingTokens(count: Int): List<String>
    fun removeFromQueue(token: String)
    fun getQueueSize(): Long
    fun getActiveTokenCount(): Long
}