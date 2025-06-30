package com.example.tdd.application.service

import com.example.tdd.application.port.`in`.QueueStatus

/**
 * 토큰 생성 및 검증을 위한 인터페이스
 */
interface TokenProvider {
    /**
     * 사용자 ID와 상태를 기반으로 JWT 토큰을 생성합니다.
     *
     * @param userId 사용자 ID
     * @param status 대기열 상태
     * @return 생성된 JWT 토큰
     */
    fun createToken(userId: String, status: QueueStatus): String

    /**
     * JWT 토큰을 검증하고 클레임 정보를 반환합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰의 클레임 정보
     * @throws io.jsonwebtoken.JwtException 토큰이 유효하지 않은 경우
     */
    fun validateToken(token: String): Claims

    /**
     * 대기열 상태에 따른 토큰 유효 시간을 반환합니다.
     *
     * @param status 대기열 상태
     * @return 토큰 유효 시간 (초)
     */
    fun getTokenValidity(status: QueueStatus): Int
}
