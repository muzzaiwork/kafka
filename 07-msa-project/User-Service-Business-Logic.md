# [실습] 회원 가입 비즈니스 로직 짜기

사용자 정보를 DB에 저장하고, 가입 완료 이벤트를 Kafka 토픽으로 발행하는 핵심 비즈니스 로직을 구현한다.

---

## ✅ 주요 구현 내용

### 1. Kafka 전송용 메시지 객체 (Event) 생성
MSA 구조에서 서비스 간 통신에 사용할 이벤트 객체를 생성한다. 비밀번호와 같이 다른 서비스에서 불필요한 정보는 제외하고 전송한다.

**UserSignedUpEvent.java**
```java
public class UserSignedUpEvent {
  private Long userId;
  private String email;
  private String name;

  public UserSignedUpEvent(Long userId, String email, String name) {
    this.userId = userId;
    this.email = email;
    this.name = name;
  }

  public Long getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }
}
```

### 2. UserService 비즈니스 로직 구현
사용자 저장 및 Kafka 메시지 발행 로직을 `signUp` 메서드에 완성한다.

**UserService.java**
```java
@Service
public class UserService {
  private final UserRepository userRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  public UserService(UserRepository userRepository, KafkaTemplate<String, String> kafkaTemplate) {
    this.userRepository = userRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  public void signUp(SignUpRequestDto signUpRequestDto) {
    // 1. 회원 가입한 사용자 정보 DB에 저장
    User user = new User(
        signUpRequestDto.getEmail(),
        signUpRequestDto.getName(),
        signUpRequestDto.getPassword()
    );
    User savedUser = userRepository.save(user);

    // 2. 카프카에 메시지 전송 (이벤트 발행)
    UserSignedUpEvent userSignedUpEvent = new UserSignedUpEvent(
        savedUser.getId(),
        savedUser.getEmail(),
        savedUser.getName()
    );
    this.kafkaTemplate.send("user.signed-up", toJsonString(userSignedUpEvent));
  }

  // 객체를 JSON 문자열로 변환하는 헬퍼 메서드
  private String toJsonString(Object object) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Json 직렬화 실패");
    }
  }
}
```

---

## ✅ 핵심 포인트
- **필요한 정보만 선별**: `UserSignedUpEvent`에는 이메일 서비스에서 필요한 최소한의 정보만 담아 전송한다.
- **이벤트 기반 아키텍처**: 가입 완료를 하나의 '이벤트'로 보고 이를 메시지 큐(Kafka)에 던져 다른 서비스가 이를 인지하게 만든다.
- **JSON 직렬화**: 네트워크를 통해 객체를 전송하기 위해 `ObjectMapper`를 사용하여 JSON 문자열로 변환한다.

---

## ➡️ 다음 단계
이제 User Service의 구현이 완료되었다. 다음 단계에서는 Kafka로부터 이벤트를 수신하여 실제로 이메일을 발송하는 처리를 담당할 **Email Service**의 초기 환경을 설정한다.

- [[실습] Email Service 서버 초기 환경 설정](./Email-Service-Setup.md)
