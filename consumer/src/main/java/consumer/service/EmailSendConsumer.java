package consumer.service;

import consumer.message.EmailSendMessage;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class EmailSendConsumer {

  @RetryableTopic(
      // 총 시도 횟수 (최초 시도 1회 + 재시도 4회)
      attempts = "5",
      // 재시도 간격 설정
      // delay: 첫 재시도 대기 시간 (1000ms = 1초)
      // multiplier: 이전 대기 시간에 곱할 값 (1초 -> 2초 -> 4초 -> 8초 순으로 증가)
      backoff = @Backoff(delay = 1000, multiplier = 2),
      // DLT(Dead Letter Topic) 토픽 이름에 붙일 접미사 설정
      // 기본값은 "-dlt"이나, 여기서는 ".dlt"로 커스텀 설정
      dltTopicSuffix = ".dlt"
  )
  @KafkaListener(
      topics = "email.send",
      groupId = "email-send-group",
      concurrency = "3"
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

  @RetryableTopic(
      attempts = "3",
      backoff = @Backoff(delay = 1000, multiplier = 2),
      autoCreateTopics = "true",
      // DLT를 사용하지 않으려면 dltStrategy를 NO_DLT로 설정한다.
      dltStrategy = org.springframework.kafka.retrytopic.DltStrategy.NO_DLT
  )
  @KafkaListener(
      topics = "email.send.retry-only",
      groupId = "email-send-retry-only-group"
  )
  public void consumeRetryOnly(String message) {
    System.out.println("[Retry Only] Kafka로부터 받아온 메시지: " + message);
    EmailSendMessage emailSendMessage = EmailSendMessage.fromJson(message);

    if (emailSendMessage.getTo().equals("fail@naver.com")) {
      System.out.println("[Retry Only] 발송 실패 - 재시도 수행");
      throw new RuntimeException("발송 실패");
    }

    System.out.println("[Retry Only] 이메일 발송 완료");
  }

  @DltHandler
  public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    System.out.println("[DLT] " + topic + " 로부터 넘어온 메시지: " + message);
  }
}
