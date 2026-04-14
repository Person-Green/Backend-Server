package bssm.plantshuman.peopleandgreen.auth.adapter.in.web;

import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.request.UpdateUsernameRequest;
import bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.response.AuthTokenResponse;
import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.AuthenticatedUser;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.UpdateUsernameUseCase;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import bssm.plantshuman.peopleandgreen.auth.domain.model.OAuthProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

    @Test
    void updatesUsernameForAuthenticatedUser() {
        RecordingUpdateUsernameUseCase useCase = new RecordingUpdateUsernameUseCase();
        UserController controller = new UserController(useCase);

        ResponseEntity<AuthTokenResponse.UserResponse> response = controller.updateUsername(
                new AuthenticatedUser(1L),
                new UpdateUsernameRequest("UserName")
        );

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, useCase.userId);
        assertEquals("UserName", useCase.username);
        assertEquals("UserName", response.getBody().name());
    }

    private static final class RecordingUpdateUsernameUseCase implements UpdateUsernameUseCase {

        private Long userId;
        private String username;

        @Override
        public AppUser updateUsername(Long userId, String username) {
            this.userId = userId;
            this.username = username;
            return new AppUser(userId, OAuthProvider.GOOGLE, "google-123", "jjm@example.com", username, "https://image");
        }
    }
}
