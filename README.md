# People & Green Backend

People & Green의 백엔드 서버입니다.  
Google OAuth 기반 인증, 식물 도감 조회, 사용자 환경 기반 식물 추천 API를 제공합니다.

이 프로젝트는 단순 기능 구현보다 다음에 초점을 맞춥니다.

- 인증, 추천, 도감 기능을 모듈 단위로 분리한 서버 구조
- 운영 환경에 맞게 조정 가능한 설정 구성
- 테스트 가능한 서비스 경계와 API 계층
- 문서화와 검증이 가능한 Spring Boot 기반 백엔드

## Features

### Authentication

- Google OAuth 인가 코드 로그인
- JWT Access Token 발급
- Refresh Token 재발급
- 로그아웃 시 Refresh Token 무효화

### Plant Catalog

- 커서 기반 식물 도감 조회
- 사용자별 즐겨찾기 여부 포함
- 즐겨찾기 추가 / 삭제

### Plant Recommendation

- 사용자 환경 정보 기반 식물 추천
- 추천 점수, 추천 사유, 주의사항 제공
- 대표 환경 태그 및 보조 환경 태그 제공

## Project Structure

```text
src/main/java/bssm/plantshuman/peopleandgreen
├── auth
├── catalog
├── recommendation
├── shared
├── infrastructure
├── domain
├── application
└── presentation
```

### Module Overview

- `auth`
  인증, JWT, OAuth 연동, 토큰 저장과 보안 처리
- `catalog`
  식물 도감 조회와 즐겨찾기 API
- `recommendation`
  추천 정책, 추천 엔진, 추천 응답 구성
- `shared`
  공통 예외, 응답 모델, 공통 유틸리티
- `infrastructure`
  OpenAPI, Jackson, RestClient, persistence 설정

## Architecture

프로젝트는 기능별 모듈 분리를 기본으로 하며, 일부 영역은 포트/어댑터 구조를 사용합니다.

- `auth`
  외부 OAuth, JWT, 토큰 저장소처럼 보안 경계와 외부 의존성이 뚜렷해 포트/어댑터 분리를 유지합니다.
- `recommendation`
  추천 규칙과 데이터 소스를 분리해 도메인 로직과 조회 소스를 독립적으로 다룰 수 있게 구성했습니다.
- `catalog`
  조회와 사용자 액션 중심의 비교적 단순한 흐름으로 구성했습니다.

이 구조를 통해 외부 연동, 도메인 로직, 웹 계층의 책임을 분리하고 테스트와 변경 범위를 통제할 수 있도록 했습니다.

## Key Technical Points

### Secure Auth Flow

- Access Token과 Refresh Token을 분리합니다.
- Refresh Token은 해시 후 저장합니다.
- 재발급 시 토큰 rotation을 적용합니다.
- 로그아웃 시 저장된 토큰을 기준으로 무효화합니다.

### Configurable Recommendation Policy

- 추천 점수와 추천 개수 제한을 설정으로 관리합니다.
- 운영 환경에 따라 코드 수정 없이 정책 값을 변경할 수 있습니다.

### Environment-Driven Configuration

- JWT 설정, Google OAuth 설정, HTTPS 여부, CORS 허용 Origin을 외부 설정으로 분리했습니다.
- `@ConfigurationProperties`를 사용해 설정을 구조적으로 관리합니다.

### API and Test Coverage

- Swagger/OpenAPI 문서를 제공합니다.
- 컨트롤러 테스트, 서비스 테스트, 통합 테스트를 포함합니다.

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Flyway
- MySQL
- H2
- JWT (`jjwt`)
- springdoc OpenAPI
- JUnit 5

## Getting Started

### 1. Environment Variables

`.env` 또는 환경변수로 아래 값을 준비합니다.

```env
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
AUTH_JWT_SECRET=
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
GOOGLE_REDIRECT_URI=
REQUIRE_HTTPS=true
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

### 2. Run Application

```bash
./gradlew bootRun
```

### 3. Run Tests

```bash
./gradlew test
```

### 4. Swagger UI

- `http://localhost:8080/swagger-ui.html`

## Testing

현재 포함된 검증 범위는 다음과 같습니다.

- 인증 컨트롤러 테스트
- 추천 서비스 테스트
- Swagger 노출 테스트
- 애플리케이션 설정 테스트
- persistence 통합 테스트

## Future Improvements

- Redis 기반 세션/토큰 저장소 도입
- 추천 결과 캐싱 및 성능 최적화
- CI 파이프라인 강화
- 운영 메트릭과 모니터링 확장
- Docker 기반 로컬 개발 환경 표준화
