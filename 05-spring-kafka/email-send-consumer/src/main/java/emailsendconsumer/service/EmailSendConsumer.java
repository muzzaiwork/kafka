package emailsendconsumer.service;

import emailsendconsumer.message.EmailSendMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailSendConsumer {

  @KafkaListener(
      topics = "email.send",
      groupId = "email-send-group"
  )
  public void consume(String message) {
    System.out.println("Kafka로부터 받아온 메시지: " + message);
    
    EmailSendMessage emailSendMessage = EmailSendMessage.fromJson(message);

    // 실제 이메일 발송 로직은 생략
    try {
      Thread.sleep(3000); // 이메일 발송을 하는 데 3초가 걸린다고 가정
    } catch (InterruptedException e) {
      throw new RuntimeException("이메일 발송 실패");
    }
    
    System.out.println("이메일 발송 완료: " + emailSendMessage.getSubject());
  }
}
