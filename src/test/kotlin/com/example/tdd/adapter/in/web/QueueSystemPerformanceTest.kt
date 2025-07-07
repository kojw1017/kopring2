package com.example.tdd.adapter.`in`.web

import com.example.tdd.IntegrationTest
import com.example.tdd.application.port.`in`.QueueStatusResponse
import com.example.tdd.application.port.`in`.QueueTokenResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class QueueSystemPerformanceTest : IntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("대기열 시스템이 많은 사용자를 순서대로 처리할 수 있어야 함")
    fun testQueueSystemWithManyUsers() {
        val totalUsers = 50
        val successCount = AtomicInteger(0)
        val latch = CountDownLatch(totalUsers)
        val executor = Executors.newFixedThreadPool(10) // 병렬 처리를 위한 스레드 풀

        val tokens = mutableListOf<QueueTokenResponse>()

        // 1. 여러 사용자가 동시에 대기열 토큰 발급 요청
        for (i in 1..totalUsers) {
            executor.execute {
                try {
                    val tokenResponse = mockMvc.perform(
                        post("/api/queue/token")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.token").exists())
                        .andExpect(jsonPath("$.queueNumber").exists())
                        .andReturn()
                        .response
                        .contentAsString

                    val queueToken = objectMapper.readValue(tokenResponse, QueueTokenResponse::class.java)
                    synchronized(tokens) {
                        tokens.add(queueToken)
                    }

                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    latch.countDown()
                }
            }
        }

        // 모든 토큰 발급 요청 완료 대기
        latch.await(30, TimeUnit.SECONDS)

        // 2. 발급된 토큰 수 확인
        assertTrue(successCount.get() == totalUsers, "모든 사용자가 토큰을 발급받아야 합니다.")

        // 3. 대기 번호 순서대로 정렬
        val sortedTokens = synchronized(tokens) {
            tokens.sortedBy { it.queueNumber }
        }

        // 4. 대기 번호 연속성 확인 (건너뛰는 번호 없이 1부터 순서대로 발급되었는지)
        for (i in sortedTokens.indices) {
            val expectedQueueNumber = i + 1
            assertTrue(sortedTokens[i].queueNumber == expectedQueueNumber,
                "대기 번호는 1부터 순서대로 발급되어야 합니다. 예상: $expectedQueueNumber, 실제: ${sortedTokens[i].queueNumber}")
        }

        // 5. 대기 상태 조회 테스트
        val statusLatch = CountDownLatch(totalUsers)
        val statusSuccessCount = AtomicInteger(0)

        for (token in sortedTokens) {
            executor.execute {
                try {
                    val statusResponse = mockMvc.perform(
                        get("/api/queue/status")
                            .param("token", token.token)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.queueNumber").value(token.queueNumber))
                        .andReturn()
                        .response
                        .contentAsString

                    val queueStatus = objectMapper.readValue(statusResponse, QueueStatusResponse::class.java)
                    assertTrue(queueStatus.queueNumber == token.queueNumber,
                        "대기 상태 조회 시 올바른 대기 번호가 반환되어야 합니다.")

                    statusSuccessCount.incrementAndGet()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    statusLatch.countDown()
                }
            }
        }

        // 모든 상태 조회 요청 완료 대기
        statusLatch.await(30, TimeUnit.SECONDS)

        // 6. 상태 조회 성공 수 확인
        assertTrue(statusSuccessCount.get() == totalUsers, "모든 사용자가 대기 상태를 성공적으로 조회해야 합니다.")

        executor.shutdown()
    }
}
