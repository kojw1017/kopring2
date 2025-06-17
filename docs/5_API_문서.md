# 5. API 문서

### 인증
-   `1. 유저 토큰 발급 API`를 제외한 모든 API는 HTTP Header에 `Authorization: Bearer {대기열_토큰}`을 포함해야 합니다.
-   서버는 토큰을 검증하여 활성 상태의 사용자인지 확인합니다.

### 1. 유저 토큰 발급 API
-   **Endpoint:** `POST /api/tokens`
-   **Description:** 서비스 이용을 위한 대기열 토큰을 발급받습니다. 대기열에 진입하는 시점입니다.
-   **Request Body:** `{ "userId": "user-uuid-123" }`
-   **Success Response (200 OK):**
    ```json
    {
      "token": "대기열_JWT_토큰",
      "status": "WAITING", // or "ACTIVE"
      "rank": 150, // 대기 순번
      "expiresIn": 3600 
    }
    ```

### 2. 예약 가능 날짜 / 좌석 조회 API
-   **Endpoint (날짜 목록):** `GET /api/concerts/dates`
-   **Endpoint (좌석 목록):** `GET /api/concerts/{scheduleId}/seats`
-   **Description:** 예약 가능한 날짜 목록 또는 특정 날짜의 좌석 상태를 조회합니다.
-   **Success Response (좌석 목록, 200 OK):**
    ```json
    {
      "seats": [
        { "seatNumber": 1, "status": "AVAILABLE", "price": 100000 },
        { "seatNumber": 2, "status": "RESERVED", "price": 100000 },
        { "seatNumber": 3, "status": "SOLD", "price": 100000 }
      ]
    }
    ```

### 3. 좌석 예약 요청 API
-   **Endpoint:** `POST /api/reservations`
-   **Description:** 좌석을 5분간 임시로 예약(선점)합니다.
-   **Request Body:** `{ "scheduleId": 1, "seatNumber": 1 }`
-   **Success Response (200 OK):**
    ```json
    {
      "reservationId": 123,
      "seatNumber": 1,
      "status": "PENDING",
      "expiresAt": "2023-10-27T15:05:00Z"
    }
    ```
-   **Fail Response (409 Conflict):** `{ "message": "이미 배정된 좌석입니다." }`

### 4. 잔액 충전 / 조회 API
-   **Endpoint (충전):** `PATCH /api/users/balance`
-   **Endpoint (조회):** `GET /api/users/balance`
-   **Description:** 사용자 잔액을 충전하거나 조회합니다.
-   **Request Body (충전):** `{ "amount": 50000 }`
-   **Success Response (조회, 200 OK):** `{ "balance": 150000 }`

### 5. 결제 API
-   **Endpoint:** `POST /api/payments`
-   **Description:** 임시 배정된 예약 건을 결제하여 최종 확정합니다.
-   **Request Body:** `{ "reservationId": 123 }`
-   **Success Response (200 OK):**
    ```json
    {
      "paymentId": 456,
      "message": "결제가 성공적으로 완료되었습니다."
    }
    ```
-   **Fail Response (400 Bad Request):** `{ "message": "잔액이 부족합니다." }`
-   **Fail Response (400 Bad Request):** `{ "message": "예약이 만료되었습니다." }`
