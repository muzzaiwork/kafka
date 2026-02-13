# [실습] 회원가입 API 전체 뼈대 만들기

User Service의 핵심 기능인 회원가입 API를 구현하기 위해 엔티티, 리포지토리, DTO, 컨트롤러, 서비스의 기본 구조(뼈대)를 생성한다.

---

## ✅ 주요 구성 요소 구현

### 1. User 엔티티 생성
데이터베이스의 `users` 테이블과 매핑될 엔티티 클래스를 생성한다.

**User.java**
```java
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;

  private String name;

  private String password;

  public User() {
  }

  public User(String email, String name, String password) {
    this.email = email;
    this.name = name;
    this.password = password;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }
}
```

### 2. UserRepository 생성
JPA를 사용하여 DB에 접근하기 위한 리포지토리 인터페이스를 생성한다.

**UserRepository.java**
```java
public interface UserRepository extends JpaRepository<User, Long> {
}
```

### 3. SignUpRequestDto 생성
클라이언트로부터 회원가입 요청 데이터를 전달받을 DTO를 생성한다.

**SignUpRequestDto.java**
```java
public class SignUpRequestDto {
  private String email;
  private String name;
  private String password;

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }
}
```

### 4. UserController 생성
회원가입 요청을 받을 REST 컨트롤러를 생성한다.

**UserController.java**
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }
  
  @PostMapping
  public ResponseEntity<String> signUp(
      @RequestBody SignUpRequestDto signUpRequestDto
  ) {
    userService.signUp(signUpRequestDto);
    return ResponseEntity.ok("회원가입 성공");
  }
}
```

### 5. UserService 생성
비즈니스 로직을 처리하고 Kafka 메시지를 발행할 서비스 클래스를 생성한다. 현재는 뼈대만 작성한다.

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
    // TODO: 회원 정보 저장 및 Kafka 이벤트 발행 로직 구현 예정
  }
}
```

---

## ➡️ 다음 단계

API의 뼈대가 완성되었다면, 이제 실제 DB에 사용자를 저장하고 가입 성공 시 Kafka 토픽(`user.signup`)으로 메시지를 발행하는 로직을 완성한다.

- [회원가입 로직 완성 및 Kafka 메시지 발행 (추후 추가 예정)]
