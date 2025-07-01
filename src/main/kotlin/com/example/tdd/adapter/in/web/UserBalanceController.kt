package com.example.tdd.adapter.`in`.web

import com.example.tdd.application.port.`in`.ChargeBalanceCommand
import com.example.tdd.application.port.`in`.UserBalanceUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

/**
 * 사용자 잔액 관리 컨트롤러
 */
@RestController
@RequestMapping("/api/users/balance")
class UserBalanceController(
    private val userBalanceUseCase: UserBalanceUseCase
) {

    /**
     * 잔액 충전 API
     */
    @PatchMapping
    fun chargeBalance(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody request: ChargeBalanceRequest
    ): ResponseEntity<BalanceResponse> {
        // 토큰에서 사용자 ID 추출 (실제 구현에서는 JWT 검증 로직 필요)
        val userId = extractUserId(authHeader)

        // 충전 명령 생성
        val command = ChargeBalanceCommand(
            userId = userId,
            amount = request.amount
        )

        // 유스케이스 호출
        val result = userBalanceUseCase.chargeBalance(command)

        // 응답 생성
        return ResponseEntity.ok(
            BalanceResponse(
                balance = result.balance,
                message = "충전이 완료되었습니다."
            )
        )
    }

    /**
     * 잔액 조회 API
     */
    @GetMapping
    fun getBalance(@RequestHeader("Authorization") authHeader: String): ResponseEntity<BalanceResponse> {
        // 토큰에서 사용자 ID 추출
        val userId = extractUserId(authHeader)

        // 유스케이스 호출
        val result = userBalanceUseCase.getBalance(userId)

        // 응답 생성
        return ResponseEntity.ok(
            BalanceResponse(
                balance = result.balance,
                message = null
            )
        )
    }

    /**
     * 토큰에서 사용자 ID를 추출합니다.
     * 실제 구현에서는 JWT 검증 및 클레임 추출 로직이 필요합니다.
     */
    private fun extractUserId(authHeader: String): String {
        // "Bearer " 접두사 제거
        val token = authHeader.replace("Bearer ", "")

        // 실제 구현에서는 여기서 JWT 검증 및 사용자 ID 추출
        // 예시 코드에서는 단순화를 위해 더미 값 반환
        return "dummy-user-id"
    }
}

/**
 * 잔액 충전 요청 DTO
 */
data class ChargeBalanceRequest(
    val amount: BigDecimal
)

/**
 * 잔액 응답 DTO
 */
data class BalanceResponse(
    val balance: BigDecimal,
    val message: String?
)
