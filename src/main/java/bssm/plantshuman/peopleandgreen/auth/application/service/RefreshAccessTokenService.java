package bssm.plantshuman.peopleandgreen.auth.application.service;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.RefreshTokenHasher;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.RefreshAccessTokenUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.IssueJwtPort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.RefreshTokenStorePort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.UserAccountPort;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AuthTokens;
import bssm.plantshuman.peopleandgreen.auth.domain.model.StoredRefreshToken;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshAccessTokenService implements RefreshAccessTokenUseCase {

    private final IssueJwtPort issueJwtPort;
    private final UserAccountPort userAccountPort;
    private final RefreshTokenStorePort refreshTokenStorePort;
    private final RefreshTokenHasher refreshTokenHasher;

    public RefreshAccessTokenService(
            IssueJwtPort issueJwtPort,
            UserAccountPort userAccountPort,
            RefreshTokenStorePort refreshTokenStorePort,
            RefreshTokenHasher refreshTokenHasher
    ) {
        this.issueJwtPort = issueJwtPort;
        this.userAccountPort = userAccountPort;
        this.refreshTokenStorePort = refreshTokenStorePort;
        this.refreshTokenHasher = refreshTokenHasher;
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        if (!issueJwtPort.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token type");
        }

        Long userId = issueJwtPort.parseUserId(refreshToken);
        StoredRefreshToken storedToken = refreshTokenStorePort.findByTokenHash(refreshTokenHasher.hash(refreshToken))
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (!userId.equals(storedToken.userId()) || storedToken.isExpired(Instant.now()) || storedToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token is invalid");
        }

        refreshTokenStorePort.revoke(storedToken.id(), Instant.now());

        AppUser user = userAccountPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newAccessToken = issueJwtPort.issueAccessToken(userId);
        String newRefreshToken = issueJwtPort.issueRefreshToken(userId);
        refreshTokenStorePort.save(
                userId,
                refreshTokenHasher.hash(newRefreshToken),
                Instant.now().plusSeconds(issueJwtPort.getRefreshTokenValiditySeconds())
        );

        return new AuthTokens(newAccessToken, newRefreshToken, issueJwtPort.getAccessTokenValiditySeconds(), user);
    }
}
