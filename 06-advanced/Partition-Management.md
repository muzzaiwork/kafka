# 06. 상세 동작 및 활용 - 파티션 관리

## 특정 토픽의 파티션 수 조회하기

토픽에 설정된 파티션 수를 조회하려면 토픽의 상세 정보를 조회하는 명령어를 사용한다.

```bash
# 특정 토픽 세부 정보 조회
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --describe --topic email.send
```

**실행 결과 분석:**
- `PartitionCount`: 토픽이 가지고 있는 파티션의 총 개수다. (별도 옵션 없이 생성 시 기본 1개)
- `Partition`: 파티션 번호를 나타내며, 0번부터 시작한다.

---

## 토픽 생성할 때 파티션 수 설정하기

토픽을 처음 생성할 때 `--partitions` 옵션을 사용하여 원하는 파티션 수를 지정할 수 있다.

```bash
# 문법
$ bin/kafka-topics.sh \
    --bootstrap-server <kafka 주소> \
    --create \
    --topic <토픽명> \
    --partitions <파티션 수>

# 실습 예제 (파티션 3개 설정)
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --create \
    --topic test.topic \
    --partitions 3
```

**확인 명령어:**
```bash
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --describe --topic test.topic
```

```mermaid
graph LR
    subgraph Topic [test.topic]
        P0[파티션 #0]
        P1[파티션 #1]
        P2[파티션 #2]
    end
```

---

## 기존 토픽의 파티션 수 변경하기

### 1. 파티션 수 늘리기
이미 생성된 토픽의 파티션 수는 `--alter` 옵션을 사용하여 늘릴 수 있다.

```bash
# 실습 예제 (파티션 수를 5개로 확장)
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --alter \
    --topic test.topic \
    --partitions 5
```

### 2. 파티션 수 줄이기 (불가능)
카프카에서는 한 번 생성된 **파티션의 수를 줄이는 것은 불가능하다.**

```bash
# 파티션을 줄이려고 시도할 경우 (에러 발생)
$ bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --alter \
    --topic test.topic \
    --partitions 3
```

**왜 줄일 수 없을까?**
- 파티션을 줄이는 과정에서 데이터 손실이 발생할 수 있다.
- 내부적인 데이터 재배치(Rebalancing) 과정에서 성능 저하 및 복잡한 문제가 발생할 위험이 크기 때문에 카프카는 이를 지원하지 않는다.

**해결 방법:**
- 파티션 수를 줄여야 한다면, 원하는 파티션 수로 **새로운 토픽을 생성**한 뒤 데이터를 마이그레이션해야 한다.
- 따라서 처음 토픽을 생성할 때 파티션 수를 신중하게 결정하는 것이 권장된다.

---

다음 강의에서는 늘어난 파티션을 활용하여 실제로 어떻게 병렬 처리가 이루어지는지 실습을 통해 확인해본다.
