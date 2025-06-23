package com.example.tdd.adapter.`in`.web

import com.example.tdd.application.port.`in`.QueueTokenUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 대기열 토큰 발급 및 조회 컨트롤러
 */
@RestController
@RequestMapping("/api/tokens")
class TokenController(
    private val queueTokenUseCase: QueueTokenUseCase
) {

    /**
     * 대기열 토큰 발급 API
     */
    @PostMapping
    fun issueToken(@RequestBody request: TokenRequest): ResponseEntity<TokenResponse> {
        val tokenResponse = queueTokenUseCase.issueToken(request.userId)

        return ResponseEntity.ok(
            TokenResponse(
                token = tokenResponse.token,
                status = tokenResponse.status.name,
                rank = tokenResponse.rank,
                expiresIn = tokenResponse.expiresIn
            )
        )
    }
}

/**
 * 토큰 발급 요청 DTO
 */
data class TokenRequest(
    val userId: String
)

/**
 * 토큰 발급 응답 DTO
 */
data class TokenResponse(
    val token: String,
    val status: String,  // "WAITING" or "ACTIVE"
    val rank: Int?,      // 대기 순번, 활성화된 경우 null
    val expiresIn: Int   // 토큰 만료 시간(초)
)
