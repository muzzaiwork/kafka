# Kafka 학습 노트

이 프로젝트는 카프카(Kafka)를 학습하며 정리한 내용을 담고 있습니다.
노션 강의 커리큘럼에 따라 목차를 구성하고, 각 단계별로 디렉토리를 나누어 정리합니다.

## 📚 목차

### 1. 카프카 기본 개념 (Introduction)
- [카프카란 무엇인가?](./01-introduction/Introduction.md)
- [메시지 큐(Message Queue)란?](./01-introduction/Introduction.md#message-queue)
- [REST API vs 메시지 큐 통신](./01-introduction/Introduction.md#rest-api-vs-mq)
- [핵심 구성 요소: 프로듀서와 컨슈머](./01-introduction/Introduction.md#producer-consumer)
- [카프카의 탄생 배경과 특징](./01-introduction/Introduction.md#background)

### 2. 환경 구성 (Setup)
- [AWS EC2 환경 셋팅하기](./02-setup/Setup.md#ec2-setup)
- [AWS EC2에 Kafka 설치 및 실행하기](./02-setup/Setup.md#ec2-kafka-install)
- [로컬 환경 설치 및 실행](./02-setup/Setup.md)
- [주키퍼(Zookeeper)와 카프카 서버 구성](./02-setup/Setup.md#components)

### 3. 카프카 기본 아키텍처 (Architecture)
- [토픽(Topic)과 파티션(Partition)](./03-architecture/Architecture.md)
- [CLI를 활용한 토픽 관리](./03-architecture/Architecture.md#topic-cli)
- [카프카의 기본 구성 요소](./03-architecture/Architecture.md#components)
- [브로커(Broker)와 클러스터(Cluster)](./03-architecture/Architecture.md#broker)
- [리플리케이션(Replication)과 ISR](./03-architecture/Architecture.md#replication)

### 4. 프로듀서와 컨슈머 (Producer & Consumer)
- [프로듀서의 역할과 메시지 전송 방식](./04-producer-consumer/ProducerConsumer.md#producer)
- [컨슈머와 컨슈머 그룹(Consumer Group)](./04-producer-consumer/ProducerConsumer.md#consumer)

### 5. 상세 동작 및 활용 (Advanced)
- [메시지 전달 보장 (Delivery Semantics)](./05-advanced/Advanced.md)
- [카프카 스트림즈와 커넥트 (Streams & Connect)](./05-advanced/Advanced.md#ecosystem)

---

*본 목차는 학습 진행 상황에 따라 업데이트될 예정입니다.*
