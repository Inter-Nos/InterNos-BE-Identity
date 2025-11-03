# InterNos Backend - Service A: Identity & Portal

InterNos 프로젝트의 Service A - Identity & Portal 마이크로서비스입니다.

## 프로젝트 개요

이 프로젝트는 GCP, Kubernetes, GitHub Actions (CI), ArgoCD 연습을 위한 MSA 아키텍처 프로젝트입니다.

Service A는 사용자 인증, 세션 관리, CSRF 보안, 대시보드 데이터 제공을 담당합니다.

## 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** - 인증/인가
- **Spring Data JPA** - 데이터베이스 접근
- **PostgreSQL 15** - 데이터베이스
- **Flyway** - 데이터베이스 마이그레이션
- **Gradle** - 빌드 도구
- **Docker** - 컨테이너화
- **OpenAPI 3.1** (springdoc) - API 문서

## 프로젝트 구조

```
backend/
├── common/                    # 공통 모듈 (ErrorResponse, GlobalExceptionHandler)
├── service-a-identity/        # Service A - Identity & Portal
│   ├── src/main/java/
│   │   └── app/internos/servicea/
│   │       ├── config/        # 설정 (Security, OpenAPI)
│   │       ├── controller/    # REST 컨트롤러
│   │       ├── domain/        # 엔티티 및 리포지토리
│   │       ├── dto/           # 요청/응답 DTO
│   │       ├── security/      # 보안 필터 (CSRF)
│   │       ├── service/       # 비즈니스 로직
│   │       └── util/          # 유틸리티
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-prod.yml
│       └── db/migration/      # Flyway 마이그레이션
├── docker/                    # Docker Compose 설정
└── infra/                     # Kubernetes 매니페스트 (향후)

```

## 주요 기능

### 인증 API (`/a/v1/auth/*`)
- `POST /auth/register` - 사용자 가입
- `POST /auth/login` - 로그인
- `POST /auth/logout` - 로그아웃 (CSRF 보호)
- `GET /auth/session` - 세션 정보 및 CSRF 토큰 조회

### 대시보드 API (`/a/v1/me/*`)
- `GET /me/dashboard` - 사용자 대시보드 데이터 (프로필 방문, 방 통계)

### 트래킹 API (`/a/v1/track/*`)
- `POST /track/visit/user` - 프로필 방문 추적 (비동기)

### 헬스 체크 (`/health/*`)
- `GET /health/liveness` - 생존 확인 (K8s Liveness Probe)
- `GET /health/readiness` - 준비 확인 (K8s Readiness Probe)

## 환경 변수

### 필수 환경 변수

```bash
# 데이터베이스
DB_URL_A=jdbc:postgresql://localhost:5432/identity_dev
DB_USER=postgres
DB_PASS=postgres

# 보안
SESSION_SECRET=your-session-secret-key
CSRF_SECRET=your-csrf-secret-key
IP_HASH_PEPPER=your-ip-hash-pepper

# 프로파일
SPRING_PROFILES_ACTIVE=dev
```

### 선택적 환경 변수

```bash
SERVER_PORT=8080
```

## 빌드 및 실행

### Prerequisites

- Java 17 이상
- Docker & Docker Compose

### Gradle 빌드

```bash
cd backend
./gradlew clean build
```

### Docker 이미지 빌드

```bash
cd backend/service-a-identity
docker build -t internos-service-a:latest .
```

### Docker로 애플리케이션 실행

```bash
docker run -p 8080:8080 \
  -e DB_URL_A=jdbc:postgresql://<host>:5432/identity_dev \
  -e DB_USER=postgres \
  -e DB_PASS=postgres \
  -e IP_HASH_PEPPER=your-pepper \
  -e SESSION_SECRET=your-secret \
  -e CSRF_SECRET=your-csrf-secret \
  -e SPRING_PROFILES_ACTIVE=dev \
  internos-service-a:latest
```

