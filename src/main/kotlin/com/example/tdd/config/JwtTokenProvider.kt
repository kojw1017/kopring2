package com.example.tdd.config

import com.example.tdd.application.port.`in`.QueueStatus
import com.example.tdd.application.service.Claims
import com.example.tdd.application.service.TokenProvider
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

/**
 * JWT 기반 토큰 제공자 구현
 */
@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val secretKey: String,

    @Value("\${jwt.token-validity-in-seconds}")
    private val tokenValidityInSeconds: Int,

    @Value("\${jwt.queue-token-validity-in-seconds}")
    private val queueTokenValidityInSeconds: Int
) : TokenProvider {

    // 비밀키를 바이트 배열로 변환하여 서명 키 생성
    private val key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    /**
     * JWT 토큰을 생성합니다.
     */
    override fun createToken(userId: String, status: QueueStatus): String {
        val now = Date()
        val validity = Date(now.time + getTokenValidity(status) * 1000)

        return Jwts.builder()
            .setSubject(userId)
            .claim("status", status.name)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    /**
     * JWT 토큰을 검증하고 클레임을 반환합니다.
     */
    override fun validateToken(token: String): Claims {
        val jwtClaims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        return object : Claims {
            override val subject: String
                get() = jwtClaims.subject

            override val expiration: Date
                get() = jwtClaims.expiration

            override fun get(key: String): Any? = jwtClaims[key]
        }
    }

    /**
     * 토큰 유효 시간을 반환합니다.
     */
    override fun getTokenValidity(status: QueueStatus): Int {
        return if (status == QueueStatus.ACTIVE) {
            tokenValidityInSeconds
        } else {
            queueTokenValidityInSeconds
        }
    }
}
