package emailservice.service;

import emailservice.dto.UserSignedUpEvent;
import emailservice.entity.EmailLog;
import emailservice.repository.EmailLogRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class UserSignedUpEventConsumer {

  private final EmailLogRepository emailLogRepository;

  public UserSignedUpEventConsumer(EmailLogRepository emailLogRepository) {
    this.emailLogRepository = emailLogRepository;
  }

  @KafkaListener(
      topics = "user.signed-up",
      groupId = "email-service",
      concurrency = "3"
  )
  @RetryableTopic(
      attempts = "5",
      backoff = @Backoff(delay = 1000, multiplier = 2.0),
      dltTopicSuffix = ".dlt",
      topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
  )
  public void consume(String message) throws InterruptedException {
    UserSignedUpEvent userSignedUpEvent = UserSignedUpEvent.fromJson(message);

    String receiverEmail = userSignedUpEvent.getEmail();
    String subject = userSignedUpEvent.getName() + "님, 회원 가입을 축하드립니다!";
    
    // 이메일 발송에 3초 정도 시간이 걸리는 걸 가정
    Thread.sleep(3000);
    System.out.println("이메일 발송 완료");

    EmailLog emailLog = new EmailLog(
        userSignedUpEvent.getUserId(),
        receiverEmail,
        subject
    );

    emailLogRepository.save(emailLog);
  }
}
