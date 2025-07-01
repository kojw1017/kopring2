package com.example.tdd.application.port.out

import com.example.tdd.domain.model.User
import java.math.BigDecimal

/**
 * 사용자 엔티티에 대한 영속성 관리를 위한 아웃바운드 포트
 */
interface UserRepositoryPort {
    /**
     * 사용자 ID로 사용자를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 찾은 사용자 객체 또는 null
     */
    fun findByUserId(userId: String): User?

    /**
     * 사용자 객체를 저장합니다.
     *
     * @param user 저장할 사용자 객체
     * @return 저장된 사용자 객체
     */
    fun save(user: User): User

    /**
     * 사용자의 잔액을 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param balance 새로운 잔액
     * @return 업데이트 성공 여부
     */
    fun updateBalance(userId: String, balance: BigDecimal): Boolean
}
