package emailservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "email_logs")
public class EmailLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long receiverUserId;

  private String receiverEmail;

  private String subject;

  public EmailLog() {
  }

  public EmailLog(Long receiverUserId, String receiverEmail, String subject) {
    this.receiverUserId = receiverUserId;
    this.receiverEmail = receiverEmail;
    this.subject = subject;
  }

  public Long getId() {
    return id;
  }

  public Long getReceiverUserId() {
    return receiverUserId;
  }

  public String getReceiverEmail() {
    return receiverEmail;
  }

  public String getSubject() {
    return subject;
  }
}
