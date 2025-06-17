package com.example.tdd.service

import com.example.tdd.dto.OrderResponse
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

/**
 * 데이터 분석 플랫폼으로 주문 데이터를 전송하는 서비스
 * 실제 구현에서는 외부 API를 호출하거나 메시지 큐에 데이터를 전송할 수 있습니다.
 */
@Service
class DataAnalyticsService {

    private val logger = LoggerFactory.getLogger(DataAnalyticsService::class.java)

    @Async
    fun sendOrderData(orderResponse: OrderResponse) {
        // 실제 환경에서는 여기서 외부 API를 호출하거나 메시지 큐에 데이터를 전송합니다.
        logger.info("주문 데이터를 분석 플랫폼으로 전송: {}", orderResponse)

        // 외부 시스템과의 통신을 시뮬레이션하기 위한 지연
        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        logger.info("주문 데이터 전송 완료: {}", orderResponse.orderId)
    }
}
