package com.example.tdd.adapter.out.persistence.adapter

import com.example.tdd.adapter.out.persistence.entity.UserEntity
import com.example.tdd.adapter.out.persistence.repository.UserJpaRepository
import com.example.tdd.domain.model.User
import com.example.tdd.domain.repository.UserRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserRepositoryAdapter(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {
    override fun findById(id: UUID): User? {
        return userJpaRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun save(user: User): User {
        val userEntity = UserEntity.fromDomain(user)
        val savedEntity = userJpaRepository.save(userEntity)
        return savedEntity.toDomain()
    }
}
