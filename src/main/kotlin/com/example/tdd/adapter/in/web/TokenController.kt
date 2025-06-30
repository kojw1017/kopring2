package com.example.tdd.adapter.`in`.web

import com.example.tdd.application.port.`in`.QueueTokenUseCase
import com.example.tdd.application.port.`in`.IssueTokenCommand
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 큐 토큰 관리 컨트롤러
 */
@RestController
@RequestMapping("/api/queue")
class TokenController(
    private val queueTokenUseCase: QueueTokenUseCase
) {

    /**
     * 큐 토큰 발급 API
     */
    @PostMapping("/token")
    fun issueToken(@RequestBody request: IssueTokenRequest): ResponseEntity<TokenDto> {
        val command = IssueTokenCommand(userId = request.userId)
        val tokenResponse = queueTokenUseCase.issueToken(command)

        return ResponseEntity.ok(
            TokenDto(
                token = tokenResponse.token,
                queuePosition = tokenResponse.queuePosition,
                estimatedWaitTime = tokenResponse.estimatedWaitTime,
                isActive = tokenResponse.isActive
            )
        )
    }

    /**
     * 토큰 상태 조회 API
     */
    @GetMapping("/token/{token}")
    fun getTokenStatus(@PathVariable token: String): ResponseEntity<TokenStatusDto> {
        val statusResponse = queueTokenUseCase.getTokenStatus(token)

        return ResponseEntity.ok(
            TokenStatusDto(
                token = statusResponse.token,
                isValid = statusResponse.isValid,
                isActive = statusResponse.isActive,
                queuePosition = statusResponse.queuePosition,
                estimatedWaitTime = statusResponse.estimatedWaitTime,
                ttl = statusResponse.ttl
            )
        )
    }

    /**
     * 대기 토큰 활성화 API (관리자용)
     */
    @PostMapping("/activate")
    fun activateWaitingTokens(): ResponseEntity<TokenActivationDto> {
        val result = queueTokenUseCase.activateWaitingTokens()

        return ResponseEntity.ok(
            TokenActivationDto(
                activatedCount = result.activatedCount,
                totalWaitingCount = result.totalWaitingCount
            )
        )
    }
}

/**
 * 토큰 발급 요청 DTO
 */
data class IssueTokenRequest(
    val userId: String
)

/**
 * 토큰 정보 DTO
 */
data class TokenDto(
    val token: String,
    val queuePosition: Long,
    val estimatedWaitTime: Long,
    val isActive: Boolean
)

/**
 * 토큰 상태 DTO
 */
data class TokenStatusDto(
    val token: String,
    val isValid: Boolean,
    val isActive: Boolean,
    val queuePosition: Long,
    val estimatedWaitTime: Long,
    val ttl: Long
)

/**
 * 토큰 활성화 결과 DTO
 */
data class TokenActivationDto(
    val activatedCount: Int,
    val totalWaitingCount: Long
)
