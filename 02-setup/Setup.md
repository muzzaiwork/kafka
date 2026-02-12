# 02. 환경 구성

## AWS EC2 환경 셋팅하기 <a name="ec2-setup"></a>

카프카를 실습하기 위해 AWS EC2 인스턴스를 생성하고 접속하는 과정을 정리한다.

### 1. EC2 인스턴스 생성하기

1. **리전 선택**: 가까운 리전(예: 서울 리전)을 선택한다.
2. **인스턴스 시작**: '인스턴스 시작' 버튼을 클릭하여 설정 페이지로 이동한다.
3. **이름 및 OS 설정**:
    - 인스턴스의 이름을 설정한다.
    - 운영체제(AMI)를 선택한다 (예: Amazon Linux 2023 등).
4. **인스턴스 유형 및 키 페어**:
    - **인스턴스 유형**: '프리 티어 사용 가능' 유형(예: t2.micro 또는 t3.micro)을 선택한다.
    - **키 페어**: 실습의 편의를 위해 키 페어 없이 생성이 가능하나, 보안을 위해 생성하는 것을 권장한다. (강의에서는 편의상 없이 진행)
5. **네트워크 설정 (보안 그룹)**:
    - **보안 그룹**: 새로운 보안 그룹 생성을 선택한다.
    - **규칙 설정**: 실습 편의를 위해 모든 TCP를 '위치 무관(0.0.0.0/0)'으로 허용한다.
    - *주의: 실제 운영 환경에서는 카프카 접속이 필요한 특정 IP만 허용해야 한다.*
6. **인스턴스 시작**: 나머지 설정은 기본값으로 두고 인스턴스를 시작한다.

---

### 2. 생성한 인스턴스로 접속하기

1. **인스턴스 목록**: EC2 인스턴스 목록에서 생성한 인스턴스를 확인한다.
2. **연결**: 인스턴스를 선택하고 '연결' 버튼을 클릭한다.
3. **EC2 인스턴스 연결**: 웹 브라우저 기반의 터미널을 통해 간편하게 인스턴스에 접속할 수 있다.

---

## AWS EC2에 Kafka 설치 및 실행하기 <a name="ec2-kafka-install"></a>

EC2 인스턴스에 접속한 후, 카프카를 설치하고 실행하는 과정을 정리한다.

### 1. JDK 17 설치하기

카프카 4.0 이상을 실행하려면 JDK 17 이상의 버전이 필요하다.

```bash
$ sudo apt update
$ sudo apt install openjdk-17-jdk
$ java -version # 설치 확인
```

### 2. Kafka 설치 및 환경 설정

#### 2.1. 카프카 다운로드 및 압축 해제
```bash
$ wget https://dlcdn.apache.org/kafka/4.0.0/kafka_2.13-4.0.0.tgz
$ tar -xzf kafka_2.13-4.0.0.tgz
$ cd kafka_2.13-4.0.0
```

#### 2.2. 메모리 부족 문제 해결 (Heap Memory & Swap)
EC2 프리티어(t3.micro)는 메모리가 1GB로 부족하므로 설정을 변경해야 한다.

**힙 메모리 크기 낮추기:**
```bash
$ export KAFKA_HEAP_OPTS="-Xmx400m -Xms400m"
```

**Swap 메모리 설정 (2GB):**

> **Swap이란?**
> 실제 메모리(RAM)가 부족할 때, 하드 디스크의 일부 공간을 메모리처럼 사용하는 기술이다. 
> RAM보다 속도는 느리지만, 메모리 부족으로 인해 프로세스가 강제 종료되는 현상을 방지할 수 있다. 
> 프리티어(t3.micro)와 같이 RAM 용량이 적은 환경에서는 필수적으로 설정하는 것이 좋다.

```bash
$ sudo dd if=/dev/zero of=/swapfile bs=128M count=16
$ sudo chmod 600 /swapfile
$ sudo mkswap /swapfile
$ sudo swapon /swapfile

# 부팅 시 자동 활성화를 위해 /etc/fstab 수정
$ sudo vi /etc/fstab
# 파일 끝에 다음 내용 추가: /swapfile swap swap defaults 0 0

$ free # swap 메모리 확인
```

**실행 결과:**
```text
               total        used        free      shared  buff/cache   available
Mem:          936152      376532      184908        2772      543452      559620
Swap:        2097148           0     2097148
```

#### 2.3. Kafka 설정 수정 (`server.properties`)
외부 클라이언트가 접속할 수 있도록 EC2의 Public IP를 설정한다.

```bash
$ vi config/server.properties
```

```properties
# advertised.listeners 주소 수정
advertised.listeners=PLAINTEXT://{EC2_Public_IP}:9092,CONTROLLER://{EC2_Public_IP}:9093
```

### 3. Kafka 서버 실행 및 종료

#### 3.1. 서버 시작 (최초 실행 시 로그 폴더 세팅 포함)
```bash
# 클러스터 ID 생성 및 포맷
$ KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"
$ bin/kafka-storage.sh format --standalone -t $KAFKA_CLUSTER_ID -c config/server.properties

# 카프카 서버 백그라운드 실행
$ bin/kafka-server-start.sh -daemon config/server.properties

# 로그 확인
$ tail -f logs/kafkaServer.out

# 실행 확인 (9092 포트)
$ sudo lsof -i:9092
```

#### 3.2. 서버 종료 및 재시작
```bash
# 종료
$ bin/kafka-server-stop.sh

# 재시작 (백그라운드)
$ bin/kafka-server-start.sh -daemon config/server.properties
```

---

### ✅ 참고) 카프카 명령어와 쉘 스크립트 <a name="kafka-cli-info"></a>

카프카 디렉터리 내부의 `bin` 디렉터리에는 다양한 쉘 스크립트 파일(`.sh`)이 존재한다. 실제 카프카 명령어를 실행할 때 이 스크립트들을 활용한다.

- **스크립트의 역할**: 카프카 서버 실행, 종료, 토픽 생성, 설정 변경 등 모든 관리 작업은 `bin` 폴더 내의 해당 스크립트를 호출하여 수행된다.
- **실행 방법**: 실행하고자 하는 파일의 경로(예: `bin/kafka-server-start.sh`)를 직접 입력하여 명령어를 시작한다.
- **팁**: 리눅스 환경에서 쉘 스크립트 실행 개념을 숙지하면 카프카를 더 효율적으로 다룰 수 있다.

---

## 로컬 환경 설치 및 실행

## 주키퍼(Zookeeper)와 카프카 서버 구성 <a name="components"></a>
