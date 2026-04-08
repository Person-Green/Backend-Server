package bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank String authorizationCode,
        @NotBlank String state,
        @NotBlank String redirectUri
) {
}
