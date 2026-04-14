package bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUsernameRequest(
        @NotBlank String username
) {
}
