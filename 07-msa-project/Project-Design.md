# 07. MSA 프로젝트 실습 - 프로젝트 설계

강의 초반에 언급했듯이, Kafka는 MSA(Microservice Architecture) 구조에서 서비스 간의 결합도를 낮추고 비동기 통신을 처리하기 위해 널리 사용된다. 실제 MSA 환경과 유사한 간단한 프로젝트를 설계하고 구축해본다.

---

## ✅ 구현할 기능

**"회원 가입을 하면 회원가입 축하 이메일을 자동으로 발송하는 시스템"**

### 1. 회원 가입 기능 (User Service)
- 사용자의 가입 정보를 입력받아 DB에 저장한다.
- 가입이 완료되면 이메일 발송을 위한 이벤트를 Kafka로 발행한다.

### 2. 이메일 발송 기능 (Email Service)
- Kafka에서 회원가입 이벤트를 구독(Consume)한다.
- 수신한 정보를 바탕으로 이메일을 발송하고, 발송 기록을 DB에 저장한다.

---

## ✅ 프로젝트 아키텍처

```mermaid
graph TD
    subgraph Client [사용자 환경]
        User((사용자))
    end

    subgraph UserService [User Service - Producer]
        direction TB
        U_Controller[User Controller]
        U_Service[User Service]
        U_DB[(H2 Database)]
        
        U_Controller --> U_Service
        U_Service --- U_DB
    end

    subgraph KafkaClusterBox [카프카 클러스터 - 3 Nodes, 3 Partitions, RF=3]
        direction TB
        subgraph Node1 [노드 1]
            direction TB
            P0L[P0 Leader]
            P1F1[P1 Follower]
            P2F1[P2 Follower]
        end
        subgraph Node2 [노드 2]
            direction TB
            P1L[P1 Leader]
            P0F1[P0 Follower]
            P2F2[P2 Follower]
        end
        subgraph Node3 [노드 3]
            direction TB
            P2L[P2 Leader]
            P0F2[P0 Follower]
            P1F2[P1 Follower]
        end
        
        TopicFlow{user.signed-up<br/>Partitioned}
        DLT_Topic{{user.signed-up.dlt<br/>Dead Letter Topic}}
    end

    subgraph EmailService [Email Service - Consumer Group]
        direction TB
        E_Consumer1[Email Consumer 1]
        E_Consumer2[Email Consumer 2]
        E_Consumer3[Email Consumer 3]
        E_Service[Email Service Logic]
        E_DB[(H2 Database)]
        
        E_Consumer1 & E_Consumer2 & E_Consumer3 --> E_Service
        E_Service --- E_DB
    end

    %% 1. 클라이언트 요청
    User -- "1. 회원가입 요청" --> U_Controller
    U_Service -- "2. 사용자 정보 저장" --> U_DB
    
    %% 3. 프로듀서가 파티션 리더들에게 메시지 분산 전송
    U_Service -- "3. 메시지 분산 전송" --> TopicFlow
    TopicFlow -- "P0 (Node 1)" --> P0L
    TopicFlow -- "P1 (Node 2)" --> P1L
    TopicFlow -- "P2 (Node 3)" --> P2L

    %% 4. 리더에서 팔로워로 내부 복제 (카프카 내부 동작)
    P0L -. "복제" .-> P0F1 & P0F2
    P1L -. "복제" .-> P1F1 & P1F2
    P2L -. "복제" .-> P2F1 & P2F2

    %% 5. 컨슈머가 각 파티션의 리더로부터 메시지 수신
    P0L -- "4. 구독 (P0)" --> E_Consumer1
    P1L -- "4. 구독 (P1)" --> E_Consumer2
    P2L -- "4. 구독 (P2)" --> E_Consumer3
    
    E_Service -- "5. 이메일 발송 & 로그 저장" --> E_DB
    E_Service -. "6. 처리 실패 시 DLT 발행" .-> DLT_Topic

    %% 스타일 설정
    style User fill:#ffffff,stroke:#333
    style UserService fill:#e3f2fd,stroke:#1565c0
    style EmailService fill:#f1f8e9,stroke:#33691e
    style KafkaClusterBox fill:#fff3e0,stroke:#e65100
    style Node1 fill:#ffffff,stroke:#e65100
    style Node2 fill:#ffffff,stroke:#e65100
    style Node3 fill:#ffffff,stroke:#e65100
    style P0L fill:#dfd,stroke:#333,stroke-width:2px
    style P1L fill:#dfd,stroke:#333,stroke-width:2px
    style P2L fill:#dfd,stroke:#333,stroke-width:2px
    style TopicFlow fill:#fff,stroke:#e65100
    style DLT_Topic fill:#fff,stroke:#e65100,stroke-dasharray: 5 5
```

