package bssm.plantshuman.peopleandgreen.auth.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request.GoogleLoginRequest;
import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request.RefreshTokenRequest;
import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.response.AuthTokenResponse;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.LoginWithGoogleUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.PrepareGoogleAuthorizationUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.RefreshAccessTokenUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

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
        return ResponseEntity.ok(AuthTokenResponse.GoogleAuthorizationResponse.from(
                prepareGoogleAuthorizationUseCase.prepare(redirectUri)
        ));
    }

    @PostMapping("/google/login")
    public ResponseEntity<AuthTokenResponse> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        return ResponseEntity.ok(AuthTokenResponse.from(
                loginWithGoogleUseCase.login(request.authorizationCode(), request.state(), request.redirectUri())
        ));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(AuthTokenResponse.from(
                refreshAccessTokenUseCase.refresh(request.refreshToken())
        ));
    }
}
