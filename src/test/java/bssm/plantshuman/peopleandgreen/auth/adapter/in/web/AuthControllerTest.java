package bssm.plantshuman.peopleandgreen.auth.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request.GoogleLoginRequest;
import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.response.AuthTokenResponse;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AuthTokens;
import bssm.plantshuman.peopleandgreen.auth.domain.model.OAuthProvider;
import bssm.plantshuman.peopleandgreen.auth.domain.model.PreparedGoogleAuthorization;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthControllerTest {

    @Test
    void returnsAuthorizationUrlAndState() {
        AuthController controller = controller();

        ResponseEntity<AuthTokenResponse.GoogleAuthorizationResponse> response =
                controller.prepareGoogleAuthorization("http://localhost:3000/auth/google/callback");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("STATE_TOKEN", response.getBody().state());
    }

    @Test
    void mapsGoogleLoginResult_andSetsRefreshCookie() {
        AuthController controller = controller();
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();

        ResponseEntity<AuthTokenResponse> response = controller.loginWithGoogle(
                new GoogleLoginRequest("AUTH_CODE", "STATE_TOKEN", "http://localhost:3000/auth/google/callback"),
                httpResponse
        );

        assertEquals(200, response.getStatusCode().value());
        assertEquals("ACCESS_TOKEN", response.getBody().accessToken());
        assertEquals(1L, response.getBody().user().id());
        String setCookie = httpResponse.getHeader("Set-Cookie");
        assertNotNull(setCookie);
        assertTrue(setCookie.contains("refresh_token="));
        assertTrue(setCookie.contains("HttpOnly"));
    }

    @Test
    void refreshSetsNewCookieAndReturnsAccessToken() {
        AuthController controller = controller();
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();

        ResponseEntity<AuthTokenResponse> response = controller.refresh("REFRESH_TOKEN", httpResponse);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("ACCESS_TOKEN", response.getBody().accessToken());
        String setCookie = httpResponse.getHeader("Set-Cookie");
        assertNotNull(setCookie);
        assertTrue(setCookie.contains("refresh_token="));
    }

    @Test
    void refreshReturns401WhenCookieMissing() {
        AuthController controller = controller();
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();

        ResponseEntity<AuthTokenResponse> response = controller.refresh(null, httpResponse);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void logoutClearsRefreshCookie() {
        AuthController controller = controller();
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();

        ResponseEntity<Void> response = controller.logout("REFRESH_TOKEN", httpResponse);

        assertEquals(204, response.getStatusCode().value());
        String setCookie = httpResponse.getHeader("Set-Cookie");
        assertNotNull(setCookie);
        assertTrue(setCookie.contains("Max-Age=0"));
    }

    private AuthController controller() {
        return new AuthController(
                redirectUri -> new PreparedGoogleAuthorization(
                        "https://accounts.google.com/o/oauth2/v2/auth?state=STATE_TOKEN", "STATE_TOKEN"),
                (authorizationCode, state, redirectUri) -> tokens(),
                refreshToken -> tokens(),
                refreshToken -> {}
        );
    }

    private static AuthTokens tokens() {
        return new AuthTokens(
                "ACCESS_TOKEN",
                "REFRESH_TOKEN",
                900,
                1209600,
                new AppUser(1L, OAuthProvider.GOOGLE, "google-123", "jjm@example.com", "jjm", "https://image")
        );
    }
}
