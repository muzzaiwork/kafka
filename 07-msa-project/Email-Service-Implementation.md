# [실습] 이메일 발송을 처리할 Consumer 로직 짜기

User Service에서 발행한 회원가입 이벤트를 구독하여 실제로 이메일 발송 처리를 수행하고, 그 결과를 DB에 저장하는 컨슈머 로직을 구현한다.

---

## ✅ 주요 구현 내용

### 1. Kafka 메시지 역직렬화용 DTO 생성
Kafka 토픽에서 가져온 JSON 문자열 메시지를 Java 객체로 변환하기 위한 클래스를 생성한다.

**UserSignedUpEvent.java**
```java
public class UserSignedUpEvent {
  private Long userId;
  private String email;
  private String name;

  // 역직렬화(String 형태의 카프카 메시지 -> Java 객체)시 기본 생성자가 반드시 필요함
  public UserSignedUpEvent() {
  }

  public UserSignedUpEvent(Long userId, String email, String name) {
    this.userId = userId;
    this.email = email;
    this.name = name;
  }

  // JSON 문자열을 객체로 변환하는 정적 메서드
  public static UserSignedUpEvent fromJson(String json) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(json, UserSignedUpEvent.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("JSON 파싱 실패");
    }
  }

  public Long getUserId() { return userId; }
  public String getEmail() { return email; }
  public String getName() { return name; }
}
```

### 2. 이메일 발송 로그 엔티티 및 리포지토리 생성
발송 결과를 저장하기 위한 JPA 엔티티와 리포지토리를 생성한다.

**EmailLog.java**
```java
@Entity
@Table(name = "email_logs")
public class EmailLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long receiverUserId;
  private String receiverEmail;
  private String subject;

  public EmailLog() {}

  public EmailLog(Long receiverUserId, String receiverEmail, String subject) {
    this.receiverUserId = receiverUserId;
    this.receiverEmail = receiverEmail;
    this.subject = subject;
  }
}
```

### 3. 컨슈머 서비스(Consumer Service) 구현
`@KafkaListener`를 사용하여 메시지를 소비하고, 비즈니스 로직(이메일 발송 시뮬레이션 및 결과 저장)을 처리한다. 장애 발생 시 재시도를 위한 `@RetryableTopic` 설정도 추가한다.

**UserSignedUpEventConsumer.java**
```java
@Service
public class UserSignedUpEventConsumer {

  private final EmailLogRepository emailLogRepository;

  public UserSignedUpEventConsumer(EmailLogRepository emailLogRepository) {
    this.emailLogRepository = emailLogRepository;
  }

  @KafkaListener(
      topics = "user.signed-up",
      groupId = "email-service",
      concurrency = "3" // 병렬 처리 설정
  )
  @RetryableTopic(
      attempts = "5", // 최대 5번 시도
      backoff = @Backoff(delay = 1000, multiplier = 2), // 지수 백오프 전략
      dltTopicSuffix = ".dlt" // 실패 시 이동할 DLT 토픽 접미사
  )
  public void consume(String message) throws InterruptedException {
    UserSignedUpEvent userSignedUpEvent = UserSignedUpEvent.fromJson(message);

    String receiverEmail = userSignedUpEvent.getEmail();
    String subject = userSignedUpEvent.getName() + "님, 회원 가입을 축하드립니다!";
    
    // 이메일 발송에 3초 정도 시간이 걸리는 걸 가정 (지연 시간 시뮬레이션)
    Thread.sleep(3000);
    System.out.println("이메일 발송 완료: " + receiverEmail);

    // 발송 로그 DB 저장
    EmailLog emailLog = new EmailLog(
        userSignedUpEvent.getUserId(),
        receiverEmail,
        subject
    );
    emailLogRepository.save(emailLog);
  }
}
```

### 4. DLT(Dead Letter Topic) 컨슈머 구현
모든 재시도가 실패하여 DLT로 넘어온 메시지를 처리하는 로직을 추가한다.

**UserSignedUpEventDltConsumer.java**
```java
@Service
public class UserSignedUpEventDltConsumer {
  @KafkaListener(
      topics = "user.signed-up.dlt",
      groupId = "email-service"
  )
  public void consume(String message) {
    // 실제 운영 환경에서는 Slack 알림 발송이나 별도의 로그 시스템 전송 등을 수행
    System.out.println("DLT 메시지 수신 (로그 시스템 전송) : " + message);
    System.out.println("Slack에 알림 발송");
  }
}
```

---

## ✅ 핵심 포인트
- **비동기 처리**: User Service의 회원가입 완료와 별개로 이메일 발송이 백그라운드에서 진행된다.
- **안정성 확보**: `@RetryableTopic`을 통해 일시적인 오류(네트워크 순단 등)에 대응하고, 최종 실패 시 DLT로 격리하여 누락을 방지한다.
- **병렬 처리**: `concurrency` 설정을 통해 여러 파티션의 메시지를 동시에 처리하여 효율을 높인다.

---

## ➡️ 다음 단계
이제 User Service와 Email Service의 모든 핵심 로직 구현이 완료되었다. 마지막 단계에서는 두 서비스를 동시에 띄우고 전체 흐름이 정상적으로 동작하는지 최종 테스트를 진행한다.

- [[실습] MSA 프로젝트 통합 테스트 및 결과 확인](./Project-Final-Test.md)
