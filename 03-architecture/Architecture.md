# 03. 카프카 기본 아키텍처

## 토픽(Topic)과 파티션(Partition)

### 토픽(Topic)이란?
카프카에서 **메시지의 종류를 구분하는 단위**이다. 파일 시스템의 폴더나 데이터베이스의 테이블과 유사한 개념으로, 특정 주제나 카테고리에 따라 메시지를 저장한다.

- **메시지 분류**: 프로듀서는 특정 토픽을 지정하여 메시지를 전송하고, 컨슈머는 관심 있는 토픽을 구독하여 데이터를 소비한다.
- **데이터 저장**: 카프카는 전달받은 메시지를 토픽별로 구분하여 메시지 큐에 안전하게 저장한다.

---

## 카프카의 기본 구성 요소 (Producer, Consumer, Topic) <a name="components"></a>

카프카의 전체적인 동작 흐름은 **프로듀서**, **컨슈머**, 그리고 **토픽** 간의 상호작용으로 이루어진다.

```mermaid
graph LR
    P[프로듀서<br/>Producer] -- "메시지 전송" --> T[카프카 토픽<br/>Topic]
    T -- "메시지 조회(Polling)" --> C[컨슈머<br/>Consumer]
```

1. **프로듀서(Producer)**: 카프카(토픽)에 메시지(데이터)를 전달하는 주체이다.
2. **카프카 토픽(Topic)**: 전달받은 메시지를 카테고리별로 구분하여 보관하는 임시 저장소이다.
3. **컨슈머(Consumer)**: 카프카에 새로운 메시지가 생겼는지 주기적으로 체크(Polling)하다가, 데이터가 있으면 가져와서 처리하는 주체이다.

---

## CLI를 활용한 토픽 관리 <a name="topic-cli"></a>

실제 운영 환경이나 실습 시 CLI(Command Line Interface)를 통해 토픽을 직접 생성하고 관리할 수 있다.

### 1. 토픽 생성하기
`kafka-topics.sh` 스크립트에 `--create` 옵션을 사용하여 새로운 토픽을 생성한다.

```bash
# kafka 디렉터리 안에서 실행 (예: kafka_2.13-4.0.0)
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --create \
    --topic email.send
```

### 2. 토픽 조회하기
생성된 토픽의 목록을 확인하거나 특정 토픽의 상세 정보를 조회한다.

**전체 목록 조회:**
```bash
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --list
```

**특정 토픽 상세 정보 조회:**
```bash
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --describe --topic email.send
```

### 3. 토픽 삭제하기
더 이상 사용하지 않는 토픽을 삭제한다.

```bash
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --delete --topic email.send
```

---

## 브로커(Broker)와 클러스터(Cluster) <a name="broker"></a>

## 리플리케이션(Replication)과 ISR <a name="replication"></a>
