package com.example.tdd.adapter.out.persistence.repository

import com.example.tdd.adapter.out.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 사용자 JPA 리포지토리
 */
interface UserJpaRepository : JpaRepository<UserEntity, String> {
    fun findByUserId(userId: String): UserEntity?
    fun existsByUserId(userId: String): Boolean
}