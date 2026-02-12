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

**실행 결과:**
```text
Created topic email.send.
```

### 2. 토픽 조회하기
생성된 토픽의 목록을 확인하거나 특정 토픽의 상세 정보를 조회한다.

**전체 목록 조회:**
```bash
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --list
```

**실행 결과 (예시):**
```text
__consumer_offsets
email.send
```

**특정 토픽 상세 정보 조회:**
```bash
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --describe --topic email.send
```

**실행 결과 (예시):**
```text
Topic: email.send  TopicId: ... PartitionCount: 1  ReplicationFactor: 1  Configs: ...
    Topic: email.send  Partition: 0  Leader: 0  Replicas: 0  Isr: 0
```

### 3. 토픽 삭제하기
더 이상 사용하지 않는 토픽을 삭제한다.

```bash
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --delete --topic email.send
```

**실행 결과:**
```text
Topic email.send is marked for deletion.
Note: This will have no impact if delete.topic.enable is not set to true.
```

---

## 노드(node), 브로커(broker), 컨트롤러(controller), 클러스터(cluster) <a name="broker"></a>

카프카의 **고가용성**(시스템이 장애 상황에서도 멈추지 않고 정상적으로 서비스를 제공할 수 있는 능력)을 확보하기 위해 반드시 알아야 할 용어들이다.

### ✅ 노드(node)란?
**노드(node)**란, **카프카가 설치되어 있는 서버 단위**를 의미한다. 전체 프로그래밍 분야에서 널리 쓰이는 개념이며, 카프카에서는 카프카 소프트웨어가 실행되는 물리적/가상 서버 한 대를 의미한다.

```mermaid
graph TD
    subgraph SingleNode [단일 노드 구성 - 위험]
        N1[노드 1]
    end
    
    subgraph MultiNode [멀티 노드 구성 - 권장]
        N2[노드 1] --- N3[노드 2] --- N4[노드 3]
    end
    
    style N1 fill:#ffebee,stroke:#c62828
    style N2 fill:#e8f5e9,stroke:#2e7d32
    style N3 fill:#e8f5e9,stroke:#2e7d32
    style N4 fill:#e8f5e9,stroke:#2e7d32
```

- **장애 상황**: 노드가 1대뿐일 때 해당 노드가 고장나면 서비스 전체가 중단된다.
- **고가용성**: 실무에서는 최소 3대의 노드를 구성하여 한 대가 고장나더라도 중단 없이 작동하게 만든다.

### ✅ 클러스터(cluster)란?
**클러스터(cluster)**란, **여러 대의 서버가 연결되어 하나의 시스템처럼 동작하는 서버들의 집합**을 의미한다.

- 여러 노드가 유기적으로 작동하여 메시지를 나눠 저장하고 복제본을 유지한다.
- 장애 시에도 시스템 전체가 중단 없이 작동하도록 보장한다.

### ✅ 브로커(broker)와 컨트롤러(controller)란?
카프카 서버 프로세스는 크게 **브로커**와 **컨트롤러**라는 두 가지 핵심 역할을 수행한다.

```mermaid
graph TD
    subgraph KafkaNode [카프카 노드 - Kafka Node]
        direction TB
        C[컨트롤러 역할<br/>클러스터 관리/조율]
        B[브로커 역할<br/>메시지 저장/전달]
    end

    P[프로듀서] -- "9092 포트" --> B
    C -- "브로커 상태 관리" --> B
    B -- "Polling" --> Cons[컨슈머]

    style C fill:#e1f5fe,stroke:#01579b
    style B fill:#fff3e0,stroke:#e65100
    style KafkaNode fill:#f5f5f5,stroke:#333,stroke-dasharray: 5 5
```

1. **브로커(broker)**: **메시지를 저장하고 클라이언트의 요청을 처리하는 역할**을 한다. (비유하자면 실무를 담당하는 **직원**)
   - 기본적으로 9092 포트를 사용한다.
2. **컨트롤러(controller)**: **브로커들 간의 연동과 전반적인 클러스터의 상태를 총괄**한다. (비유하자면 **총관리자**)
   - 기본적으로 9093 포트를 사용하며, 별개의 프로세스로 실행된다.

> **실습 안내**: 실제로 하나의 서버에 여러 개의 브로커와 컨트롤러를 띄워 클러스터를 구성하는 방법은 [[실습] 카프카 서버 총 3대 셋팅하기](../02-setup/Multi-Broker-Setup.md)에서 확인할 수 있다.

---

## 리플리케이션(Replication)과 ISR <a name="replication"></a>

### ✅ 리플리케이션(replication)이란?
카프카에서의 **레플리케이션(replication)**은 데이터의 안정성과 가용성을 높이기 위해 **토픽의 파티션을 여러 노드에 복제하는 것**을 의미한다.

```mermaid
graph TD
    subgraph KafkaCluster [카프카 클러스터]
        subgraph N1 [노드 1]
            P0L[파티션 #0 - Leader]
        end
        subgraph N2 [노드 2]
            P0F1[파티션 #0 - Follower]
        end
        subgraph N3 [노드 3]
            P0F2[파티션 #0 - Follower]
        end
    end
    
    P0L -- 복제 --> P0F1
    P0L -- 복제 --> P0F2
    
    style P0L fill:#dfd,stroke:#333,stroke-width:2px
    style N1 fill:#f9f9f9,stroke:#333
    style N2 fill:#f9f9f9,stroke:#333
    style N3 fill:#f9f9f9,stroke:#333
```

- **리더 파티션 (Leader)**: 프로듀서나 컨슈머가 직접적으로 메시지를 쓰고 읽는 원본 파티션이다.
- **팔로워 파티션 (Follower)**: 리더의 데이터를 실시간으로 복제하여 유지하는 복제본이다. 평상시에는 읽기/쓰기에 관여하지 않는다.

### ✅ 리플리케이션의 동작과 장점
1. **고가용성 보장**: 리더 파티션이 있는 노드에 장애가 발생하면, 팔로워 중 하나가 새로운 리더 역할을 즉시 승계한다.
2. **데이터 유실 방지**: 이미 팔로워가 데이터를 복제해 두었기 때문에 장애가 발생해도 메시지를 정상적으로 이어서 처리할 수 있다.
3. **설정**: 실무에서는 보통 레플리케이션 개수(Replication Factor)를 **2 또는 3**으로 설정하여 활용한다.
