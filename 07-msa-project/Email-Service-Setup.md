# [실습] Spring Boot로 Email Service 서버 초기 환경 설정하기

MSA 프로젝트의 두 번째 서비스인 **Email Service** 서버의 초기 환경을 설정하고, 카프카 컨슈머 설정 및 데이터베이스(H2) 연결 상태를 확인한다.

---

## ✅ 실습 과정

### 1. Spring Boot 프로젝트 셋팅

[start.spring.io](https://start.spring.io/)에서 다음과 같이 프로젝트를 생성한다.

- **Project**: Gradle - Groovy
- **Language**: Java
- **Spring Boot**: 3.x.x
- **Artifact / Name**: `email-service`
- **Package name**: `emailservice`
- **Java**: 21 (실습 기준 버전)
- **Dependencies**:
    - `Spring Boot DevTools`: 개발 편의 도구
    - `Spring Web`: REST API 구현용
    - `Spring for Apache Kafka`: 카프카 연동 라이브러리
    - `H2 Database`: 인메모리 데이터베이스 (테스트용)
    - `Spring Data JPA`: 데이터베이스 조작을 위한 ORM 도구

### 2. application.yml 설정

`src/main/resources/application.properties` 파일을 삭제하고 `application.yml` 파일을 생성하여 다음과 같이 작성한다.

**application.yml**
```yaml
server:
  port: 8081 # Email Service는 8081 포트 사용 (User Service와 충돌 방지)

spring:
  kafka:
    # 연결시킬 Kafka 클러스터 주소
    bootstrap-servers:
      - {Kafka-IP}:9092
      - {Kafka-IP}:19092
      - {Kafka-IP}:29092
    consumer:
      # 메시지 역직렬화: Kafka에서 받아온 String을 객체로 변환하기 위해 String으로 먼저 수신
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      
      # 컨슈머 그룹 설정: 그룹이 없으면 처음부터 메시지를 읽음(earliest)
      # 이 옵션이 없으면 그룹 생성 전의 메시지는 누락될 수 있음
      auto-offset-reset: earliest

  # H2 데이터베이스 설정
  h2:
    console:
      enabled: true
  datasource:
    url: jdbc:h2:mem:emailDB # Email Service 전용 DB
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

---

## ✅ 연결 및 실행 확인

### 1. 애플리케이션 실행
작성한 설정을 바탕으로 Spring Boot 서버를 실행한다. 로그에 에러 없이 `Started EmailServiceApplication` 메시지가 뜬다면 성공이다.

### 2. H2 데이터베이스 콘솔 접속
웹 브라우저에서 다음 주소로 접속하여 DB 연결을 확인한다.
- **주소**: [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
- **JDBC URL**: `jdbc:h2:mem:emailDB`

`Connect` 버튼을 눌렀을 때 DB 관리 화면이 정상적으로 나타나면 데이터베이스 설정이 완료된 것이다.

---

## ➡️ 다음 단계

초기 설정이 완료되었다면, 이제 User Service에서 발행한 `user.signed-up` 이벤트를 구독하여 실제 이메일 발송 기능을 구현한다.

- [Email Service 구현하기 (추후 추가 예정)]
