package com.example.tdd.domain.repository

import com.example.tdd.domain.model.User
import java.util.UUID

/**
 * 사용자 리포지토리 인터페이스
 */
interface UserRepository {
    /**
     * 사용자 ID로 사용자 조회
     */
    fun findById(id: UUID): User?

    /**
     * 사용자 저장
     */
    fun save(user: User): User
}
