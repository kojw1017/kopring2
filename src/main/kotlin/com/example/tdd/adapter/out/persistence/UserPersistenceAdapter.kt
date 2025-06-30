package com.example.tdd.adapter.out.persistence

import com.example.tdd.adapter.out.persistence.mapper.PersistenceMapper
import com.example.tdd.adapter.out.persistence.repository.UserJpaRepository
import com.example.tdd.application.port.out.UserRepository
import com.example.tdd.domain.model.User
import org.springframework.stereotype.Component

/**
 * 사용자 관련 영속성 어댑터
 * 아웃바운드 포트를 구현하여 도메인 모델과 데이터베이스 간의 상호작용을 담당합니다.
 */
@Component
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
    private val mapper: PersistenceMapper
) : UserRepository {

    /**
     * 사용자 ID로 사용자를 조회합니다.
     */
    override fun findById(userId: String): User? {
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
     * 사용자 존재 여부를 확인합니다.
     */
    override fun existsById(userId: String): Boolean {
        return userJpaRepository.existsByUserId(userId)
    }
}