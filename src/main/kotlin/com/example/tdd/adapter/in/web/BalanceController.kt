package com.example.tdd.adapter.`in`.web

import com.example.tdd.application.port.`in`.BalanceManagementUseCase
import com.example.tdd.application.port.`in`.BalanceResponse
import com.example.tdd.application.port.`in`.ChargeBalanceCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/balance")
@Tag(name = "잔액 관리", description = "사용자 잔액 충전 및 조회 API")
class BalanceController(
    private val balanceManagementUseCase: BalanceManagementUseCase
) {

    @PostMapping("/charge")
    @Operation(
        summary = "잔액 충전",
        description = "사용자의 잔액을 충전합니다."
    )
    fun chargeBalance(
        @Valid @RequestBody request: ChargeBalanceRequest
    ): ResponseEntity<BalanceResponse> {
        val command = ChargeBalanceCommand(
            userId = request.userId,
            amount = request.amount
        )
        val response = balanceManagementUseCase.chargeBalance(command)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{userId}")
    @Operation(
        summary = "잔액 조회",
        description = "사용자의 현재 잔액을 조회합니다."
    )
    fun getBalance(
        @PathVariable userId: UUID
    ): ResponseEntity<BalanceResponse> {
        val response = balanceManagementUseCase.getBalance(userId)
        return ResponseEntity.ok(response)
    }
}

data class ChargeBalanceRequest(
    @field:NotNull(message = "사용자 ID가 필요합니다.")
    val userId: UUID,

    @field:NotNull(message = "충전 금액이 필요합니다.")
    @field:Min(value = 1, message = "충전 금액은 1 이상이어야 합니다.")
    val amount: Long
)
