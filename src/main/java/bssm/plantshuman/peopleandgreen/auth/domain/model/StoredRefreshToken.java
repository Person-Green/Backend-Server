package bssm.plantshuman.peopleandgreen.auth.domain.model;

import java.time.Instant;

public record StoredRefreshToken(
        Long id,
        Long userId,
        String tokenHash,
        Instant expiresAt,
        Instant revokedAt
) {
    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }
}
