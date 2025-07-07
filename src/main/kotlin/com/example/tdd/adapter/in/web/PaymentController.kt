package com.example.tdd.adapter.`in`.web

import com.example.tdd.application.port.`in`.PaymentResponse
import com.example.tdd.application.port.`in`.PaymentUseCase
import com.example.tdd.application.port.`in`.ProcessPaymentCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/payments")
@Tag(name = "결제", description = "결제 처리 API")
class PaymentController(
    private val paymentUseCase: PaymentUseCase
) {

    @PostMapping("/process")
    @Operation(
        summary = "결제 처리",
        description = "임시 예약된 좌석에 대한 결제를 처리합니다."
    )
    fun processPayment(
        @Valid @RequestBody request: ProcessPaymentRequest
    ): ResponseEntity<PaymentResponse> {
        val command = ProcessPaymentCommand(
            token = request.token,
            reservationId = request.reservationId
        )
        val response = paymentUseCase.processPayment(command)
        return ResponseEntity.ok(response)
    }
}

data class ProcessPaymentRequest(
    @field:NotBlank(message = "토큰이 필요합니다.")
    val token: String,

    @field:NotNull(message = "예약 ID가 필요합니다.")
    val reservationId: UUID
)
