package bssm.plantshuman.peopleandgreen.auth.application.service;

import bssm.plantshuman.peopleandgreen.auth.application.port.in.UpdateUsernameUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.UserAccountPort;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUsernameService implements UpdateUsernameUseCase {

    private final UserAccountPort userAccountPort;

    @Override
    public AppUser updateUsername(Long userId, String username) {
        String normalizedUsername = normalize(username);
        return userAccountPort.updateUsername(userId, normalizedUsername);
    }

    private String normalize(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is required");
        }

        String normalized = username.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        return normalized;
    }
}
