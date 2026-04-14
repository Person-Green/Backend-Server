package bssm.plantshuman.peopleandgreen.auth.application.service;

import bssm.plantshuman.peopleandgreen.auth.application.port.in.UpdateUsernameUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.UserAccountPort;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUsernameService implements UpdateUsernameUseCase {

    private static final int MAX_USERNAME_LENGTH = 30;

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
        if (normalized.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException("Username must be at most 30 characters");
        }
        return normalized;
    }
}
