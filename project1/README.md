# PROJECT 1 — 개인 일정 관리 API

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![H2](https://img.shields.io/badge/DB-H2_InMemory-blue)
![JWT](https://img.shields.io/badge/Auth-JWT_0.12.5-black)

## 빠른 시작
```bash
# 1. Gradle Wrapper 초기화 (최초 1회, Gradle 설치 필요)
gradle wrapper --gradle-version 8.7

# 2. 실행 (H2 인메모리 DB 자동 사용)
./gradlew bootRun

# 3. 접속
# Swagger UI : http://localhost:8080/swagger-ui.html
# H2 Console : http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:scheduledb)
```

## 테스트 실행
```bash
./gradlew test
```

## API 명세
| Method | URL | 설명 | 인증 |
|--------|-----|------|:----:|
| POST | /api/auth/signup | 회원가입 | ✗ |
| POST | /api/auth/login | 로그인 | ✗ |
| POST | /api/schedules | 일정 생성 | ✓ |
| GET | /api/schedules | 목록 조회 (페이징) | ✓ |
| GET | /api/schedules/{id} | 단건 조회 | ✓ |
| PUT | /api/schedules/{id} | 수정 | ✓ |
| DELETE | /api/schedules/{id} | 삭제 | ✓ |
| PATCH | /api/schedules/{id}/complete | 완료 토글 | ✓ |
| POST | /api/categories | 카테고리 생성 | ✓ |
| GET | /api/categories | 카테고리 목록 | ✓ |
| PUT | /api/categories/{id} | 카테고리 수정 | ✓ |
| DELETE | /api/categories/{id} | 카테고리 삭제 | ✓ |

## 환경변수 (선택)
```bash
export JWT_SECRET=my-super-secret-key-32-chars-minimum
```
설정하지 않으면 기본값(dev용)이 적용됩니다.

## 패키지 구조
```
src/main/java/com/portfolio/schedule/
├── config/           # JpaConfig, SecurityConfig
├── domain/
│   ├── user/         # entity, repository, service, controller, dto
│   ├── schedule/     # entity, repository, service, controller, dto
│   └── category/     # entity, repository, service, controller, dto
└── global/
    ├── exception/    # ErrorCode, CustomException, GlobalExceptionHandler
    ├── jwt/          # JwtProvider, JwtAuthenticationFilter
    └── response/     # ApiResponse, BaseTimeEntity
```

## 기술적 의사결정
| 결정 | 이유 |
|------|------|
| JWT (Stateless) | 수평 확장 시 세션 동기화 불필요 |
| OSIV 비활성화 | DB 커넥션 낭비 방지 |
| @Transactional(readOnly=true) | 더티 체킹 비활성화, 읽기 성능 향상 |
| @EnableJpaAuditing 분리 | @WebMvcTest 슬라이스 테스트 호환 |
| GlobalExceptionHandler | 예외처리 횡단 관심사 분리 |

## Trouble Shooting
> 개발하면서 겪은 문제를 여기에 기록하세요 (면접에서 중요!)
- 예) `@EnableJpaAuditing`을 Main에 두면 @WebMvcTest 실패 → JpaConfig 클래스로 분리
