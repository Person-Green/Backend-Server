package bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.repository.RefreshTokenRepository;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.RefreshTokenStorePort;
import bssm.plantshuman.peopleandgreen.auth.domain.model.StoredRefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Component
public class RefreshTokenPersistenceAdapter implements RefreshTokenStorePort {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserPersistenceAdapter userPersistenceAdapter;

    public RefreshTokenPersistenceAdapter(
            RefreshTokenRepository refreshTokenRepository,
            UserPersistenceAdapter userPersistenceAdapter
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userPersistenceAdapter = userPersistenceAdapter;
    }

    @Override
    @Transactional
    public void save(Long userId, String tokenHash, Instant expiresAt) {
        refreshTokenRepository.save(new RefreshTokenEntity(
                userPersistenceAdapter.getReference(userId),
                tokenHash,
                expiresAt,
                Instant.now()
        ));
    }

    @Override
    public Optional<StoredRefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    @Transactional
    public void revoke(Long tokenId, Instant revokedAt) {
        refreshTokenRepository.findById(tokenId).ifPresent(token -> token.revoke(revokedAt));
    }

    @Override
    @Transactional
    public void revokeAllByUserId(Long userId, Instant revokedAt) {
        refreshTokenRepository.revokeAllActiveByUserId(userId, revokedAt);
    }

    private StoredRefreshToken toDomain(RefreshTokenEntity entity) {
        return new StoredRefreshToken(
                entity.getId(),
                entity.getUser().getId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.getRevokedAt()
        );
    }
}