### ✅ 상세 동작 메커니즘
1. **파티션 분산 처리 (Partitioning)**:
   - 토픽 `user.signed-up`은 3개의 파티션으로 나뉘어 있으며, 각 파티션의 **리더**가 서로 다른 노드(1, 2, 3)에 골고루 분산되어 부하를 분산한다.
   - **중요**: 파티션과 노드는 1:1로 매핑되는 것이 아닙니다. 하나의 노드는 여러 파티션(리더 또는 팔로워)을 호스팅할 수 있습니다. 예를 들어 노드 1은 P0의 리더이면서 P1, P2의 팔로워 파티션을 모두 가집니다.
   - User Service(Producer)는 라운드 로빈 방식을 통해 P0, P1, P2 리더들에게 메시지를 순차적으로 전송한다.

2. **리더와 팔로워의 역할 (Leader/Follower)**:
   - **리더 (Leader)**: 그림의 연한 녹색 노드들로, 실제 메시지 생산(Write)과 소비(Read)가 일어나는 핵심 주체다.
   - **팔로워 (Follower)**: 리더 노드의 데이터를 실시간으로 복제하여 저장하며, 장애 발생 시 리더 자리를 이어받을 준비를 한다.

3. **컨슈머 그룹의 병렬 처리 (Parallel Processing)**:
   - Email Service는 3개의 컨슈머가 하나의 그룹으로 묶여 각 파티션의 리더로부터 메시지를 나누어 읽는다.
   - 이를 통해 대량의 회원가입 이벤트를 지연 없이 빠르게 처리할 수 있다.

4. **트래픽 급증 시의 완충 작용 (Buffering & Decoupling)**:
   - **상황**: 이벤트 기간이나 마케팅 알림 등으로 인해 회원가입 요청이 갑자기 10배 이상 폭주하는 상황을 가정해보자.
   - **기존 방식 (REST API)**: User Service가 Email Service의 응답을 기다려야 하므로, Email Service가 느려지면 User Service까지 줄줄이 마비된다.
   - **Kafka 방식**: User Service는 메시지를 카프카에 던지기만 하면 끝난다. Email Service가 아무리 느려져도 User Service의 회원가입 기능은 영향을 받지 않고 빠르게 응답할 수 있다.
   - **결과**: 이때 발생하는 것이 **컨슈머 랙(Consumer Lag)**이다. 카프카가 거대한 담수지 역할을 하여 메시지를 안전하게 보관해주고, Email Service는 자신이 처리할 수 있는 속도로 메시지를 차근차근 가져와서 처리한다. 서비스 전체가 다운되는 '연쇄 장애'를 막아주는 핵심 메커니즘이다.

---

## ✅ 참고 사항

- 이 실습은 MSA 자체의 복잡한 기법(Service Discovery, API Gateway 등)보다는 **Kafka를 활용한 서비스 간 통신**에 집중한다.
- MSA에 대한 깊은 지식이 없더라도 Kafka의 프로듀서와 컨슈머 개념을 이해하고 있다면 충분히 따라올 수 있도록 구성했다.

---

## ➡️ 다음 단계
- [[실습] User Service 서버 초기 환경 설정](./User-Service-Setup.md)
- [[실습] Email Service 서버 초기 환경 설정](./Email-Service-Setup.md)
