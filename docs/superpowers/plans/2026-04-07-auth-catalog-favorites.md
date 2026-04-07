# Auth, Catalog, Favorites Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 구글 OAuth 기반 로그인과 자체 access/refresh 토큰 발급, refresh rotation, 인증 필수 식물 도감 cursor 조회, 사용자별 즐겨찾기를 구현한다.

**Architecture:** `auth`, `security`, `catalog`, `favorite`를 분리하고, 외부 구글 OAuth 연동과 JWT 발급/검증은 어댑터로 숨긴다. 인증 성공 후에는 모든 보호 API가 자체 access token만 사용하고, refresh token은 DB 해시 저장 + rotation 정책을 따른다.

**Tech Stack:** Spring Boot, Spring Security, Spring MVC, Spring Data JPA, Flyway, MySQL/H2, JWT

---

## Chunk 1: Security Foundation

### Task 1: 보안 의존성 및 기본 설정 추가

**Files:**
- Modify: `build.gradle`
- Modify: `src/main/resources/application.yml`
- Test: `src/test/java/bssm/plantshuman/peopleandgreen/infrastructure/config/ApplicationConfigurationTest.java`

- [ ] **Step 1: 설정 테스트 작성**
- [ ] **Step 2: 테스트 실행 후 실패 확인**
- [ ] **Step 3: Spring Security/JWT/OAuth 관련 의존성 및 설정 추가**
- [ ] **Step 4: 테스트 재실행**
- [ ] **Step 5: 커밋**

### Task 2: JWT 인증 인프라 구현

**Files:**
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/domain/model/AuthTokens.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/application/port/out/IssueJwtPort.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/out/security/JwtTokenProvider.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/out/security/JwtAuthenticationFilter.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/out/security/SecurityConfiguration.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/out/security/AuthenticatedUser.java`
- Test: `src/test/java/bssm/plantshuman/peopleandgreen/auth/adapter/out/security/JwtTokenProviderTest.java`

- [ ] **Step 1: JWT 발급/검증 테스트 작성**
- [ ] **Step 2: 테스트 실행 후 실패 확인**
- [ ] **Step 3: 최소 JWT 발급/파싱 코드 작성**
- [ ] **Step 4: 보안 필터와 SecurityFilterChain 작성**
- [ ] **Step 5: 테스트 재실행**
- [ ] **Step 6: 커밋**

## Chunk 2: Auth Domain

### Task 3: 사용자/리프레시토큰 스키마와 엔티티 추가

**Files:**
- Create: `src/main/resources/db/migration/V2__create_auth_tables.sql`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/out/persistence/entity/UserEntity.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/out/persistence/entity/RefreshTokenEntity.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/out/persistence/repository/UserRepository.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/out/persistence/repository/RefreshTokenRepository.java`
- Test: `src/test/java/bssm/plantshuman/peopleandgreen/auth/adapter/out/persistence/AuthPersistenceIntegrationTest.java`

- [ ] **Step 1: persistence 테스트 작성**
- [ ] **Step 2: 테스트 실행 후 실패 확인**
- [ ] **Step 3: migration/엔티티/리포지토리 작성**
- [ ] **Step 4: 테스트 재실행**
- [ ] **Step 5: 커밋**

### Task 4: 구글 로그인 유스케이스 구현

**Files:**
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/domain/model/OAuthProvider.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/domain/model/AppUser.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/domain/model/GoogleUserInfo.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/application/port/in/LoginWithGoogleUseCase.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/application/port/out/ExchangeGoogleAuthCodePort.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/application/service/LoginWithGoogleService.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/out/google/GoogleOAuthClient.java`
- Test: `src/test/java/bssm/plantshuman/peopleandgreen/auth/application/service/LoginWithGoogleServiceTest.java`

- [ ] **Step 1: 로그인 유스케이스 테스트 작성**
- [ ] **Step 2: 테스트 실행 후 실패 확인**
- [ ] **Step 3: 사용자 upsert + 토큰 발급 최소 구현**
- [ ] **Step 4: 구글 교환 포트 및 검증 구조 추가**
- [ ] **Step 5: 테스트 재실행**
- [ ] **Step 6: 커밋**

### Task 5: refresh rotation 유스케이스 구현

**Files:**
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/application/port/in/RefreshAccessTokenUseCase.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/application/service/RefreshAccessTokenService.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/out/security/RefreshTokenHasher.java`
- Test: `src/test/java/bssm/plantshuman/peopleandgreen/auth/application/service/RefreshAccessTokenServiceTest.java`

- [ ] **Step 1: refresh rotation 테스트 작성**
- [ ] **Step 2: 테스트 실행 후 실패 확인**
- [ ] **Step 3: refresh 조회/검증/재발급/폐기 구현**
- [ ] **Step 4: 테스트 재실행**
- [ ] **Step 5: 커밋**

