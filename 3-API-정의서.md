# API 정의서

## 기본 정보
- 기본 URL: `/api`
- 응답 형식: JSON
- 인증 방식: 현재 버전에서는 별도의 인증 방식을 사용하지 않습니다.

## 1. 사용자 API

### 1.1 잔액 충전
- **URL**: `/api/users/{userId}/charge`
- **Method**: POST
- **Path Parameters**:
  - `userId`: 사용자 ID (Long)
- **Request Body**:
  ```json
  {
    "amount": 10000
  }
  ```
- **Response**:
  - Status Code: 200 OK
  ```json
  {
    "userId": 1,
    "balance": 10000
  }
  ```
- **Error Responses**:
  - 400 Bad Request: 요청 파라미터가 유효하지 않은 경우
  - 404 Not Found: 사용자를 찾을 수 없는 경우

### 1.2 잔액 조회
- **URL**: `/api/users/{userId}/balance`
- **Method**: GET
- **Path Parameters**:
  - `userId`: 사용자 ID (Long)
- **Response**:
  - Status Code: 200 OK
  ```json
  {
    "userId": 1,
    "balance": 10000
  }
  ```
- **Error Responses**:
  - 404 Not Found: 사용자를 찾을 수 없는 경우

## 2. 상품 API

### 2.1 전체 상품 조회
- **URL**: `/api/products`
- **Method**: GET
- **Response**:
  - Status Code: 200 OK
  ```json
  [
    {
      "id": 1,
      "name": "노트북",
      "price": 1500000,
      "stockQuantity": 10
    },
    {
      "id": 2,
      "name": "스마트폰",
      "price": 1000000,
      "stockQuantity": 20
    }
  ]
  ```

### 2.2 상품 상세 조회
- **URL**: `/api/products/{productId}`
- **Method**: GET
- **Path Parameters**:
  - `productId`: 상품 ID (Long)
- **Response**:
  - Status Code: 200 OK
  ```json
  {
    "id": 1,
    "name": "노트북",
    "price": 1500000,
    "stockQuantity": 10
  }
  ```
- **Error Responses**:
  - 404 Not Found: 상품을 찾을 수 없는 경우

### 2.3 상품 등록
- **URL**: `/api/products`
- **Method**: POST
- **Request Body**:
  ```json
  {
    "name": "블루투스 이어폰",
    "price": 150000,
    "stockQuantity": 50
  }
  ```
- **Response**:
  - Status Code: 201 Created
  ```json
  {
    "id": 3,
    "name": "블루투스 이어폰",
    "price": 150000,
    "stockQuantity": 50
  }
  ```
- **Error Responses**:
  - 400 Bad Request: 요청 파라미터가 유효하지 않은 경우

### 2.4 인기 상품 조회
- **URL**: `/api/products/top-selling`
- **Method**: GET
- **Response**:
  - Status Code: 200 OK
  ```json
  [
    {
      "id": 2,
      "name": "스마트폰",
      "price": 1000000,
      "stockQuantity": 18
    },
    {
      "id": 1,
      "name": "노트북",
      "price": 1500000,
      "stockQuantity": 8
    }
  ]
  ```

## 3. 주문 API

### 3.1 주문 생성
- **URL**: `/api/orders/{userId}`
- **Method**: POST
- **Path Parameters**:
  - `userId`: 사용자 ID (Long)
- **Request Body**:
  ```json
  {
    "items": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 1
      }
    ]
  }
  ```
- **Response**:
  - Status Code: 201 Created
  ```json
  {
    "orderId": 1,
    "userId": 1,
    "totalAmount": 4000000,
    "orderDate": "2023-06-17 15:30:45",
    "items": [
      {
        "productId": 1,
        "productName": "노트북",
        "quantity": 2,
        "price": 1500000
      },
      {
        "productId": 2,
        "productName": "스마트폰",
        "quantity": 1,
        "price": 1000000
      }
    ]
  }
  ```
- **Error Responses**:
  - 400 Bad Request: 요청 파라미터가 유효하지 않은 경우
  - 404 Not Found: 사용자나 상품을 찾을 수 없는 경우
  - 400 Bad Request: 재고가 부족한 경우
  - 400 Bad Request: 잔액이 부족한 경우

## 4. 장바구니 API

### 4.1 장바구니 조회
- **URL**: `/api/cart/{userId}`
- **Method**: GET
- **Path Parameters**:
  - `userId`: 사용자 ID (Long)
- **Response**:
  - Status Code: 200 OK
  ```json
  [
    {
      "id": 1,
      "productId": 1,
      "productName": "노트북",
      "price": 1500000,
      "quantity": 1
    },
    {
      "id": 2,
      "productId": 3,
      "productName": "블루투스 이어폰",
      "price": 150000,
      "quantity": 2
    }
  ]
  ```
- **Error Responses**:
  - 404 Not Found: 사용자를 찾을 수 없는 경우

### 4.2 장바구니에 상품 추가
- **URL**: `/api/cart/{userId}`
- **Method**: POST
- **Path Parameters**:
  - `userId`: 사용자 ID (Long)
- **Request Body**:
  ```json
  {
    "productId": 3,
    "quantity": 2
  }
  ```
- **Response**:
  - Status Code: 201 Created
  ```json
  {
    "id": 2,
    "productId": 3,
    "productName": "블루투스 이어폰",
    "price": 150000,
    "quantity": 2
  }
  ```
- **Error Responses**:
  - 400 Bad Request: 요청 파라미터가 유효하지 않은 경우
  - 404 Not Found: 사용자나 상품을 찾을 수 없는 경우
  - 400 Bad Request: 재고가 부족한 경우

### 4.3 장바구니 상품 수량 수정
- **URL**: `/api/cart/{userId}/products/{productId}`
- **Method**: PUT
- **Path Parameters**:
  - `userId`: 사용자 ID (Long)
  - `productId`: 상품 ID (Long)
- **Query Parameters**:
  - `quantity`: 변경할 수량 (Integer)
- **Response**:
  - Status Code: 200 OK
  ```json
  {
    "id": 2,
    "productId": 3,
    "productName": "블루투스 이어폰",
    "price": 150000,
    "quantity": 3
  }
  ```
- **Error Responses**:
  - 400 Bad Request: 요청 파라미터가 유효하지 않은 경우
  - 404 Not Found: 사용자, 상품 또는 장바구니 항목을 찾을 수 없는 경우
  - 400 Bad Request: 재고가 부족한 경우

### 4.4 장바구니에서 상품 삭제
- **URL**: `/api/cart/{userId}/products/{productId}`
- **Method**: DELETE
- **Path Parameters**:
  - `userId`: 사용자 ID (Long)
  - `productId`: 상품 ID (Long)
- **Response**:
  - Status Code: 204 No Content
- **Error Responses**:
  - 404 Not Found: 사용자, 상품 또는 장바구니 항목을 찾을 수 없는 경우
