package bssm.plantshuman.peopleandgreen.auth.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request.GoogleLoginRequest;
import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.response.AuthTokenResponse;
import bssm.plantshuman.peopleandgreen.auth.application.config.SecurityProperties;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.LoginWithGoogleUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.LogoutUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.PrepareGoogleAuthorizationUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.RefreshAccessTokenUseCase;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AuthTokens;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final PrepareGoogleAuthorizationUseCase prepareGoogleAuthorizationUseCase;
    private final LoginWithGoogleUseCase loginWithGoogleUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final SecurityProperties securityProperties;

    @GetMapping("/google/authorize")
    public ResponseEntity<AuthTokenResponse.GoogleAuthorizationResponse> prepareGoogleAuthorization(
            @RequestParam String redirectUri
    ) {
        return ResponseEntity.ok(AuthTokenResponse.GoogleAuthorizationResponse.from(
                prepareGoogleAuthorizationUseCase.prepare(redirectUri)
        ));
    }

    @PostMapping("/google/login")
    public ResponseEntity<AuthTokenResponse> loginWithGoogle(
            @Valid @RequestBody GoogleLoginRequest request,
            HttpServletResponse servletResponse
    ) {
        AuthTokens tokens = loginWithGoogleUseCase.login(
                request.authorizationCode(), request.state(), request.redirectUri()
        );
        setRefreshTokenCookie(servletResponse, tokens.refreshToken(), tokens.refreshExpiresIn());
        return ResponseEntity.ok(AuthTokenResponse.from(tokens));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
            HttpServletResponse servletResponse
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthTokens tokens = refreshAccessTokenUseCase.refresh(refreshToken);
        setRefreshTokenCookie(servletResponse, tokens.refreshToken(), tokens.refreshExpiresIn());
        return ResponseEntity.ok(AuthTokenResponse.from(tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
            HttpServletResponse servletResponse
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            logoutUseCase.logout(refreshToken);
        }
        clearRefreshTokenCookie(servletResponse);
        return ResponseEntity.noContent().build();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token, long maxAgeSeconds) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildRefreshCookie(token, maxAgeSeconds).toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildRefreshCookie("", 0).toString());
    }

    private ResponseCookie buildRefreshCookie(String value, long maxAgeSeconds) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, value)
                .httpOnly(true)
                .secure(securityProperties.isRequireHttps())
                .sameSite("Strict")
                .path("/auth")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .build();
    }
}
