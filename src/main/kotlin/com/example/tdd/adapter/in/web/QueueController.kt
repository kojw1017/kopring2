package com.example.tdd.adapter.`in`.web

import com.example.tdd.application.port.`in`.IssueQueueTokenUseCase
import com.example.tdd.application.port.`in`.QueueStatusResponse
import com.example.tdd.application.port.`in`.QueueTokenResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/queue")
@Tag(name = "대기열 관리", description = "대기열 토큰 발급 및 상태 조회 API")
class QueueController(
    private val issueQueueTokenUseCase: IssueQueueTokenUseCase
) {

    @PostMapping("/token")
    @Operation(
        summary = "대기열 토큰 발급",
        description = "새로운 사용자를 생성하고 대기열 토큰을 발급합니다."
    )
    fun issueToken(): ResponseEntity<QueueTokenResponse> {
        val response = issueQueueTokenUseCase.issueToken()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/status")
    @Operation(
        summary = "대기 상태 조회",
        description = "토큰으로 현재 대기 상태(위치, 예상 대기 시간)를 조회합니다."
    )
    fun getQueueStatus(
        @RequestParam token: String
    ): ResponseEntity<QueueStatusResponse> {
        val response = issueQueueTokenUseCase.getQueueStatus(token)
        return ResponseEntity.ok(response)
    }
}
