package com.example.tdd.application.port.out

/**
 * 분산 락 관리를 위한 아웃바운드 포트
 */
interface LockManagerRepository {
    /**
     * 지정된 리소스에 대한 락을 획득합니다.
     *
     * @param resourceId 락을 걸 리소스 ID
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
