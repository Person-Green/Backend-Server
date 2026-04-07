package bssm.plantshuman.peopleandgreen.auth.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request.GoogleLoginRequest;
import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request.RefreshTokenRequest;
import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.response.AuthTokenResponse;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.LoginWithGoogleUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.PrepareGoogleAuthorizationUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.RefreshAccessTokenUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    static final String OAUTH_STATE_COOKIE = "oauth_state_nonce";

    private final PrepareGoogleAuthorizationUseCase prepareGoogleAuthorizationUseCase;
    private final LoginWithGoogleUseCase loginWithGoogleUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    public AuthController(
            PrepareGoogleAuthorizationUseCase prepareGoogleAuthorizationUseCase,
            LoginWithGoogleUseCase loginWithGoogleUseCase,
            RefreshAccessTokenUseCase refreshAccessTokenUseCase
    ) {
        this.prepareGoogleAuthorizationUseCase = prepareGoogleAuthorizationUseCase;
        this.loginWithGoogleUseCase = loginWithGoogleUseCase;
        this.refreshAccessTokenUseCase = refreshAccessTokenUseCase;
    }

    @GetMapping("/google/authorize")
    public ResponseEntity<AuthTokenResponse.GoogleAuthorizationResponse> prepareGoogleAuthorization(
            @RequestParam String redirectUri
    ) {
        var authorization = prepareGoogleAuthorizationUseCase.prepare(redirectUri);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildStateCookie(authorization.stateNonce(), redirectUri).toString())
                .body(AuthTokenResponse.GoogleAuthorizationResponse.from(authorization));
    }

    @PostMapping("/google/login")
    public ResponseEntity<AuthTokenResponse> loginWithGoogle(
            @Valid @RequestBody GoogleLoginRequest request,
            @CookieValue(name = OAUTH_STATE_COOKIE, required = false) String stateNonce
    ) {
        if (stateNonce == null || stateNonce.isBlank()) {
            throw new IllegalArgumentException("Missing oauth state cookie");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearStateCookie(request.redirectUri()).toString())
                .body(AuthTokenResponse.from(
                        loginWithGoogleUseCase.login(
                                request.authorizationCode(),
                                request.state(),
                                stateNonce,
                                request.redirectUri()
                        )
                ));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(AuthTokenResponse.from(
                refreshAccessTokenUseCase.refresh(request.refreshToken())
        ));
    }

    private ResponseCookie buildStateCookie(String stateNonce, String redirectUri) {
        return ResponseCookie.from(OAUTH_STATE_COOKIE, stateNonce)
                .httpOnly(true)
                .secure(redirectUri.startsWith("https://"))
                .sameSite("Lax")
                .path("/auth")
                .maxAge(300)
                .build();
    }

    private ResponseCookie clearStateCookie(String redirectUri) {
        return ResponseCookie.from(OAUTH_STATE_COOKIE, "")
                .httpOnly(true)
                .secure(redirectUri.startsWith("https://"))
                .sameSite("Lax")
                .path("/auth")
                .maxAge(0)
                .build();
    }
}
