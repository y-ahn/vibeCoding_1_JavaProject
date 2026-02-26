# 🚀 VIBE CODING Vol.1 — Spring Boot 포트폴리오

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.7-02303A?logo=gradle&logoColor=white)
![JWT](https://img.shields.io/badge/Auth-JWT-black)
![Redis](https://img.shields.io/badge/Cache-Redis-DC382D?logo=redis&logoColor=white)
![MySQL](https://img.shields.io/badge/DB-MySQL-4479A1?logo=mysql&logoColor=white)

> **Claude(AI)와 함께 만든 Spring Boot 실전 포트폴리오 5종**  
> 신입 개발자가 면접에서 "직접 만들어봤다"고 말할 수 있는 프로젝트들

---

## 📁 프로젝트 구성

| 폴더 | 프로젝트 | 핵심 기술 | 난이도 |
|------|---------|-----------|:------:|
| [project1/](./project1) | 개인 일정 관리 API | JWT, Spring Security, JPA | ⭐⭐ |
| [project2/](./project2) | 중고마켓 거래 플랫폼 | N+1 해결, QueryDSL, 파일 업로드 | ⭐⭐⭐ |
| [project3/](./project3) | 실시간 채팅 서버 | WebSocket, STOMP, Redis Pub/Sub | ⭐⭐⭐⭐ |
| [project4/](./project4) | 소셜 미디어 피드 | Cursor 페이징, Redis Sorted Set, 동시성 | ⭐⭐⭐⭐ |
| [project5/](./project5) | 결제 연동 쇼핑몰 | 포트원 결제, 비관적 락, Docker/CD | ⭐⭐⭐⭐⭐ |

---

## 🛠 공통 기술 스택

```
Language     : Java 21
Framework    : Spring Boot 3.2.5
Build        : Gradle 8.7
ORM          : Spring Data JPA + Hibernate 6
Security     : Spring Security 6 + JWT (jjwt 0.12.5)
Database     : MySQL 8.0 (운영) / H2 InMemory (개발)
Cache        : Redis 7
Docs         : SpringDoc OpenAPI 3 (Swagger UI)
Container    : Docker + Docker Compose
CI/CD        : GitHub Actions
```

---

## 📌 PROJECT 1 — 개인 일정 관리 API

**"JWT를 직접 구현해봤습니다"** — 신입 개발자의 첫 번째 무기

### 핵심 구현
- JWT 발급/검증 (`JwtProvider`) + `OncePerRequestFilter` 인증 필터
- BCrypt 비밀번호 암호화
- `@RestControllerAdvice` 전역 예외 처리
- `@EnableJpaAuditing` 분리 (`JpaConfig`) — `@WebMvcTest` 호환
- `open-in-view: false` OSIV 비활성화

### 면접 포인트
> "JWT는 Stateless라서 서버를 여러 대로 늘려도 세션 동기화가 필요 없습니다.  
> Access Token은 30분, Refresh Token은 7일로 설정했고,  
> `OncePerRequestFilter`로 모든 요청에서 토큰을 검증합니다."

---

## 📌 PROJECT 2 — 중고마켓 거래 플랫폼

**"N+1 문제를 직접 발견하고 해결해봤습니다"**

### 핵심 구현
- N+1 해결 3가지: Fetch Join / `@EntityGraph` / `@BatchSize`
- QueryDSL 동적 검색 (키워드, 카테고리, 가격 범위, 상태 필터)
- MySQL 인덱스 설계 + `EXPLAIN` 실행 계획 분석
- Strategy Pattern 파일 업로드 (Local / S3 전환)

### 면접 포인트
> "상품 목록 조회 시 판매자 정보를 LAZY로 설정하면 N+1이 발생합니다.  
> Fetch Join으로 한 번의 쿼리로 해결했고,  
> 이미지 컬렉션은 @BatchSize(100)으로 IN 쿼리를 사용했습니다."

---

## 📌 PROJECT 3 — 실시간 채팅 서버

**"멀티 서버 환경에서도 채팅이 동작하도록 설계했습니다"**

### 핵심 구현
- WebSocket + STOMP 프로토콜
- STOMP `ChannelInterceptor`로 JWT 인증 (HTTP Filter가 아닌 STOMP 레벨)
- Redis Pub/Sub으로 멀티 서버 메시지 동기화
- Redis List로 최근 메시지 50개 캐싱 (TTL 24시간)
- SSE(Server-Sent Events) 알림 시스템

### 면접 포인트
> "서버가 2대일 때 A서버 유저와 B서버 유저가 채팅하면 메시지가 전달이 안 됩니다.  
> Redis Pub/Sub으로 모든 서버 인스턴스에 메시지를 브로드캐스트해서 해결했습니다."

---

## 📌 PROJECT 4 — 소셜 미디어 피드 서비스

**"대용량 트래픽을 고려한 피드 설계를 해봤습니다"**

### 핵심 구현
- Fan-out on Write vs Read 전략 비교 및 하이브리드 적용
- Cursor 기반 무한스크롤 (Offset 페이징의 성능 문제 해결)
- Redis 원자적 연산으로 좋아요 동시성 처리 (`opsForSet` + `INCR`)
- Redis Sorted Set 인기 게시글 랭킹 (좋아요 +2점, 댓글 +3점)
- 계층형 댓글 (자기 참조 엔티티)

### 면접 포인트
> "Offset 페이징은 뒤로 갈수록 OFFSET만큼 행을 스캔 후 버려서 느려집니다.  
> Cursor 방식은 WHERE id < cursor로 인덱스를 직접 탐색하므로 항상 빠릅니다."

---

## 📌 PROJECT 5 — 결제 연동 쇼핑몰 API

**"결제 API를 직접 연동해봤습니다"** — 신입에게서 듣기 가장 어려운 말

### 핵심 구현
- 포트원(아임포트) 결제 검증 (서버에서 금액 재검증, 위변조 방지)
- Webhook 서명 검증 (HMAC-SHA256)
- 비관적 락(`SELECT FOR UPDATE`)으로 재고 동시성 처리
- `@Async` + `ThreadPoolTaskExecutor` 비동기 이메일 발송
- Docker 멀티 스테이지 빌드 + GitHub Actions CD

### 면접 포인트
> "결제 금액을 클라이언트에서 받으면 안 됩니다.  
> imp_uid로 포트원 서버에 직접 조회해서 실제 결제 금액을 검증합니다.  
> 금액 불일치 시 자동으로 결제 취소 API를 호출합니다."

---

## 🚀 빠른 시작

### 사전 준비
- JDK 21+
- Gradle 8.7+
- Docker Desktop (PROJECT 3~5)

### PROJECT 1 실행 (H2 InMemory — 설치 불필요)
```bash
cd project1
gradle wrapper --gradle-version 8.7
./gradlew bootRun

# 접속
# Swagger UI : http://localhost:8080/swagger-ui/index.html
# H2 Console : http://localhost:8080/h2-console
```

### PROJECT 2~5 실행 (MySQL + Redis 필요)
```bash
cd project2  # 또는 project3, project4, project5

# 인프라 실행 (Docker 필요)
docker compose up -d

# 앱 실행
./gradlew bootRun
```

---

## 📚 함께 보면 좋은 자료

- 📖 **VIBE CODING Vol.1** 본문 — 각 프로젝트의 설계 이유와 면접 Q&A
- 💬 **부록 C** — Claude에게 바로 쓰는 프롬프트 50선
- 🛠 **부록 D** — GitHub 업로드 & 빌드 가이드

---

## 📝 커밋 컨벤션

```
feat:     새로운 기능 추가
fix:      버그 수정
refactor: 코드 리팩토링
test:     테스트 코드
docs:     문서 수정
chore:    빌드/설정 변경
```

---

<div align="center">

**VIBE CODING Vol.1**  
Claude와 함께, 직접 만들고, 면접에서 설명할 수 있는 포트폴리오

</div>
