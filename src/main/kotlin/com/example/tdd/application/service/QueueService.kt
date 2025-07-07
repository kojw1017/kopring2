package com.example.tdd.application.service

import com.example.tdd.application.exception.QueueException
import com.example.tdd.application.port.`in`.IssueQueueTokenUseCase
import com.example.tdd.application.port.`in`.QueueStatusResponse
import com.example.tdd.application.port.`in`.QueueTokenResponse
import com.example.tdd.domain.model.QueueStatus
import com.example.tdd.domain.model.QueueToken
import com.example.tdd.domain.model.User
import com.example.tdd.domain.repository.QueueTokenRepository
import com.example.tdd.domain.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class QueueService(
    private val queueTokenRepository: QueueTokenRepository,
    private val userRepository: UserRepository,
    @Value("\${jwt.queue-token-validity-in-seconds:7200}")
    private val queueTokenValiditySeconds: Long,
    @Value("\${queue.active-user-limit:20}")
    private val activeUserLimit: Int
) : IssueQueueTokenUseCase {

    @Transactional
    override fun issueToken(): QueueTokenResponse {
        // 새로운 사용자 생성
        val user = userRepository.save(User.create())

        // 대기열 번호 발급
        val queueNumber = queueTokenRepository.getNextQueueNumber()

        // 대기열 토큰 생성 및 저장
        val queueToken = QueueToken.create(user.id, queueNumber, queueTokenValiditySeconds)
        val savedToken = queueTokenRepository.save(queueToken)

        // 대기 시간 예상 (앞에 있는 사용자 수 * 평균 처리 시간)
        // 여기서는 간단하게 앞에 있는 사용자 수 * 1분으로 계산
        val estimatedWaitTimeMinutes = (queueNumber - 1).coerceAtLeast(0)

        return QueueTokenResponse(
            userId = user.id,
            token = savedToken.token,
            queueNumber = savedToken.queueNumber,
            estimatedWaitTimeMinutes = estimatedWaitTimeMinutes
        )
    }

    @Transactional(readOnly = true)
    override fun getQueueStatus(token: String): QueueStatusResponse {
        val queueToken = queueTokenRepository.findByToken(token)
            ?: throw QueueException("대기열 토큰을 찾을 수 없습니다.")

        // 토큰이 만료되었거나 이미 활성화된 경우
        if (!queueToken.isValid() && queueToken.status != QueueStatus.ACTIVE) {
            throw QueueException("만료된 대기열 토큰입니다.")
        }

        // 활성화 여부 확인 (대기열 앞 부분에 있는 사용자는 활성화)
        val activeTokenCount = queueTokenRepository.countActiveTokens()
        val isActive = queueToken.status == QueueStatus.ACTIVE ||
                (queueToken.status == QueueStatus.WAITING && queueToken.queueNumber <= activeUserLimit)

        // 활성화된 상태라면 토큰을 활성화
        if (isActive && queueToken.status == QueueStatus.WAITING) {
            queueTokenRepository.save(queueToken.activate())
        }

        // 대기 위치 계산 (현재 대기 번호 - 활성화된 토큰 수)
        val position = (queueToken.queueNumber - activeTokenCount).coerceAtLeast(0)

        // 예상 대기 시간 계산 (남은 위치 * 평균 처리 시간)
        val estimatedWaitTimeMinutes = position.coerceAtLeast(0)

        return QueueStatusResponse(
            token = queueToken.token,
            queueNumber = queueToken.queueNumber,
            isActive = isActive,
            estimatedWaitTimeMinutes = estimatedWaitTimeMinutes,
            position = position
        )
    }
}
