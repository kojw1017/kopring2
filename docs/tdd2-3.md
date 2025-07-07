<aside>
👑 명예의 전당

</aside>

**천동민님**

https://github.com/Cheondongmin/hhplus-concert-java/pull/12

- PR(Pull Request) 이 정말 친절합니다. 리뷰할 때 위에서 아래로 하나씩 살펴보면 되는 구조였습니다.
- 코드에서도 리뷰어가 API 테스트 할 수있는 HTTP 를 제공해주는 것도 좋았습니다.
- Docs 에 ddl.sql , [erd.md](http://erd.md) 등등 리뷰어가 보기 편하도록 만들어주셨습니다.

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/604c1481-ef07-4f33-9bdc-1da2a059425f/0341633c-a643-469e-bb40-54e308627dea/image.png)

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/604c1481-ef07-4f33-9bdc-1da2a059425f/53f50e9c-4f6d-4733-b290-f296ec2f8b27/image.png)

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/604c1481-ef07-4f33-9bdc-1da2a059425f/249a4cbb-7e71-4e3c-8f9d-5dc46553a9ec/image.png)

**유재현님**

- PR 나눠준것, 라벨링, 리뷰포인트까지 정말 다른 분들에게 귀감이 될 수 있을 것 같네요 (허재코치님 왈…)

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/604c1481-ef07-4f33-9bdc-1da2a059425f/6277ad2f-ff0f-454e-8034-fc1bdaee3f53/image.png)

