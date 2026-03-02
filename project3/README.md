# PROJECT 3 — 실시간 채팅 서버

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-blue)
![Redis](https://img.shields.io/badge/Redis-Pub/Sub-DC382D?logo=redis&logoColor=white)

## 핵심 학습 포인트
- **WebSocket + STOMP** 실시간 양방향 통신
- **STOMP ChannelInterceptor** JWT 인증 (HTTP Filter가 아닌 STOMP 레벨)
- **Redis Pub/Sub** 멀티 서버 메시지 동기화
- **Redis List** 최근 메시지 50개 캐싱 (TTL 24시간)
- **Redis Set** 온라인 유저 관리 (중복 없는 집합)
- **SSE(Server-Sent Events)** 실시간 알림

## 기술 스택
- Spring Boot 3.2.5 / Java 21
- Spring WebSocket + STOMP
- Spring Data Redis
- Spring Security 6 + JWT
- H2 (개발) / MySQL 8.0 (운영)
- Gradle 8.7

## 실행 방법

### Redis 필요 (Docker 권장)
```bash
docker run -d --name redis-chat -p 6379:6379 redis:7
```

### 앱 실행
```bash
gradle wrapper --gradle-version 8.7
./gradlew bootRun
```

## 접속
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **H2 Console**: http://localhost:8080/h2-console

## API 명세
| Method | URL | 설명 | 인증 |
|--------|-----|------|:----:|
| POST | /api/auth/signup | 회원가입 | ✗ |
| POST | /api/auth/login | 로그인 | ✗ |
| POST | /api/chat/rooms | 채팅방 생성/조회 | ✓ |
| GET | /api/chat/rooms | 내 채팅방 목록 | ✓ |
| GET | /api/chat/rooms/{id}/messages | 메시지 기록 조회 | ✓ |
| GET | /api/notifications/subscribe | SSE 알림 구독 | ✓ |

## WebSocket 연결 (STOMP)
```javascript
// 1. 연결
const socket = new SockJS('http://localhost:8080/ws');
const client = Stomp.over(socket);
client.connect({'Authorization': 'Bearer [TOKEN]'}, () => {

  // 2. 채팅방 구독
  client.subscribe('/topic/chat/room/1', (msg) => {
    console.log(JSON.parse(msg.body));
  });

  // 3. 메시지 전송
  client.send('/app/chat.send', {}, JSON.stringify({
    roomId: 1,
    content: '안녕하세요!'
  }));
});
```

## 패키지 구조
```
src/main/java/com/portfolio/chat/
├── config/
│   ├── JpaConfig.java
│   ├── RedisConfig.java          # Redis Pub/Sub 설정
│   ├── WebSocketConfig.java      # STOMP 엔드포인트 + 인터셉터
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── domain/
│   ├── user/                     # 회원가입, 로그인
│   ├── chat/                     # 채팅방, 메시지, Redis Pub/Sub
│   └── notification/             # SSE 알림, 온라인 유저
└── global/
    ├── exception/
    ├── jwt/
    │   ├── JwtProvider.java
    │   ├── JwtAuthenticationFilter.java
    │   ├── StompPrincipal.java       # STOMP 세션 사용자 정보
    │   └── StompAuthChannelInterceptor.java  # STOMP JWT 인증
    └── response/
```

## 기술적 의사결정
| 결정 | 이유 |
|------|------|
| STOMP 레벨 JWT 인증 | WebSocket은 최초 Upgrade 이후 HTTP 헤더 사용 불가 → STOMP CONNECT 프레임에서 인증 |
| Redis Pub/Sub | 서버 2대일 때 A서버 유저↔B서버 유저 메시지 동기화 |
| Redis List 캐시 | DB 조회 없이 최근 50개 메시지 빠르게 로드 |
| Redis Set 온라인 유저 | 중복 없는 집합 — 같은 유저 여러 탭 접속 시에도 정확한 카운트 |
| SSE (단방향) | 알림은 서버→클라이언트 단방향만 필요 — WebSocket보다 구현 단순 |

## Trouble Shooting
| 문제 | 원인 | 해결 |
|------|------|------|
| Redis 연결 실패 | Docker Redis 미실행 | docker run -d -p 6379:6379 redis:7 |
| STOMP 인증 실패 | Authorization 헤더 누락 | CONNECT 프레임에 헤더 포함 |
| SseEmitter 메모리 누수 | onCompletion/onTimeout/onError 미등록 | 3개 콜백 모두 등록 |