## API 문서

애플리케이션 실행 후 Swagger UI에 접근:

- **로컬**: http://localhost:8080/a/v1/swagger-ui.html
- **프로덕션**: https://api.internos.app/a/v1/swagger-ui.html

OpenAPI 스펙:

- **로컬**: http://localhost:8080/a/v1/v3/api-docs
- **프로덕션**: https://api.internos.app/a/v1/v3/api-docs

## 데이터베이스 마이그레이션

Flyway가 자동으로 마이그레이션을 실행합니다. 수동 실행:

```bash
cd backend/service-a-identity
./gradlew flywayMigrate
```

### 마이그레이션 파일

- `V1__create_extension_citext.sql` - citext 확장 생성
- `V2__create_app_user_table.sql` - 사용자 테이블
- `V3__create_user_session_table.sql` - 세션 테이블
- `V4__create_visit_log_user_table.sql` - 방문 로그 테이블

## 보안

### CSRF 보호

Double Submit Cookie 패턴을 사용합니다:

1. `GET /auth/session`으로 CSRF 토큰 조회
2. 응답에 `XSRF-TOKEN` 쿠키와 `csrfToken` JSON 필드 포함
3. 상태 변경 요청 (POST, PUT, PATCH, DELETE) 시 `X-CSRF-Token` 헤더 필수

### 세션 관리

- Spring Security 기본 세션 관리
- HTTP-only 쿠키 (`SESSION`)
- 24시간 유효기간

### 비밀번호 해싱

- BCrypt (12 rounds) 또는 Argon2id 권장
- 평문 저장 금지

## 테스트

### 단위 테스트 실행

```bash
cd backend
./gradlew test
```

### 통합 테스트 실행 (Testcontainers)

```bash
cd backend/service-a-identity
./gradlew integrationTest
```

## 배포

### Docker 이미지 태깅

```bash
docker tag internos-service-a:latest gcr.io/PROJECT_ID/internos-service-a:TAG
docker push gcr.io/PROJECT_ID/internos-service-a:TAG
```

### Kubernetes 배포

Kubernetes 매니페스트는 `backend/infra/` 디렉토리에 있습니다 (향후 추가 예정).

## 개발 가이드

### 커밋 규칙

커밋 메시지는 다음 형식을 따릅니다:

```
<type>: <description>

예시:
feat: add user registration endpoint
fix: resolve session timeout issue
docs: update API documentation
refactor: improve authentication service
```

타입:
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `docs`: 문서 변경
- `refactor`: 내부 구조 개선
- `test`: 테스트 추가/수정
- `build`: 빌드 시스템/의존성 변경
- `ci`: CI 설정/스크립트 변경
- `chore`: 기타 잡무

### 코드 스타일

- Clean Code 원칙 준수
- DTO 패턴 사용 (Entity 직접 노출 금지)
- 예외는 표준 ErrorResponse 형식 사용
- 트랜잭션은 `@Transactional` 어노테이션 사용

## 문제 해결

### 데이터베이스 연결 실패

1. PostgreSQL이 실행 중인지 확인
2. `DB_URL_A`, `DB_USER`, `DB_PASS` 환경 변수 확인
3. 포트 5432가 열려있는지 확인

### CSRF 토큰 오류

1. `GET /auth/session`으로 토큰을 먼저 조회했는지 확인
2. `X-CSRF-Token` 헤더가 올바르게 설정되었는지 확인
3. `XSRF-TOKEN` 쿠키가 존재하는지 확인

## 라이센스

이 프로젝트는 연습용 프로젝트입니다.

## 참고 문서

- [API 명세서](./tasking/api-spec.md)
- [백엔드 지침서](./tasking/backend-spec.md)
- [ERD](./tasking/erd.md)
- [기능 명세서](./tasking/feature-spec.md)
- [인프라 아키텍처](./tasking/infra.md)
