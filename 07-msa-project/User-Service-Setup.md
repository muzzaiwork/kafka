# [실습] Spring Boot로 User Service 서버 초기 환경 설정하기

MSA 프로젝트의 첫 번째 관문인 **User Service** 서버의 초기 환경을 설정하고, 카프카 및 데이터베이스(H2) 연결 상태를 확인한다.

---

## ✅ 실습 과정

### 1. Spring Boot 프로젝트 셋팅

[start.spring.io](https://start.spring.io/)에서 다음과 같이 프로젝트를 생성한다.

- **Project**: Gradle - Groovy
- **Language**: Java
- **Spring Boot**: 3.x.x
- **Artifact / Name**: `user-service`
- **Package name**: `userservice`
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
  port: 8080 # User Service는 8080 포트 사용

spring:
  kafka:
    # 연결시킬 Kafka 클러스터 주소 (실제 서버 IP 또는 도메인 입력)
    bootstrap-servers:
      - {Kafka-IP}:9092
      - {Kafka-IP}:19092
      - {Kafka-IP}:29092
    producer:
      # 메시지 직렬화: 객체를 Kafka 전송을 위해 String으로 변환
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      properties:
        # 메시지 분배 방식: 라운드 로빈 (순차 분배)
        partitioner.class: org.apache.kafka.clients.producer.RoundRobinPartitioner

  # H2 데이터베이스 설정
  h2:
    console:
      enabled: true # 웹 콘솔 사용 활성화
  datasource:
    url: jdbc:h2:mem:userDB # 메모리 모드 DB 주소
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update # 테이블 자동 생성
    show-sql: true # 실행 쿼리 출력
```

---

## ✅ 연결 및 실행 확인

### 1. 애플리케이션 실행
작성한 설정을 바탕으로 Spring Boot 서버를 실행한다. 로그에 에러 없이 `Started UserServiceApplication` 메시지가 뜬다면 성공이다.

### 2. H2 데이터베이스 콘솔 접속
웹 브라우저에서 다음 주소로 접속하여 DB 연결을 확인한다.
- **주소**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **JDBC URL**: `jdbc:h2:mem:userDB` (설정 파일과 동일하게 입력)

`Connect` 버튼을 눌렀을 때 DB 관리 화면이 정상적으로 나타나면 데이터베이스 설정이 완료된 것이다.

---

## ➡️ 다음 단계

초기 설정이 완료되었다면, 이제 실제 회원가입 로직을 구현하고 가입 완료 시 카프카로 메시지를 발행하는 기능을 추가한다.

- [User Service 회원가입 기능 구현하기 (추후 추가 예정)]
