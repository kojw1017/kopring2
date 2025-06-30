package com.example.tdd.adapter.`in`.web

import com.example.tdd.application.port.`in`.PaymentUseCase
import com.example.tdd.application.port.`in`.ProcessPaymentCommand
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 결제 처리 컨트롤러
 */
@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentUseCase: PaymentUseCase
) {

    /**
     * 결제 처리 API
     */
    @PostMapping
    fun processPayment(
        @RequestBody request: ProcessPaymentRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<PaymentDto> {
        val command = ProcessPaymentCommand(
            userId = request.userId,
            reservationId = request.reservationId,
            token = token.removePrefix("Bearer ")
        )

        val payment = paymentUseCase.processPayment(command)

        return ResponseEntity.ok(
            PaymentDto(
                paymentId = payment.paymentId,
                reservationId = payment.reservationId,
                userId = payment.userId,
                amount = payment.amount,
                paymentDate = payment.paymentDate,
                status = payment.status
            )
        )
    }

    /**
     * 결제 내역 조회 API
     */
    @GetMapping("/{paymentId}")
    fun getPayment(@PathVariable paymentId: Long): ResponseEntity<PaymentDto> {
        val payment = paymentUseCase.getPayment(paymentId)

        return ResponseEntity.ok(
            PaymentDto(
                paymentId = payment.paymentId,
                reservationId = payment.reservationId,
                userId = payment.userId,
                amount = payment.amount,
                paymentDate = payment.paymentDate,
                status = payment.status
            )
        )
    }

    /**
     * 사용자 결제 내역 목록 조회 API
     */
    @GetMapping
    fun getUserPayments(@RequestParam userId: String): ResponseEntity<List<PaymentDto>> {
        val payments = paymentUseCase.getUserPayments(userId)

        return ResponseEntity.ok(
            payments.map { payment ->
                PaymentDto(
                    paymentId = payment.paymentId,
                    reservationId = payment.reservationId,
                    userId = payment.userId,
                    amount = payment.amount,
                    paymentDate = payment.paymentDate,
                    status = payment.status
                )
            }
        )
    }
}

/**
 * 결제 처리 요청 DTO
 */
data class ProcessPaymentRequest(
    val userId: String,
    val reservationId: Long
)

/**
 * 결제 정보 DTO
 */
data class PaymentDto(
    val paymentId: Long,
    val reservationId: Long,
    val userId: String,
    val amount: BigDecimal,
    val paymentDate: LocalDateTime,
    val status: String
)
