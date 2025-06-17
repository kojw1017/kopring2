package com.example.tdd.controller

import com.example.tdd.dto.UserBalanceResponse
import com.example.tdd.dto.UserChargeRequest
import com.example.tdd.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping("/{userId}/charge")
    fun chargeBalance(
        @PathVariable userId: Long,
        @Valid @RequestBody request: UserChargeRequest
    ): ResponseEntity<UserBalanceResponse> {
        val response = userService.chargeBalance(userId, request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{userId}/balance")
    fun getBalance(@PathVariable userId: Long): ResponseEntity<UserBalanceResponse> {
        val response = userService.getBalance(userId)
        return ResponseEntity.ok(response)
    }
}
