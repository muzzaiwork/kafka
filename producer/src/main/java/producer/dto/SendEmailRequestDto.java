package producer.dto;

public class SendEmailRequestDto {
  private String from;         // 발신자 이메일
  private String to;           // 수신자 이메일
  private String subject;      // 이메일 제목
  private String body;         // 이메일 본문

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

  public String getSubject() {
    return subject;
  }

  public String getBody() {
    return body;
  }
}
