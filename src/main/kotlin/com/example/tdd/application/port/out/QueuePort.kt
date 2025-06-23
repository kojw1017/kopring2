package com.example.tdd.application.port.out

/**
 * 대기열 관리를 위한 아웃바운드 포트
 */
interface QueueManagerPort {
    /**
     * 사용자를 대기열에 추가합니다.
     *
     * @param userId 사용자 ID
     * @return 대기열 순번
     */
    fun addToQueue(userId: String): Int

    /**
     * 사용자의 현재 대기열 순번을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 대기열 순번 또는 null (활성화 상태이거나 대기열에 없는 경우)
     */
    fun getQueuePosition(userId: String): Int?

    /**
     * 사용자가 활성 상태인지 확인합니다.
     *
     * @param userId 사용자 ID
     * @return 활성화 여부
     */
    fun isActive(userId: String): Boolean

    /**
     * 사용자를 활성 상태로 전환합니다.
     *
     * @param userId 사용자 ID
     * @return 활성화 성공 여부
     */
    fun activateUser(userId: String): Boolean

    /**
     * 사용자의 활성 상태를 해제합니다.
     *
     * @param userId 사용자 ID
     * @return 비활성화 성공 여부
     */
    fun deactivateUser(userId: String): Boolean

    /**
     * 대기열에서 다음 활성화할 사용자 목록을 가져옵니다.
     *
     * @param count 활성화할 사용자 수
     * @return 활성화할 사용자 ID 목록
     */
    fun getNextActiveUsers(count: Int): List<String>
}

/**
 * 분산 락 관리를 위한 아웃바운드 포트
 */
interface LockManagerPort {
    /**
     * 지정된 리소스에 대한 락을 획득합니다.
     *
     * @param resourceId 락을 획득할 리소스 ID
     * @param ownerId 락 소유자 ID
     * @param timeoutMs 락 타임아웃 (밀리초)
     * @return 락 획득 성공 여부
     */
    fun acquireLock(resourceId: String, ownerId: String, timeoutMs: Long): Boolean

    /**
     * 지정된 리소스의 락을 해제합니다.
     *
     * @param resourceId 락을 해제할 리소스 ID
     * @param ownerId 락 소유자 ID
     * @return 락 해제 성공 여부
     */
    fun releaseLock(resourceId: String, ownerId: String): Boolean
}
