package com.example.tdd.adapter.out.persistence

import com.example.tdd.adapter.out.persistence.mapper.PersistenceMapper
import com.example.tdd.adapter.out.persistence.repository.UserJpaRepository
import com.example.tdd.application.port.out.UserRepositoryPort
import com.example.tdd.domain.model.User
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * 사용자 관련 영속성 어댑터
 * 아웃바운드 포트를 구현하여 도메인 모델과 데이터베이스 간의 상호작용을 담당합니다.
 */
@Component
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
    private val mapper: PersistenceMapper
) : UserRepositoryPort {

    /**
     * 사용자 ID로 사용자를 조회합니다.
     */
    override fun findByUserId(userId: String): User? {
        val userEntity = userJpaRepository.findByUserId(userId) ?: return null
        return mapper.mapToDomainUser(userEntity)
    }

    /**
     * 사용자 객체를 저장합니다.
     */
    override fun save(user: User): User {
        val userEntity = mapper.mapToEntityUser(user)
        val savedEntity = userJpaRepository.save(userEntity)
        return mapper.mapToDomainUser(savedEntity)
    }

    /**
     * 사용자의 잔액을 업데이트합니다.
     */
    override fun updateBalance(userId: String, balance: BigDecimal): Boolean {
        val userEntity = userJpaRepository.findByUserId(userId) ?: return false
        userEntity.balance = balance
        userJpaRepository.save(userEntity)
        return true
    }
}
