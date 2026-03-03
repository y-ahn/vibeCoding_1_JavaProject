# PROJECT 5 — 커머스 주문/결제 서버

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![PortOne](https://img.shields.io/badge/PortOne-결제연동-purple)

## 핵심 학습 포인트
- **포트원(PortOne) 결제 연동** — 결제 검증 서버 구현
- **비관적 락(Pessimistic Lock)** 재고 동시성 처리
- **주문 상태 머신** — 상태 전환 유효성 검증
- **Docker Compose** CD 파이프라인

## 기술 스택
- Spring Boot 3.2.5 / Java 21
- Spring Data JPA + WebFlux (WebClient)
- Spring Security 6 + JWT
- H2 (개발) / MySQL 8.0 (운영)
- Docker / Docker Compose
- Gradle 8.7

## 실행 방법

### 개발 환경 (H2)
```bash
gradle wrapper --gradle-version 8.7
./gradlew bootRun
```

### 운영 환경 (Docker Compose)
```bash
# .env 파일 생성
cat > .env << 'ENVEOF'
DB_PASSWORD=your_db_password
DB_ROOT_PASSWORD=your_root_password
JWT_SECRET=your-jwt-secret-min-32-chars
PORTONE_IMP_KEY=your_imp_key
PORTONE_IMP_SECRET=your_imp_secret
ENVEOF

docker-compose up -d
```

## 접속
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **H2 Console**: http://localhost:8080/h2-console

## API 명세
| Method | URL | 설명 | 인증 |
|--------|-----|------|:----:|
| POST | /api/auth/signup | 회원가입 | ✗ |
| POST | /api/auth/login | 로그인 | ✗ |
| POST | /api/products | 상품 등록 | ✓ |
| GET | /api/products | 상품 목록 | ✗ |
| GET | /api/products/{id} | 상품 단건 조회 | ✗ |
| POST | /api/orders | 주문 생성 | ✓ |
| GET | /api/orders | 내 주문 목록 | ✓ |
| GET | /api/orders/{id} | 주문 상세 | ✓ |
| POST | /api/orders/{id}/cancel | 주문 취소 | ✓ |
| POST | /api/payments/verify | 결제 검증 | ✓ |
| GET | /api/payments/orders/{orderId} | 결제 조회 | ✓ |

## 포트원 결제 플로우
```
1. 클라이언트 → POST /api/orders → 주문 생성 (PENDING)
2. 클라이언트 → 포트원 결제 위젯 → 결제 완료 → impUid 수신
3. 클라이언트 → POST /api/payments/verify (impUid + orderId)
4. 서버 → 포트원 API GET /payments/{impUid} → 실제 금액 조회
5. 서버 → 금액 검증 (서버 주문금액 == 포트원 결제금액)
6. 검증 성공 → Order PENDING→PAID, Payment 저장
```

## 기술적 의사결정
| 결정 | 이유 |
|------|------|
| 비관적 락 | 재고 차감은 읽기-수정-쓰기 패턴 → 동시 요청 시 음수 재고 방지. 낙관적 락은 충돌 시 재시도 부담 |
| 주문 시점 가격 스냅샷 | 상품 가격이 바뀌어도 주문 당시 금액 보존 (OrderItem.unitPrice) |
| 포트원 서버 검증 | 클라이언트가 amount 조작 가능 → 반드시 서버에서 포트원에 금액 재확인 |
| 상태 머신 | 잘못된 상태 전환 (배송 중→결제) 방어적 차단 |
| Docker Compose | DB + App 컨테이너 의존성 관리, healthcheck로 DB 준비 후 App 시작 |

## Trouble Shooting
| 문제 | 원인 | 해결 |
|------|------|------|
| 재고 동시 차감 | 비관적 락 미적용 | @Lock(PESSIMISTIC_WRITE) 적용 |
| 결제 금액 불일치 | 클라이언트 조작 | 포트원 API 서버 검증 |
| Docker DB 연결 실패 | DB 준비 전 App 시작 | healthcheck + depends_on 설정 |
