package bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUsernameRequest(
        @NotBlank
        @Size(max = 30)
        String username
) {
}
