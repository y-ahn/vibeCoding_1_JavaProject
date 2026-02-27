# PROJECT 2 — 중고마켓 거래 플랫폼

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-5.0.0-blue)
![JWT](https://img.shields.io/badge/Auth-JWT-black)
![H2](https://img.shields.io/badge/DB-H2_InMemory-lightgrey)

## 핵심 학습 포인트
- **N+1 문제** 직접 발생 → 3가지 방법으로 해결 (Fetch Join / @EntityGraph / @BatchSize)
- **QueryDSL** 동적 검색 쿼리 (null 조건 자동 제외)
- **JPA 연관관계** 설계 (LAZY 필수, 단방향 원칙)
- **MySQL 인덱스** 설계 + EXPLAIN 실행 계획 분석

## 기술 스택
- Spring Boot 3.2.5 / Java 21
- Spring Data JPA + QueryDSL 5.0.0
- Spring Security 6 + JWT
- H2 (개발) / MySQL 8.0 (운영)
- Gradle 8.7

## 실행 방법

```bash
# Gradle Wrapper 생성 (최초 1회)
gradle wrapper --gradle-version 8.7

# Q클래스 생성 (QueryDSL, 최초 1회)
./gradlew compileJava

# 실행
./gradlew bootRun
```

## 접속
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:marketdb`
  - User Name: `sa`

## API 명세
| Method | URL | 설명 | 인증 |
|--------|-----|------|:----:|
| POST | /api/auth/signup | 회원가입 | ✗ |
| POST | /api/auth/login | 로그인 | ✗ |
| POST | /api/products | 상품 등록 | ✓ |
| GET | /api/products | 상품 검색 (동적 쿼리) | ✗ |
| GET | /api/products/{id} | 상품 단건 조회 | ✗ |
| GET | /api/products/my | 내 상품 목록 | ✓ |
| PUT | /api/products/{id} | 상품 수정 | ✓ |
| DELETE | /api/products/{id} | 상품 삭제 | ✓ |
| PATCH | /api/products/{id}/status | 거래상태 변경 | ✓ |
| POST | /api/trades/{productId} | 구매 요청 | ✓ |
| PATCH | /api/trades/{tradeId}/complete | 거래 완료 확정 | ✓ |
| PATCH | /api/trades/{tradeId}/cancel | 거래 취소 | ✓ |
| GET | /api/trades/purchases | 구매 내역 | ✓ |
| GET | /api/trades/sales | 판매 내역 | ✓ |

## 패키지 구조
```
src/main/java/com/portfolio/market/
├── config/
│   ├── JpaConfig.java
│   ├── QueryDslConfig.java      # JPAQueryFactory Bean
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── domain/
│   ├── user/                    # 회원가입, 로그인
│   ├── product/                 # 상품 CRUD + QueryDSL 검색
│   └── trade/                   # 거래 요청/완료/취소
└── global/
    ├── exception/
    ├── jwt/
    └── response/
```

## 기술적 의사결정
| 결정 | 이유 |
|------|------|
| QueryDSL 동적 쿼리 | 검색 조건이 null이면 WHERE 절 자동 제외 — JPQL @Query로는 불가능 |
| @BatchSize(100) | 이미지 컬렉션 N+1을 IN 쿼리로 해결 — Fetch Join은 페이징 불가 |
| Fetch Join + countQuery 분리 | Pageable + Fetch Join 함께 쓸 때 카운트 쿼리 정확도 보장 |
| LAZY 전체 적용 | EAGER는 항상 JOIN 발생 — 불필요한 쿼리 방지 |
| 복합 인덱스 (status, created_at) | 상태 필터 + 최신순 정렬 쿼리 최적화 |

## Trouble Shooting
| 문제 | 원인 | 해결 |
|------|------|------|
| Q클래스 없음 오류 | compileJava 미실행 | ./gradlew compileJava 먼저 실행 |
| Fetch Join + Pageable 경고 | HHH90003004 경고 | countQuery 분리로 해결 |
| 순환 참조 | 양방향 매핑 toString() | @JsonIgnore 또는 단방향으로 변경 |
