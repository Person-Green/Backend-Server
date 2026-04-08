package bssm.plantshuman.peopleandgreen.auth.application.port.out;

import bssm.plantshuman.peopleandgreen.auth.domain.model.StoredRefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenStorePort {

    void save(Long userId, String tokenHash, Instant expiresAt);

    Optional<StoredRefreshToken> findByTokenHash(String tokenHash);

    void revoke(Long tokenId, Instant revokedAt);

    void revokeAllByUserId(Long userId, Instant revokedAt);
}
