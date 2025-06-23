package com.example.tdd.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

/**
 * 사용자 엔티티
 * 데이터베이스의 USERS 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "USERS")
class UserEntity(
    @Id
    @Column(name = "user_id")
    val userId: String,

    @Column(name = "balance", nullable = false)
    var balance: BigDecimal = BigDecimal.ZERO
)
