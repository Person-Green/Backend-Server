package bssm.plantshuman.peopleandgreen.auth.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request.GoogleLoginRequest;
import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request.RefreshTokenRequest;
import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.response.AuthTokenResponse;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.LoginWithGoogleUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.PrepareGoogleAuthorizationUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.RefreshAccessTokenUseCase;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AuthTokens;
import bssm.plantshuman.peopleandgreen.auth.domain.model.OAuthProvider;
import bssm.plantshuman.peopleandgreen.auth.domain.model.PreparedGoogleAuthorization;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthControllerTest {

    @Test
    void returnsAuthorizationUrlAndState() {
        AuthController controller = new AuthController(
                redirectUri -> new PreparedGoogleAuthorization("https://accounts.google.com/o/oauth2/v2/auth?state=STATE_TOKEN", "STATE_TOKEN", "STATE_NONCE"),
                (authorizationCode, state, stateNonce, redirectUri) -> tokens(),
                refreshToken -> tokens()
        );

        ResponseEntity<AuthTokenResponse.GoogleAuthorizationResponse> response =
                controller.prepareGoogleAuthorization("http://localhost:3000/auth/google/callback");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("STATE_TOKEN", response.getBody().state());
        assertEquals(true, response.getHeaders().getFirst("Set-Cookie").contains("oauth_state_nonce=STATE_NONCE"));
    }

    @Test
    void mapsGoogleLoginResult() {
        AuthController controller = new AuthController(
                redirectUri -> new PreparedGoogleAuthorization("https://accounts.google.com/o/oauth2/v2/auth?state=STATE_TOKEN", "STATE_TOKEN", "STATE_NONCE"),
                (authorizationCode, state, stateNonce, redirectUri) -> tokens(),
                refreshToken -> tokens()
        );

        ResponseEntity<AuthTokenResponse> response = controller.loginWithGoogle(
                new GoogleLoginRequest("AUTH_CODE", "STATE_TOKEN", "http://localhost:3000/auth/google/callback"),
                "STATE_NONCE"
        );

        assertEquals(200, response.getStatusCode().value());
        assertEquals("ACCESS_TOKEN", response.getBody().accessToken());
        assertEquals(1L, response.getBody().user().id());
        assertEquals(true, response.getHeaders().getFirst("Set-Cookie").contains("Max-Age=0"));
    }

    @Test
    void mapsRefreshResult() {
        AuthController controller = new AuthController(
                redirectUri -> new PreparedGoogleAuthorization("https://accounts.google.com/o/oauth2/v2/auth?state=STATE_TOKEN", "STATE_TOKEN", "STATE_NONCE"),
                (authorizationCode, state, stateNonce, redirectUri) -> tokens(),
                refreshToken -> tokens()
        );

        ResponseEntity<AuthTokenResponse> response = controller.refresh(new RefreshTokenRequest("REFRESH_TOKEN"));

        assertEquals(200, response.getStatusCode().value());
        assertEquals("REFRESH_TOKEN", response.getBody().refreshToken());
    }

    private static AuthTokens tokens() {
        return new AuthTokens(
                "ACCESS_TOKEN",
                "REFRESH_TOKEN",
                900,
                new AppUser(1L, OAuthProvider.GOOGLE, "google-123", "jjm@example.com", "jjm", "https://image")
        );
    }
}
