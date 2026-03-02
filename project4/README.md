# PROJECT 4 — 소셜 미디어 피드 서비스

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-Sorted_Set-DC382D?logo=redis&logoColor=white)

## 핵심 학습 포인트
- **Cursor 기반 무한스크롤** — Offset 페이징 문제 해결
- **Redis Sorted Set** 인기 게시글 실시간 랭킹
- **Redis Set** 좋아요 원자적 연산 (동시성 문제 해결)
- **계층형 댓글** 자기 참조 + Soft Delete
- **팔로우/언팔로우** Fan-out 피드 전략

## 기술 스택
- Spring Boot 3.2.5 / Java 21
- Spring Data JPA + Spring Data Redis
- Spring Security 6 + JWT
- H2 (개발) / MySQL 8.0 (운영)
- Gradle 8.7

## 실행 방법
```bash
# Redis 실행 (Project 3에서 실행했다면 이미 실행 중)
docker run -d --name redis-feed -p 6379:6379 redis:7

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
| POST | /api/posts | 게시글 작성 | ✓ |
| GET | /api/posts/{id} | 게시글 단건 조회 | ✗ |
| GET | /api/posts/feed | 팔로우 피드 (Cursor) | ✓ |
| GET | /api/posts/my | 내 게시글 (Cursor) | ✓ |
| PUT | /api/posts/{id} | 게시글 수정 | ✓ |
| DELETE | /api/posts/{id} | 게시글 삭제 | ✓ |
| POST | /api/posts/{id}/like | 좋아요 | ✓ |
| DELETE | /api/posts/{id}/like | 좋아요 취소 | ✓ |
| POST | /api/posts/{postId}/comments | 댓글 작성 | ✓ |
| GET | /api/posts/{postId}/comments | 댓글 목록 | ✗ |
| DELETE | /api/comments/{id} | 댓글 삭제 | ✓ |
| POST | /api/users/{id}/follow | 팔로우 | ✓ |
| DELETE | /api/users/{id}/follow | 언팔로우 | ✓ |
| GET | /api/posts/ranking | 인기 랭킹 TOP 10 | ✗ |

## 기술적 의사결정
| 결정 | 이유 |
|------|------|
| Cursor 페이징 | Offset은 OFFSET N 만큼 스캔 후 버림 → 데이터 많을수록 느려짐. Cursor는 WHERE id < cursor 인덱스 직접 탐색 → 항상 O(log n) |
| Redis Sorted Set 랭킹 | ZINCRBY로 점수 원자적 증가, ZREVRANGE로 TOP N 즉시 조회 — DB 집계 쿼리 불필요 |
| Redis Set 좋아요 | SADD 원자적 연산 — 동시에 두 요청이 와도 중복 방지 |
| Soft Delete (댓글) | 대댓글이 있는 댓글 삭제 시 대댓글도 사라지는 문제 방지 |

## Trouble Shooting
| 문제 | 원인 | 해결 |
|------|------|------|
| Redis 연결 실패 | Docker Redis 미실행 | docker run -d -p 6379:6379 redis:7 |
| Cursor 첫 요청 | cursor 파라미터 없이 요청 | cursor=null이면 최신부터 조회 |
| 좋아요 중복 | Redis Set SADD 반환값 0 | ALREADY_LIKED 예외 발생 |