## Chunk 3: Auth API

### Task 6: 로그인/토큰 갱신 API 구현

**Files:**
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/in/web/AuthController.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/in/web/dto/request/GoogleLoginRequest.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/in/web/dto/request/RefreshTokenRequest.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/in/web/dto/response/AuthTokenResponse.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/auth/adapter/in/web/AuthExceptionHandler.java`
- Test: `src/test/java/bssm/plantshuman/peopleandgreen/auth/adapter/in/web/AuthControllerTest.java`

- [ ] **Step 1: 컨트롤러 테스트 작성**
- [ ] **Step 2: 테스트 실행 후 실패 확인**
- [ ] **Step 3: DTO/컨트롤러/예외 처리 구현**
- [ ] **Step 4: 테스트 재실행**
- [ ] **Step 5: 커밋**

## Chunk 4: Catalog And Favorites

### Task 7: 즐겨찾기 스키마와 엔티티 추가

**Files:**
- Create: `src/main/resources/db/migration/V3__create_favorite_plant_table.sql`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/adapter/out/persistence/entity/FavoritePlantEntity.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/adapter/out/persistence/repository/FavoritePlantRepository.java`
- Test: `src/test/java/bssm/plantshuman/peopleandgreen/catalog/adapter/out/persistence/FavoritePlantRepositoryTest.java`

- [ ] **Step 1: favorite persistence 테스트 작성**
- [ ] **Step 2: 테스트 실행 후 실패 확인**
- [ ] **Step 3: migration/엔티티/리포지토리 구현**
- [ ] **Step 4: 테스트 재실행**
- [ ] **Step 5: 커밋**

### Task 8: 식물 도감 cursor 조회 유스케이스 구현

**Files:**
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/application/port/in/GetPlantCatalogUseCase.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/application/service/GetPlantCatalogService.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/application/port/out/LoadPlantCatalogPagePort.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/adapter/out/persistence/PlantCatalogPersistenceAdapter.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/domain/model/PlantCatalogCursorPage.java`
- Test: `src/test/java/bssm/plantshuman/peopleandgreen/catalog/application/service/GetPlantCatalogServiceTest.java`

- [ ] **Step 1: cursor 목록 조회 테스트 작성**
- [ ] **Step 2: 테스트 실행 후 실패 확인**
- [ ] **Step 3: plant 기준 목록 조회 + favorite join 최소 구현**
- [ ] **Step 4: 테스트 재실행**
- [ ] **Step 5: 커밋**

### Task 9: 즐겨찾기 추가/삭제 유스케이스 구현

**Files:**
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/application/port/in/AddFavoritePlantUseCase.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/application/port/in/RemoveFavoritePlantUseCase.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/application/service/FavoritePlantService.java`
- Test: `src/test/java/bssm/plantshuman/peopleandgreen/catalog/application/service/FavoritePlantServiceTest.java`

- [ ] **Step 1: favorite 유스케이스 테스트 작성**
- [ ] **Step 2: 테스트 실행 후 실패 확인**
- [ ] **Step 3: add/remove 최소 구현**
- [ ] **Step 4: 테스트 재실행**
- [ ] **Step 5: 커밋**

### Task 10: 식물 도감/즐겨찾기 API 구현

**Files:**
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/adapter/in/web/PlantCatalogController.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/adapter/in/web/dto/response/PlantCatalogItemResponse.java`
- Create: `src/main/java/bssm/plantshuman/peopleandgreen/catalog/adapter/in/web/dto/response/PlantCatalogPageResponse.java`
- Test: `src/test/java/bssm/plantshuman/peopleandgreen/catalog/adapter/in/web/PlantCatalogControllerTest.java`

- [ ] **Step 1: 컨트롤러 테스트 작성**
- [ ] **Step 2: 테스트 실행 후 실패 확인**
- [ ] **Step 3: 목록/즐겨찾기 API 구현**
- [ ] **Step 4: 테스트 재실행**
- [ ] **Step 5: 커밋**

## Chunk 5: End-To-End Verification

### Task 11: 인증 + 도감 통합 검증

**Files:**
- Create: `src/test/java/bssm/plantshuman/peopleandgreen/auth/AuthCatalogIntegrationTest.java`

- [ ] **Step 1: 로그인 후 인증된 도감 조회 시나리오 테스트 작성**
- [ ] **Step 2: 테스트 실행 후 실패 확인**
- [ ] **Step 3: 누락된 wiring/설정 최소 보완**
- [ ] **Step 4: 전체 테스트 실행**
- [ ] **Step 5: 커밋**
