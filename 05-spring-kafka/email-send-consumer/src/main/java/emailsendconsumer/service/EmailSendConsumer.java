package emailsendconsumer.service;

import emailsendconsumer.message.EmailSendMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class EmailSendConsumer {

  @RetryableTopic(
      attempts = "5",
      backoff = @Backoff(delay = 1000, multiplier = 2)
  )
  @KafkaListener(
      topics = "email.send",
      groupId = "email-send-group"
  )
  public void consume(String message) {
    System.out.println("Kafka로부터 받아온 메시지: " + message);
    
    EmailSendMessage emailSendMessage = EmailSendMessage.fromJson(message);

    // 잘못된 이메일 주소일 경우 실패 가정
    if (emailSendMessage.getTo().equals("fail@naver.com")) {
      System.out.println("잘못된 이메일 주소로 인해 발송 실패");
      throw new RuntimeException("잘못된 이메일 주소로 인해 발송 실패");
    }

    // 실제 이메일 발송 로직은 생략
    try {
      Thread.sleep(10000); // 이메일 발송을 하는 데 10초가 걸린다고 가정
    } catch (InterruptedException e) {
      throw new RuntimeException("이메일 발송 실패");
    }
    
    System.out.println("이메일 발송 완료: " + emailSendMessage.getSubject());
  }
}
