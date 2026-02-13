# [실습] 프로젝트 구조에 맞게 Kafka 셋팅하기

프로젝트의 병렬 처리와 고가용성을 보장하기 위해 카프카 서버를 백그라운드에서 실행하고, 실습에 필요한 토픽들을 생성한다.

---

## ✅ 카프카 서버 백그라운드 실행

이전 실습에서는 로그 확인을 위해 포그라운드에서 실행했으나, 실제 프로젝트 운영 환경을 가정하여 3대의 노드를 백그라운드(데몬) 모드로 실행한다.

```bash
# 1. 기존 카프카 및 스프링 부트 서버가 켜져 있다면 모두 종료

# 2. 카프카 노드 3대 백그라운드 실행
$ bin/kafka-server-start.sh -daemon config/server.properties
$ bin/kafka-server-start.sh -daemon config/server2.properties
$ bin/kafka-server-start.sh -daemon config/server3.properties

# 3. 정상 작동 확인 (포트 체크)
$ lsof -i:9092
$ lsof -i:19092
$ lsof -i:29092
```

---

## ✅ 실습용 토픽 생성

기존에 테스트용으로 생성했던 토픽들을 정리하고, MSA 프로젝트 아키텍처에 맞게 새로운 토픽들을 생성한다.

### 1. 기존 토픽 삭제 (선택 사항)
깔끔한 테스트를 위해 기존 토픽을 삭제한다.
```bash
# 전체 토픽 조회
$ bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

# 토픽 삭제
$ bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic email.send
```

### 2. 메인 토픽 생성 (`user.signed-up`)
회원가입 이벤트를 전달할 토픽을 생성한다. 병렬 처리와 고가용성을 위해 파티션과 레플리케이션을 각각 3으로 설정한다.

```bash
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --create \
    --topic user.signed-up \
    --partitions 3 \
    --replication-factor 3
```

### 3. DLT 토픽 생성 (`user.signed-up.dlt`)
재시도에 최종 실패한 메시지를 보관할 DLT 토픽을 생성한다. DLT는 실시간 처리가 급하지 않으므로 파티션은 1개로 충분하지만, 데이터 보존을 위해 레플리케이션은 3으로 설정한다.

```bash
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --create \
    --topic user.signed-up.dlt \
    --partitions 1 \
    --replication-factor 3
```

---

## ✅ 설정 확인

생성된 토픽의 세부 정보를 조회하여 정상적으로 클러스터에 분산되었는지 확인한다.

```bash
$ bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic user.signed-up
$ bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic user.signed-up.dlt
```

---

## ➡️ 다음 단계

카프카 서버와 토픽 셋팅이 모두 완료되었다. 이제 User Service와 Email Service를 모두 실행하여 실제 회원가입 시 이메일 발송 이벤트가 정상적으로 처리되는지 최종 테스트를 진행한다.

- [[실습] MSA 프로젝트 통합 테스트 및 결과 확인](./Project-Final-Test.md)
