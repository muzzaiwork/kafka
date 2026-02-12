# 05. Spring Boot와 카프카 연동 - Producer 구현

## Spring Boot로 Kafka에 메시지 넣는 코드 작성하기 (Producer)

Spring Boot 서버를 활용해 카프카에 메시지를 전송하는 프로듀서를 구현한다.

### 1. DTO 및 메시지 객체 생성

사용자의 요청을 받을 DTO와 카프카에 전송할 메시지 객체를 정의한다.

**SendEmailRequestDto.java** (Request Body 수신용)
```java
public class SendEmailRequestDto {
  private String from;         // 발신자 이메일
  private String to;           // 수신자 이메일
  private String subject;      // 이메일 제목
  private String body;         // 이메일 본문

  public String getFrom() { return from; }
  public String getTo() { return to; }
  public String getSubject() { return subject; }
  public String getBody() { return body; }
}
```

**EmailSendMessage.java** (Kafka 전송용 메시지 객체)
```java
public class EmailSendMessage {
  private String from;
  private String to;
  private String subject;
  private String body;

  public EmailSendMessage(String from, String to, String subject, String body) {
    this.from = from;
    this.to = to;
    this.subject = subject;
    this.body = body;
  }
  // Getter 생략
}
```

### 2. Controller 구현

사용자로부터 API 요청을 받아 서비스를 호출하는 컨트롤러를 작성한다.

**EmailController.java**
```java
@RestController
@RequestMapping("/api/emails")
public class EmailController {
  private final EmailService emailService;

  public EmailController(EmailService emailService) {
    this.emailService = emailService;
  }

  @PostMapping
  public ResponseEntity<String> sendEmail(@RequestBody SendEmailRequestDto requestDto) {
    emailService.sendEmail(requestDto);
    return ResponseEntity.ok("이메일 발송 요청 완료");
  }
}
```

### 3. Service 구현

`KafkaTemplate`을 사용하여 실제로 카프카 토픽에 메시지를 전송하는 로직을 구현한다.

**EmailService.java**
```java
@Service
public class EmailService {
  private final KafkaTemplate<String, String> kafkaTemplate;

  public EmailService(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendEmail(SendEmailRequestDto request) {
    EmailSendMessage emailSendMessage = new EmailSendMessage(
      request.getFrom(), request.getTo(), request.getSubject(), request.getBody()
    );
    
    // 객체를 JSON 문자열로 직렬화하여 'email.send' 토픽으로 전송한다.
    this.kafkaTemplate.send("email.send", toJsonString(emailSendMessage));
  }
  
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

카프카에 메시지를 전송하는 프로듀서 코드 작성을 완료했다. 다음 단계에서는 Spring Boot 애플리케이션이 카프카에 메시지를 정상적으로 전송하는지 테스트한다.
