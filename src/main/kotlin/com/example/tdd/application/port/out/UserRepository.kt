package com.example.tdd.application.port.out

import com.example.tdd.domain.model.User

/**
 * 사용자 데이터 접근을 위한 아웃바운드 포트
 */
interface UserRepository {
    fun findById(userId: String): User?
    fun save(user: User): User
    fun existsById(userId: String): Boolean
}