[좋은 PR의 의미와 만드는 방법: 효과적인 코드 리뷰를 위한 가이드](https://www.notion.so/1012dc3ef51480e2a6c6c8705ddd92b5?pvs=21)

<aside>
🎯 지난 주차 Summary

</aside>

- **서버 개발 Summary**

  ### Repository는 Unit ? Integration ?

  “코치님은 Repository 구현체에 대한 테스트를 진행할 때 단위테스트, 통합테스트 중 어떤 것을 진행하시나요 ?”

  우리는 테스트를 할 때, 그 목적에 대해 자세히 생각해 보아야 합니다. 저는 외부 Infra 와 함께하는 `통합 테스트` 로 진행합니다.그 이유는 Repository 의 기능적 / 사용 역할을 생각해보았을 때 단순히 데이터에 대한 CRUD 로 귀결되기 때문이죠. 그렇기 때문에 Repository 구현체의 주요 테스트 대상은 **“DB 와 같은 외부 Storage와 올바르게 상호작용하는 기능을 제공하고 있는가”** 라고 생각합니다. 그리고 하나의 TestSuite 내에서 유기적으로 관심사 ( 데이터 ) 의 생명주기 [ 생성 ~ 삭제 ] 순서대로 테스트를 구성해 하나의 흐름으로서 테스트를 구성합니다.

    ```tsx
    // 데이터의 생성 ~ 삭제 가 해당 테스트 컨텍스트 내에서 완료될 수 있도록 자연스럽게 구성한다.
    class 테스트수트 {
    	@Order(1)
    	fun `생성 관련 테스트`() { }
    	...
    	@Order(2)
    	fun `조회 관련 테스트`() { }
    	...
    	@Order(3)
    	fun `수정 관련 테스트`() { }
    	...
    	@Order(4)
    	fun `삭제 관련 테스트`() { }
    }
    ```

  ## 테스트 코드는 고립될 수 있어야 한다.

  테스트 코드의 특징으로 FIRST 라는 것이 있습니다.

  개발자들이 보편적으로 "좋은 단위 테스트" 라고 부르는 테스트는 FIRST 규칙을 따른다고 합니다. 여기서 FIRST 란 다음과 같습니다.

  - Fast
  - Independent
  - Repeatable
  - Self-Validating
  - Timely

  이것들을 다 설명할 수는 없고, 이 중 **Independent** 에 대해서 이야기해보려 합니다.

  여기서 Independent 의미는 `단위 테스트는 독립적으로 수행되어야 합니다.`  입니다.

  이번에 과제리뷰하면서, 통합테스트를 동작시킬때마다 깔끔한 성공을 보기 힘들었습니다. 이유는 mysql DB 가 동작되어진 상태여야 하고, 테이블도 생성되어야만 그제서야 테스트 코드를 실행시킬 수 있기 때문입니다. 그럼 위에서 말하는 FIRST 를 지킨 좋은 테스트 라고 말하기 힘듭니다.

  이 문제를 해결하기 위해 가장 보편적으로 사용하는 것은 [TestContainer](https://testcontainers.com/) 입니다. 테스트 코드가 독립적으로 수행되기 위해 docker 로 해당 DB 를 띄우고, 테스트 코드를 실행시켜줄 수 있도록 도와주는 테스트 유틸 라이브러리입니다.

  주로 저는 https://github.com/zonkyio/embedded-database-spring-test 이 라이브러리를 사용해서 DB에 대한 의존성을 제거합니다.

    ```jsx
    @RunWith(SpringRunner.class)
    @AutoConfigureEmbeddedDatabase
    @ContextConfiguration("path/to/application-config.xml")
    public class EmptyDatabaseIntegrationTest {
        // class body...
    }
    ```

  관련 내용이 담긴 블로그

  - 컬리블로그 [http://thefarmersfront.github.io/blog/delivery-testContainer-apply/]

  ### **지난 4주간 수백번의 서비스 라는 말을 하고 있는데, 서비스가 뭘까?**

  멘토링하면서 서비스 구현에 대한 이야기를 많이 이야기 했는데요. 이 서비스에 대한 이야기를 해보려 해요. 제가 말하는 서비스라는 것과, 청중의 입장에서 서비스라는 말이 간혹 다르게 받아들여지는 경향이 있는 것 같아 가져왔어요.

  서비스에 대해 올바르게 이해하는 것이 중요한 이유는 애플리케이션, 도메인, 인프라스트럭처 등 다양한 계층에서 객체 간의 관계에 대해 잘 이해할 수 있기 때문입니다. 또한, 각 계층의 책임을 분리해 DDD(도메인 주도 설계)에서는 도메인 서비스가 특정 비즈니스 로직을 캡슐화하기 위해서는 Service에 대한 이해를 바탕으로 수반하게 되기도 합니다.

  그럼 다시 돌아가서 **서비스** 가 뭘까요? Springboot에서 말하는 @Service 를 붙으면 다 서비스인걸까요?

  ![](https://raw.githubusercontent.com/LenKIM/images/master/2024-10-16/image-20241016132454379.png)

  아니면, 위에서 말하는 흔히 MVC 패턴의 Service 가 서비스일까요?

  서비스 추상화라는 용어가 있는데, 이것은 DIP 에 해당하는 이야기이고, 여기서는 차치하고 좀더 포괄적인 서비스라는 단어에 집중하겠습니다.

  일단 제가 말하고 있는 서비스는 스프링의 @Service 를 말하는 것이 아니라 *백엔드 개발 전반에서 사용되는 서비스에 대한 이야기입니다.*

  **서비스라는 것은  일반적인 용어**라서 쓰이는 곳에 따라 다른 의미를 가지게 됩니다.

  서비스에 대해서 크게 2가지 의미를 가집니다.

  ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/604c1481-ef07-4f33-9bdc-1da2a059425f/c938e821-8813-4bab-b23f-44a29690a12e/image.png)

  ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/604c1481-ef07-4f33-9bdc-1da2a059425f/2acb6784-ac72-43ec-a205-45121a766632/image.png)

  서비스란 용어는 쓰이는 곳에 따라 다른 의미를 가진다라고 말했는데, 서비스의 종류에는 크게 3가지가 있습니다.

  - **애플리케이션 서비스(Application service)**
    - 서비스 레이어라고 부르면서 우리가 흔히 서비스라고 하면 여기를 의미하곤 하죠.
    - 대부분 @Service 라는 것을 붙인다. 비지니스로직을 담당하기 시작하는 시작점. 종료/ 그 경계가 되는 곳이고, nextNest.js에서는 @Injectable() 데코레이터를 사용하는 것과 유사합니다.
    - DIP 활용한 애플리케이션 서비스 코드

        ```java
        interface MemberUsecase {
        
                Member find(Long id);
        }
        
        --
        @Service
        class MemberApplicationService implements MemberUsecase {
        
                Aaaa aService;
                Baaa bService;
                Caaa cService;
                
                @Override
                @Transcational
                Member find(Long id) {
                            aService.get(id)
                            .. bService, 
                        ....cService
                }
        }
        
        ---
        
        class MemberController {
                
                MemberUsecase memberUsecase;
                
                @GetMapping
                Member get(Long id) {
                    return memberUsecase.find(id);
                }
                ...use 
        
        }
        ```

  - **도메인 서비스 (Domain service)**
    - 도메인 모델 패턴을 이용해서 비지니스 로직을 엔티티 같은 도메인 오브젝트에 집어넣는 경우에 특정 엔티티로는 표현하기 힘든 그런 로직들이 있는데, 그런 경우에 도메인 서비스에 오브젝트를 만들어서 그 안에 집어넣을 수 있습니다.
    - 만약 도메인 서비스를 왜 만들어야 하는지 아직도 이해되지 않는다면… https://happy-coding-day.tistory.com/267 링크를 통해 좀더 이해해보자
    - 흔하게 우리가 하는 코드

        ```java
        // Order.java
        public class Order {
            private Long id;
            private List<OrderItem> items = new ArrayList<>();
            private BigDecimal totalAmount;
        
            public Order() {
                this.totalAmount = BigDecimal.ZERO;
            }
        
            public void addItem(OrderItem item) {
                items.add(item);
                totalAmount = totalAmount.add(item.getTotalPrice());
            }
        
            public BigDecimal getTotalAmount() {
                return totalAmount;
            }
        
            public void applyDiscount(BigDecimal discount) {
                this.totalAmount = this.totalAmount.subtract(discount);
            }
        }
        
        // Customer.java
        public class Customer {
            private Long id;
            private String name;
            private boolean isVip;
        
            public Customer(Long id, String name, boolean isVip) {
                this.id = id;
                this.name = name;
                this.isVip = isVip;
            }
        
            public boolean isVip() {
                return isVip;
            }
        }
        
        // OrderApplicationService.java
        public class OrderApplicationService {
            private final OrderRepository orderRepository;
            private final CustomerRepository customerRepository;
        
            public OrderApplicationService(OrderRepository orderRepository,
                                           CustomerRepository customerRepository) {
                this.orderRepository = orderRepository;
                this.customerRepository = customerRepository;
            }
        
                // 예시 정책: VIP 고객은 10% 할인, 일반 고객은 5% 할인
            @Transactional
            public void applyDiscountToOrder(Long orderId, Long customerId) {
                // 1. 리포지토리를 통해 주문과 고객을 조회
                Order order = orderRepository.findById(orderId);
                Customer customer = customerRepository.findById(customerId);
        
                if (order == null || customer == null) {
                    throw new IllegalArgumentException("Order or Customer not found");
                }
        
                // 2. 할인을 직접 계산
                BigDecimal discount = calculateDiscount(order, customer);
                
                // 3. 주문에 할인을 적용
                order.applyDiscount(discount);
        
                // 4. 변경된 주문을 저장
                orderRepository.save(order);
        
                System.out.println("Discount applied: " + discount);
                System.out.println("Total amount after discount: " + order.getTotalAmount());
            }
        
            // 할인 계산 로직을 애플리케이션 서비스에서 직접 구현
            private BigDecimal calculateDiscount(Order order, Customer customer) {
                BigDecimal discount = BigDecimal.ZERO;
                
                if (customer.isVip()) {
                    discount = order.getTotalAmount().multiply(BigDecimal.valueOf(0.10));
                } else {
                    discount = order.getTotalAmount().multiply(BigDecimal.valueOf(0.05));
                }
        
                return discount;
            }
        }
        ```

    - 도메인 서비스를 접목한 코드

        ```java
        // DiscountService.java
        // 예시 정책: VIP 고객은 10% 할인, 일반 고객은 5% 할인
        // VipDiscountPolicy.java
        public class DiscountService {
        
            // 할인을 계산하는 메서드
            public BigDecimal calculateDiscount(Order order, Customer customer) {
                BigDecimal discount = BigDecimal.ZERO;
        
                if (customer.isVip()) {
                    discount = order.getTotalAmount().multiply(BigDecimal.valueOf(0.10));
                } else {
                    discount = order.getTotalAmount().multiply(BigDecimal.valueOf(0.05));
                }
        
                return discount;
            }
        }
        
        // OrderApplicationService.java
        public class OrderApplicationService {
            private final OrderRepository orderRepository;
            private final CustomerRepository customerRepository;
            private final DiscountService discountService;
        
            public OrderApplicationService(OrderRepository orderRepository,
                                           CustomerRepository customerRepository,
                                           DiscountService discountService) {
                this.orderRepository = orderRepository;
                this.customerRepository = customerRepository;
                this.discountService = discountService;
            }
        
            @Transactional
            public void applyDiscountToOrder(Long orderId, Long customerId) {
                // 1. 리포지토리를 통해 주문과 고객을 조회
                Order order = orderRepository.findById(orderId);
                Customer customer = customerRepository.findById(customerId);
        
                if (order == null || customer == null) {
                    throw new IllegalArgumentException("Order or Customer not found");
                }
        
                // 2. 도메인 서비스 호출하여 할인 계산
                BigDecimal discount = discountService.calculateDiscount(order, customer);
                
                // 3. 주문에 할인을 적용
                order.applyDiscount(discount);
        
                // 4. 변경된 주문을 저장
                orderRepository.save(order);
        
                System.out.println("Discount applied: " + discount);
                System.out.println("Total amount after discount: " + order.getTotalAmount());
            }
        }
        ```


    - **인프라 서비스(Infrastrucutre service)**
        - **서비스 추상화의 대상이 되는 것**
        - *도메인/애플리케이션 로직에 참여하지 않는, 기술을 제공하는 서비스*
        - 메일, 캐시, 트랜잭션, 메시징 ... 굉장히 기술적인 부분을 말한다. 여기서 트랜잭션이 주 대상.
        - 우리가 아는 @Transactional > PlatformTransactionManager
            - Next.js 에서는 아래와 같이 사용할 때도 마찬가지인데 sequelize 안에서 Orcle 에서 MySQL 로 변경된다 하더라도 코드의 변화는 없다.
                
                ```java
                async createUserWithTransaction(userData: any): Promise<User> {
                    const transaction = await this.sequelize.transaction();
                
                    try {
                      const user = await this.userModel.create(userData, { transaction });
                
                      // 트랜잭션 완료 시 커밋
                      await transaction.commit();
                      return user;
                    } catch (error) {
                      // 오류 발생 시 롤백
                      await transaction.rollback();
                      throw error;
                    }
                  }
                ```
                
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/604c1481-ef07-4f33-9bdc-1da2a059425f/b5fa69c6-0adf-49a7-9f46-a13756e4b971/image.png)
        
        출처 - 내 블로그 / 토비의 스프링 인프런 강의


<aside>
⛵ **이번 챕터 목표**

</aside>

- **아키텍처와 테스트에 집중하며, 시나리오 기반 서버 애플리케이션 구축**
- **아키텍처와 테스트 코드 작성에 집중하며, 견고하고 유연한 서버 개발을 지향합니다.**

<aside>
🚩 **What to do: 이번 챕터에 해야 할 것. 이것만 집중하세요!**

</aside>

### 6. Logging & Exception

**로깅**

- **로그**란 다양한 정보를 제공하기 위한 일련의 기록을 의미합니다.
  - `log` 는 실제 발생한 문제에 대한 훌륭한 단서를 제공합니다.
  - `log` 는 실행 중인 애플리케이션의 정보를 프로그램 외부에 전달할 수 있습니다.
  - `log` 는 운영 중인 시스템의 상태를 제공하고, 적절한 대처가 가능하도록 합니다.

  > ex_ 로깅 환경을 적용하지 않은 상황에서 특정 API 의 기능에 장애가 발생했다 !
  → 운영 중인 환경에 대해 개발자는 이를 인지할 수 있는 정보가 없다.
  → 무엇이 잘못되었는지, 어떻게 대처해야 할지 단서가 없으므로 해당 기능은 장애를 안은 채로 지속 운영된다.
  >

    <aside>
    💡

  이제 우리 `println` 과 `console.log` 와 작별할 때 !

    </aside>

- `log` 는 어떤 정보를 작성해야 할까?
  - http log - 요청/응답에 대한 로그 ( 통상 interceptor 를 이용 )
  - error / warn - 예기치 못한 Exception 이나 주의를 요하는 곳에 대한 로그
  - info - 주요 비즈니스 로직에서 처리되는 데이터 등에 대한 로그
  - DATA - 데이터 분석 및 통계를 위한 로그

    <aside>
    🚫 위 로그 네이밍은 로깅 라이브러리의 네이밍과 무관합니다.

    </aside>


**예외 처리**

<aside>
💡 예외 또한 적절히 다뤄야 하는 대상이다!

</aside>

- Exception 을 제어하지 못하면 애플리케이션의 Crash 로 이어질 수 있음
- 사용하는 라이브러리, api 등에서 발생하는 Exception 에 대한 처리 또한 *비즈니스 로직*
  - `try-catch` ( kotlin - `runCatching` ) 구문을 활용
  - 애플리케이션의 “예외” 로 처리되어야 한다면 우리의 제어가능한 Exception 으로 변경
  - 특정 도메인 내에서만 발생할 수 있는 `예외` 라면 이 또한 응집의 대상
- `요청` 에 대한 기능의 수행을 적절히 처리할 수 없는 상황에서는 `Exception` 을 발생시키고 프레임워크에서 제공하는 Global Exception Handling 을 통해 제어하도록 하자
  - e.g. Java Spring Example ( feat. `@RestControllerAdvice` )

      ```java
      public class CustomException extends RuntimeException {
          private final Object data;
          private final LogLevel logLevel;
          
          public CustomException(String message, LogLevel logLevel, Object data) {
              super(message);
              this.logLevel = logLevel;
              this.data = data;
          }
          
          public LogLevel getLogLevel() {
              return logLevel;
          }
          
          public Object getData() {
              return data;
          }
      }
      
      @RestControllerAdvice
      class ApiControllerAdvice {
          private final Logger logger = LoggerFactory.getLogger(getClass());
          // 내가 정의한 Exception 이 발생했을 때 에러 응답
          @ExceptionHandler(CustomException.class)
          public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
              switch (e.getLogLevel()) {
            case ERROR -> log.error("ApiException : {}", e.getMessage(), e);
            case WARN -> log.warn("ApiException : {}", e.getMessage(), e);
            default -> log.info("ApiException : {}", e.getMessage(), e);
          }
          // Http status 200 선호 "UserNotFound --> x 200"
          return new ResponseEntity<>(ApiResponse.error(e.getMessage(), e.getData()), getErrorType(e).getStatus());
        }
        
          @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
          log.error("UnhandledException : {}", e.getMessage(), e);
          return new ResponseEntity<>(ApiResponse.error(e.getMessage()), DEFAULT_ERROR.getStatus());
        }
      }
      ```

  - e.g. TS Nest.js Example ( feat. `ExceptionFilter` )

      ```tsx
      export class CustomException extends HttpException {
          message: string
          statusCode: number
          data?: any
          
          constructor(message: string, statusCode: number, data?: any) {
              super(message, statusCode)
              this.message = message
              this.statusCode = statusCode
              this.data = data
          } 
      }
      
      @Catch(HttpException)
      export class GlobalExceptionFilter implements ExceptionFilter {
          private logger = new Logger("GlobalExceptionFilter")
          // unhandled 또한 처리 가능하도록 unknown Type 으로 receive
          catch(exception: unknown, host: ArgumentsHost) {
              const ctx = host.switchToHttp()
              const request = ctx.getRequest()
              const response = ctx.getResponse()
              
              const e = exception instanceof HttpException ? : new UnhandledException()
              this.logger.error(e)
              
              response.status(e.statusCode).json({
                  message: e.message,
                  data: e.data,
              })
          }
      }
      ```


- try { } catch (Exception e) { .. }
- 예외의 타입을 고민. 이게 복구가능한 예외인가? 핸들링할 예외인가? 무시해도 되는 예외인가?
- Custom Exception, **표준예외를 활용하는 편을 추천**
  - Rollback 할 때, Custom Exception 사실을 알았는데.. 왜 필요한지?
  -
- **CI/CD 공부 참고 자료**

  [CI/CD 배포 파이프라인 구축](https://www.notion.so/CI-CD-157f5cff9b928171a357ed39484f3ead?pvs=21)


### 시나리오를 선택해 서버 애플리케이션 구축

<aside>
❓ 아키텍처와 테스트 코드 작성에 집중하며, 견고하고 유연한 서버 개발이 목표인 사람 (챌린지 과제가 포함되어 있습니다)

<aside>
**과제 : 이번 챕터 과제**

</aside>

</aside>

[e-커머스 서비스 (1)](https://www.notion.so/e-1-157f5cff9b92810cb1dbd7be7da039bb?pvs=21)

[맛집 검색 서비스 (1)](https://www.notion.so/1-157f5cff9b9281ad826de3e079354fad?pvs=21)

[콘서트 예약 서비스 (1)](https://www.notion.so/1-157f5cff9b9281c68463fb358ace54d1?pvs=21)

<aside>
🗓️ **Weekly Schedule Summary: 이번 챕터의 주간 일정 (금요일 오전 10시까지 제출)**

</aside>

### **`DEFAULT`**

- 비즈니스 별 발생할 수 있는 에러 코드 정의 및 관리 체계 구축
- 프레임워크별 글로벌 에러 핸들러를 통해 예외 로깅 및 응답 처리 핸들러 구현
  - `spring` - **RestControllerAdvice**
  - `nestjs` - **ExceptionFilter**

### **`STEP 09`**

- 시스템 성격에 적합하게 Filter, Interceptor 를 활용해 기능의 관점을 분리하여 개선
- 모든 API 가 정상적으로 기능을 제공하도록 완성

> 각 시나리오별 요구사항 내에 정의된 기능은 정상적으로 동작할 수 있어야 합니다. 개선 및 최적화에 초점을 두는 것이 아닌, 추후 개선해나갈 수 있도록 동작하는 기능을 완성하는 것이 목적입니다.
>

### **`STEP 10`**

- 시나리오별 동시성 **통합 테스트** 작성
- **Chapter 2** 회고록 작성

> DB Index , 대용량 처리를 위한 개선 포인트 등은 추후 챕터에서 진행하므로 목표는 `기능 개발의 완료` 로 합니다. 최적화 작업 등을 고려하는 것 보다 모든 기능을 정상적으로 제공할 수 있도록 해주세요. 특정 기능을 왜 이렇게 개발하였는지 합당한 이유와 함께 기능 개발을 진행해주시면 됩니다.
>