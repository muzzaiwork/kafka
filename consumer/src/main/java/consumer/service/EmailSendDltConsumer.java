package consumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailSendDltConsumer {
  @KafkaListener(
      topics = "email.send.dlt",
      groupId = "email-send-dlt-group"
  )
  public void consume(String message) {
    // 실제 운영 환경에서는 로그 시스템(ELK 등)에 저장하거나 Slack 알림을 보낸다.
    System.out.println("[DLT Post-Processing] 로그 시스템에 전송: " + message);
    System.out.println("[DLT Post-Processing] Slack에 장애 알림 발송");
  }
}
