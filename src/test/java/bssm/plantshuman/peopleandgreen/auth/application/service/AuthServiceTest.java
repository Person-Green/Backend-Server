package bssm.plantshuman.peopleandgreen.auth.application.service;

import bssm.plantshuman.peopleandgreen.auth.application.config.GoogleOAuthProperties;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.ExchangeGoogleAuthCodePort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.IssueJwtPort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.RefreshTokenStorePort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.UserAccountPort;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import bssm.plantshuman.peopleandgreen.auth.domain.model.GoogleUserInfo;
import bssm.plantshuman.peopleandgreen.auth.domain.model.OAuthProvider;
import bssm.plantshuman.peopleandgreen.auth.domain.model.PreparedGoogleAuthorization;
import bssm.plantshuman.peopleandgreen.auth.domain.model.StoredRefreshToken;
import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.RefreshTokenHasher;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceTest {

    private static final GoogleOAuthProperties GOOGLE_PROPERTIES = new GoogleOAuthProperties(
            "google-client-id",
            "google-client-secret",
            "https://oauth2.googleapis.com/token",
            "https://accounts.google.com/o/oauth2/v2/auth",
            List.of("openid", "email", "profile"),
            List.of("http://localhost:3000/auth/google/callback")
    );

    @Test
    void preparesGoogleAuthorizationUrlWithServerIssuedState() {
        PrepareGoogleAuthorizationService service = new PrepareGoogleAuthorizationService(
                GOOGLE_PROPERTIES,
                new StubIssueJwtPort()
        );

        PreparedGoogleAuthorization authorization = service.prepare("http://localhost:3000/auth/google/callback");

        assertEquals("STATE_TOKEN", authorization.state());
        assertEquals(true, authorization.authorizationUrl().contains("state=STATE_TOKEN"));
    }

    @Test
    void logsInByValidatingStateAndUpsertingUser() {
        RecordingRefreshTokenStorePort refreshTokenStorePort = new RecordingRefreshTokenStorePort();
        LoginWithGoogleService service = new LoginWithGoogleService(
                GOOGLE_PROPERTIES,
                (code, redirectUri) -> new GoogleUserInfo("google-123", "jjm@example.com", "jjm", "https://image"),
                new StubUserAccountPort(),
                new StubIssueJwtPort(),
                refreshTokenStorePort,
                new RefreshTokenHasher()
        );

        var tokens = service.login("AUTH_CODE", "STATE_TOKEN", "http://localhost:3000/auth/google/callback");

        assertEquals("ACCESS_TOKEN", tokens.accessToken());
        assertEquals("REFRESH_TOKEN", tokens.refreshToken());
        assertEquals(900, tokens.expiresIn());
        assertEquals(1L, tokens.user().id());
        assertEquals(1L, refreshTokenStorePort.savedUserId);
    }

    @Test
    void rejectsLoginWhenRedirectUriIsNotAllowed() {
        LoginWithGoogleService service = new LoginWithGoogleService(
                GOOGLE_PROPERTIES,
                (code, redirectUri) -> new GoogleUserInfo("google-123", "jjm@example.com", "jjm", "https://image"),
                new StubUserAccountPort(),
                new StubIssueJwtPort(),
                new RecordingRefreshTokenStorePort(),
                new RefreshTokenHasher()
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.login("AUTH_CODE", "STATE_TOKEN", "http://localhost:3000/other"));
    }

    @Test
    void rotatesRefreshTokenWhenRefreshingAccessToken() {
        RefreshAccessTokenService service = new RefreshAccessTokenService(
                new StubIssueJwtPort(),
                new StubUserAccountPort(),
                new ReadyRefreshTokenStorePort(new RefreshTokenHasher().hash("REFRESH_TOKEN")),
                new RefreshTokenHasher()
        );

        var tokens = service.refresh("REFRESH_TOKEN");

        assertEquals("ACCESS_TOKEN", tokens.accessToken());
        assertEquals("REFRESH_TOKEN", tokens.refreshToken());
        assertEquals(1L, tokens.user().id());
    }

    private static final class StubIssueJwtPort implements IssueJwtPort {

        @Override
        public String issueAccessToken(Long userId) {
            return "ACCESS_TOKEN";
        }

        @Override
        public String issueRefreshToken(Long userId) {
            return "REFRESH_TOKEN";
        }

        @Override
        public long getAccessTokenValiditySeconds() {
            return 900;
        }

        @Override
        public long getRefreshTokenValiditySeconds() {
            return 1209600;
        }

        @Override
        public String issueGoogleState(String redirectUri) {
            return "STATE_TOKEN";
        }

        @Override
        public void validateGoogleState(String token, String redirectUri) {
            if (!"STATE_TOKEN".equals(token)) {
                throw new IllegalArgumentException("invalid state");
            }
        }

        @Override
        public Long parseUserId(String token) {
            return 1L;
        }

        @Override
        public boolean isRefreshToken(String token) {
            return true;
        }
    }

    private static final class StubUserAccountPort implements UserAccountPort {

        @Override
        public AppUser upsertGoogleUser(GoogleUserInfo userInfo) {
            return new AppUser(1L, OAuthProvider.GOOGLE, userInfo.providerUserId(), userInfo.email(), userInfo.name(), userInfo.profileImageUrl());
        }

        @Override
        public Optional<AppUser> findById(Long userId) {
            return Optional.of(new AppUser(userId, OAuthProvider.GOOGLE, "google-123", "jjm@example.com", "jjm", "https://image"));
        }
    }

    private static final class RecordingRefreshTokenStorePort implements RefreshTokenStorePort {

        private Long savedUserId;

        @Override
        public void save(Long userId, String tokenHash, Instant expiresAt) {
            this.savedUserId = userId;
        }

        @Override
        public Optional<StoredRefreshToken> findByTokenHash(String tokenHash) {
            return Optional.empty();
        }

        @Override
        public void revoke(Long tokenId, Instant revokedAt) {
        }
    }

    private static final class ReadyRefreshTokenStorePort implements RefreshTokenStorePort {

        private final String tokenHash;

        private ReadyRefreshTokenStorePort(String tokenHash) {
            this.tokenHash = tokenHash;
        }

        @Override
        public void save(Long userId, String tokenHash, Instant expiresAt) {
        }

        @Override
        public Optional<StoredRefreshToken> findByTokenHash(String tokenHash) {
            return Optional.of(new StoredRefreshToken(1L, 1L, this.tokenHash, Instant.now().plusSeconds(300), null));
        }

        @Override
        public void revoke(Long tokenId, Instant revokedAt) {
        }
    }
}
