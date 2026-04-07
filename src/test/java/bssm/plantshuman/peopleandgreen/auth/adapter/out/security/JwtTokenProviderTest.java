package bssm.plantshuman.peopleandgreen.auth.adapter.out.security;

import bssm.plantshuman.peopleandgreen.auth.application.config.AuthJwtProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(new AuthJwtProperties(
            "peopleandgreen",
            "test-secret-key-test-secret-key-test-secret-key",
            900,
            1209600,
            300
    ));

    @Test
    void issuesAccessTokenWithUserIdSubject() {
        String accessToken = tokenProvider.issueAccessToken(1L);

        assertEquals(1L, tokenProvider.parseUserId(accessToken));
        assertFalse(tokenProvider.isRefreshToken(accessToken));
    }

    @Test
    void issuesRefreshTokenWithRefreshMarker() {
        String refreshToken = tokenProvider.issueRefreshToken(1L);

        assertEquals(1L, tokenProvider.parseUserId(refreshToken));
        assertTrue(tokenProvider.isRefreshToken(refreshToken));
    }

    @Test
    void validatesIssuedGoogleStateAgainstRedirectUri() {
        String state = tokenProvider.issueGoogleState("http://localhost:3000/auth/google/callback", "nonce");

        tokenProvider.validateGoogleState(state, "http://localhost:3000/auth/google/callback", "nonce");
    }

    @Test
    void rejectsGoogleStateWhenRedirectUriDoesNotMatch() {
        String state = tokenProvider.issueGoogleState("http://localhost:3000/auth/google/callback", "nonce");

        assertThrows(IllegalArgumentException.class, () ->
                tokenProvider.validateGoogleState(state, "http://localhost:3000/auth/google/other", "nonce"));
    }

    @Test
    void rejectsGoogleStateWhenNonceDoesNotMatch() {
        String state = tokenProvider.issueGoogleState("http://localhost:3000/auth/google/callback", "nonce");

        assertThrows(IllegalArgumentException.class, () ->
                tokenProvider.validateGoogleState(state, "http://localhost:3000/auth/google/callback", "other"));
    }

    @Test
    void rejectsProviderCreationWhenSecretIsMissing() {
        assertThrows(IllegalStateException.class, () -> new JwtTokenProvider(new AuthJwtProperties(
                "peopleandgreen",
                "",
                900,
                1209600,
                300
        )));
    }
}
