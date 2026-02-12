package emailsendproducer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import emailsendproducer.dto.SendEmailRequestDto;
import emailsendproducer.message.EmailSendMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  // <메시지의 Key 타입, 메시지의 Value 타입>
  // 실습에서는 메시지를 만들 때 key는 생략한 채로 value만 넣을 예정이다.
  private final KafkaTemplate<String, String> kafkaTemplate;

  public EmailService(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendEmail(SendEmailRequestDto request) {
    EmailSendMessage emailSendMessage = new EmailSendMessage(
      request.getFrom(),
      request.getTo(),
      request.getSubject(),
      request.getBody()
    );
    
    // 메시지의 value 타입을 String으로 설정했으므로 객체를 String으로 변환해서 전송한다.
    this.kafkaTemplate.send("email.send", toJsonString(emailSendMessage));
  }
  
  // 객체를 Json 형태의 String으로 만들어주는 메서드
  private String toJsonString(Object object) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Json 직렬화 실패");
    }
  }
}
