package bssm.plantshuman.peopleandgreen.auth.application.service;

import bssm.plantshuman.peopleandgreen.auth.application.config.GoogleOAuthProperties;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.ExchangeGoogleAuthCodePort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.IssueJwtPort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.RefreshTokenHashPort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.RefreshTokenStorePort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.UserAccountPort;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import bssm.plantshuman.peopleandgreen.auth.domain.model.GoogleUserInfo;
import bssm.plantshuman.peopleandgreen.auth.domain.model.OAuthProvider;
import bssm.plantshuman.peopleandgreen.auth.domain.model.PreparedGoogleAuthorization;
import bssm.plantshuman.peopleandgreen.auth.domain.model.StoredRefreshToken;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceTest {

    private static final RefreshTokenHashPort HASH_PORT = token -> "HASHED_" + token;

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
                HASH_PORT
        );

        var tokens = service.login("AUTH_CODE", "STATE_TOKEN", "http://localhost:3000/auth/google/callback");

        assertEquals("ACCESS_TOKEN", tokens.accessToken());
        assertEquals("REFRESH_TOKEN", tokens.refreshToken());
        assertEquals(900, tokens.expiresIn());
        assertEquals(1L, tokens.user().id());
        assertEquals(1L, refreshTokenStorePort.savedUserId);
        assertEquals("HASHED_REFRESH_TOKEN", refreshTokenStorePort.savedTokenHash);
    }

    @Test
    void keepsExistingUsernameWhenGoogleUserLogsInAgain() {
        RecordingUserAccountPort userAccountPort = new RecordingUserAccountPort(
                new AppUser(1L, OAuthProvider.GOOGLE, "google-123", "before@example.com", "chosenName", "https://old-image")
        );
        LoginWithGoogleService service = new LoginWithGoogleService(
                GOOGLE_PROPERTIES,
                (code, redirectUri) -> new GoogleUserInfo("google-123", "after@example.com", "googleName", "https://new-image"),
                userAccountPort,
                new StubIssueJwtPort(),
                new RecordingRefreshTokenStorePort(),
                HASH_PORT
        );

        var tokens = service.login("AUTH_CODE", "STATE_TOKEN", "http://localhost:3000/auth/google/callback");

        assertEquals("chosenName", tokens.user().name());
        assertEquals("after@example.com", tokens.user().email());
        assertEquals("https://new-image", tokens.user().profileImageUrl());
    }

    @Test
    void rejectsLoginWhenRedirectUriIsNotAllowed() {
        LoginWithGoogleService service = new LoginWithGoogleService(
                GOOGLE_PROPERTIES,
                (code, redirectUri) -> new GoogleUserInfo("google-123", "jjm@example.com", "jjm", "https://image"),
                new StubUserAccountPort(),
                new StubIssueJwtPort(),
                new RecordingRefreshTokenStorePort(),
                HASH_PORT
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.login("AUTH_CODE", "STATE_TOKEN", "http://localhost:3000/other"));
    }

    @Test
    void rotatesRefreshTokenWhenRefreshingAccessToken() {
        RefreshAccessTokenService service = new RefreshAccessTokenService(
                new StubIssueJwtPort(),
                new StubUserAccountPort(),
                new ReadyRefreshTokenStorePort("HASHED_REFRESH_TOKEN"),
                HASH_PORT
        );

        var tokens = service.refresh("REFRESH_TOKEN");

        assertEquals("ACCESS_TOKEN", tokens.accessToken());
        assertEquals("REFRESH_TOKEN", tokens.refreshToken());
        assertEquals(1L, tokens.user().id());
    }

    @Test
    void revokesStoredRefreshTokenOnLogout() {
        RecordingRefreshTokenStorePort refreshTokenStorePort = new RecordingRefreshTokenStorePort();
        refreshTokenStorePort.storedToken = new StoredRefreshToken(7L, 1L, "HASHED_REFRESH_TOKEN", Instant.now().plusSeconds(300), null);
        LogoutService service = new LogoutService(
                new StubIssueJwtPort(),
                refreshTokenStorePort,
                HASH_PORT
        );

        service.logout("REFRESH_TOKEN");

        assertEquals(7L, refreshTokenStorePort.revokedTokenId);
    }

    @Test
    void ignoresLogoutWhenTokenIsNotRefreshToken() {
        RecordingRefreshTokenStorePort refreshTokenStorePort = new RecordingRefreshTokenStorePort();
        LogoutService service = new LogoutService(
                new NonRefreshTokenIssueJwtPort(),
                refreshTokenStorePort,
                HASH_PORT
        );

        service.logout("ACCESS_TOKEN");

        assertEquals(null, refreshTokenStorePort.revokedTokenId);
    }

    @Test
    void updatesUsernameForExistingUser() {
        UpdateUsernameService service = new UpdateUsernameService(new RecordingUserAccountPort());

        AppUser user = service.updateUsername(1L, "  UserName  ");

        assertEquals("UserName", user.name());
    }

    @Test
    void rejectsBlankUsername() {
        UpdateUsernameService service = new UpdateUsernameService(new RecordingUserAccountPort());

        assertThrows(IllegalArgumentException.class, () -> service.updateUsername(1L, "   "));
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

    private static final class NonRefreshTokenIssueJwtPort implements IssueJwtPort {

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
        }

        @Override
        public Long parseUserId(String token) {
            return 1L;
        }

        @Override
        public boolean isRefreshToken(String token) {
            return false;
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

        @Override
        public AppUser updateUsername(Long userId, String username) {
            return new AppUser(userId, OAuthProvider.GOOGLE, "google-123", "jjm@example.com", username, "https://image");
        }
    }

    private static final class RecordingUserAccountPort implements UserAccountPort {

        private AppUser existingUser;

        private RecordingUserAccountPort() {
            this(new AppUser(1L, OAuthProvider.GOOGLE, "google-123", "jjm@example.com", "jjm", "https://image"));
        }

        private RecordingUserAccountPort(AppUser existingUser) {
            this.existingUser = existingUser;
        }

        @Override
        public AppUser upsertGoogleUser(GoogleUserInfo userInfo) {
            if (existingUser != null
                    && existingUser.oauthProvider() == OAuthProvider.GOOGLE
                    && existingUser.oauthProviderUserId().equals(userInfo.providerUserId())) {
                existingUser = new AppUser(
                        existingUser.id(),
                        existingUser.oauthProvider(),
                        existingUser.oauthProviderUserId(),
                        userInfo.email(),
                        existingUser.name(),
                        userInfo.profileImageUrl()
                );
                return existingUser;
            }

            existingUser = new AppUser(1L, OAuthProvider.GOOGLE, userInfo.providerUserId(), userInfo.email(), userInfo.name(), userInfo.profileImageUrl());
            return existingUser;
        }

        @Override
        public Optional<AppUser> findById(Long userId) {
            return Optional.ofNullable(existingUser).filter(user -> user.id().equals(userId));
        }

        @Override
        public AppUser updateUsername(Long userId, String username) {
            existingUser = new AppUser(userId, OAuthProvider.GOOGLE, "google-123", "jjm@example.com", username, "https://image");
            return existingUser;
        }
    }

    private static final class RecordingRefreshTokenStorePort implements RefreshTokenStorePort {

        private Long savedUserId;
        private String savedTokenHash;
        private Long revokedTokenId;
        private StoredRefreshToken storedToken;

        @Override
        public void save(Long userId, String tokenHash, Instant expiresAt) {
            this.savedUserId = userId;
            this.savedTokenHash = tokenHash;
        }

        @Override
        public Optional<StoredRefreshToken> findByTokenHash(String tokenHash) {
            return Optional.ofNullable(storedToken);
        }

        @Override
        public void revoke(Long tokenId, Instant revokedAt) {
            this.revokedTokenId = tokenId;
        }

        @Override
        public void revokeAllByUserId(Long userId, Instant revokedAt) {
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

        @Override
        public void revokeAllByUserId(Long userId, Instant revokedAt) {
        }
    }
}
