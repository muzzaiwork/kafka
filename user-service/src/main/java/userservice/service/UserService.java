package userservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import userservice.dto.SignUpRequestDto;
import userservice.repository.UserRepository;

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
