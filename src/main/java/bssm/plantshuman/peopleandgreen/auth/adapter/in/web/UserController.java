package bssm.plantshuman.peopleandgreen.auth.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request.UpdateUsernameRequest;
import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.response.AuthTokenResponse;
import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.AuthenticatedUser;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.UpdateUsernameUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UpdateUsernameUseCase updateUsernameUseCase;

    @PatchMapping("/me/username")
    public ResponseEntity<AuthTokenResponse.UserResponse> updateUsername(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody UpdateUsernameRequest request
    ) {
        return ResponseEntity.ok(AuthTokenResponse.UserResponse.from(
                updateUsernameUseCase.updateUsername(authenticatedUser.userId(), request.username())
        ));
    }
}
