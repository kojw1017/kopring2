package com.example.tdd.adapter.`in`.web

import com.example.tdd.adapter.`in`.web.exception.TokenException
import com.example.tdd.application.port.`in`.PaymentCommand
import com.example.tdd.application.port.`in`.PaymentUseCase
import com.example.tdd.application.service.TokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 결제 처리 컨트롤러
 */
@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentUseCase: PaymentUseCase,
    private val tokenProvider: TokenProvider
) {

    /**
     * 결제 처리 API
     */
    @PostMapping
    fun processPayment(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody request: PaymentRequest
    ): ResponseEntity<PaymentResponse> {
        // 토큰에서 사용자 ID 추출 (실제 구현에서는 JWT 검증 로직 필요)
        val userId = extractUserId(authHeader)

        // 결제 명령 생성
        val command = PaymentCommand(
            userId = userId,
            reservationId = request.reservationId
        )

        // 유스케이스 호출
        val result = paymentUseCase.processPayment(command)

        // 응답 생성
        return ResponseEntity.ok(
            PaymentResponse(
                paymentId = result.paymentId,
                message = "결제가 성공적으로 완료되었습니다."
            )
        )
    }

    /**
     * 토큰에서 사용자 ID를 추출합니다.
     * JWT 검증 및 클레임 추출 로직을 수행합니다.
     */
    private fun extractUserId(authHeader: String): String {
        // "Bearer " 접두사 제거
        val token = authHeader.replace("Bearer ", "")

        try {
            // JWT 토큰 검증 및 클레임 추출
            val claims = tokenProvider.validateToken(token)
            return claims.subject
        } catch (e: Exception) {
            throw TokenException("유효하지 않은 토큰입니다.", path = "/api/payments")
        }
    }
}

/**
 * 결제 요청 DTO
 */
data class PaymentRequest(
    val reservationId: Long
)

/**
 * 결제 응답 DTO
 */
data class PaymentResponse(
    val paymentId: Long,
    val message: String
)
