package com.example.tdd.adapter.out.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    val id: UUID,

    val balance: Long
) {
    companion object {
        fun fromDomain(domain: com.example.tdd.domain.model.User): UserEntity {
            return UserEntity(
                id = domain.id,
                balance = domain.balance
            )
        }
    }

    fun toDomain(): com.example.tdd.domain.model.User {
        return com.example.tdd.domain.model.User.of(
            id = id,
            balance = balance
        )
    }
}
