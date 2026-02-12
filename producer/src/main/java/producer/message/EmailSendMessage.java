package producer.message;

public class EmailSendMessage {
  private String from;         // 발신자 이메일
  private String to;           // 수신자 이메일
  private String subject;      // 이메일 제목
  private String body;         // 이메일 본문

  public EmailSendMessage(String from, String to, String subject, String body) {
    this.from = from;
    this.to = to;
    this.subject = subject;
    this.body = body;
  }

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
