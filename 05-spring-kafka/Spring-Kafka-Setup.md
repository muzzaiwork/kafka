# 05. Spring Boot와 카프카 연동

## Spring Boot에 Kafka 연결하기

동일한 환경에서 실습을 진행하기 위해 아래 버전을 사용할 것을 권장한다.
- **Spring Boot**: 3.x.x 버전
- **JDK**: 21

### 1. Spring Boot 프로젝트 셋팅

[start.spring.io](https://start.spring.io/)에서 다음과 같이 프로젝트를 생성한다.

- **Project**: Gradle - Groovy (또는 사용자가 익숙한 빌드 도구)
- **Language**: Java
- **Spring Boot**: 3.x.x
- **Artifact / Name**: `email-send-producer`
- **Package name**: `emailsendproducer`
- **Java**: 21
- **Dependencies**:
    - `Spring Boot DevTools`
    - `Spring Web`
    - `Spring for Apache Kafka`

### 2. application.yml 설정

`src/main/resources/application.properties` 파일을 삭제하고 `application.yml` 파일을 생성하여 다음과 같이 작성한다.

**application.yml**
```yaml
spring:
  kafka:
    # Kafka 서버 주소 (EC2에 카프카를 설치했으므로 해당 EC2의 Public IP를 입력해야 한다.)
    bootstrap-servers: 15.164.96.71:9092
    producer:
      # 메시지의 key 직렬화 방식: 자바 객체를 문자열(String)로 변환해서 Kafka에 전송한다.
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      # 메시지의 value 직렬화 방식: 자바 객체를 문자열(String)로 변환해서 Kafka에 전송한다.
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

- `bootstrap-servers`: EC2에 설치된 카프카 브로커의 주소를 입력한다.
- `key-serializer` / `value-serializer`: 카프카는 메시지를 바이트 배열로 주고받기 때문에, 자바 객체를 전송 가능한 형태로 변환하는 직렬화 설정이 필요하다.

---

### 3. 프로젝트 구조 확인

생성된 프로젝트의 기본 구조는 다음과 같다.
- **위치**: `05-spring-kafka/email-send-producer`
- **주요 파일**:
    - `build.gradle`: 의존성 및 자바 버전 설정
    - `src/main/java/emailsendproducer/EmailSendProducerApplication.java`: 메인 애플리케이션 클래스
    - `src/main/resources/application.yml`: 카프카 연결 설정

---

기본적인 Spring Boot의 카프카 설정을 완료했다. 다음 단계에서는 Spring Boot를 사용하여 카프카에 실제로 메시지를 넣는 프로듀서 코드를 작성한다.
