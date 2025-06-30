package com.example.tdd.application.service

import java.util.*

/**
 * JWT 토큰의 클레임 정보를 추상화한 인터페이스
 */
interface Claims {
    /**
     * 토큰의 주체 (사용자 ID)
     */
    val subject: String

    /**
     * 토큰 만료 시간
     */
    val expiration: Date

    /**
     * 특정 키에 해당하는 클레임 값을 조회합니다.
     *
     * @param key 클레임 키
     * @return 클레임 값
     */
    fun get(key: String): Any?
}
