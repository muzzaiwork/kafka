package emailservice.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserSignedUpEvent {
  private Long userId;
  private String email;
  private String name;

  // 역직렬화(String 형태의 카프카 메시지 -> Java 객체)시 필요함
  public UserSignedUpEvent() {
  }

  public UserSignedUpEvent(Long userId, String email, String name) {
    this.userId = userId;
    this.email = email;
    this.name = name;
  }

  public static UserSignedUpEvent fromJson(String json) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(json, UserSignedUpEvent.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("JSON 파싱 실패");
    }
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
