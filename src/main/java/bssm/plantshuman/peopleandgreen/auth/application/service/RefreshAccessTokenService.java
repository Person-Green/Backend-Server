package bssm.plantshuman.peopleandgreen.auth.application.service;

import bssm.plantshuman.peopleandgreen.auth.application.port.in.RefreshAccessTokenUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.IssueJwtPort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.RefreshTokenHashPort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.RefreshTokenStorePort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.UserAccountPort;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AuthTokens;
import bssm.plantshuman.peopleandgreen.auth.domain.model.StoredRefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshAccessTokenService implements RefreshAccessTokenUseCase {

    private final IssueJwtPort issueJwtPort;
    private final UserAccountPort userAccountPort;
    private final RefreshTokenStorePort refreshTokenStorePort;
    private final RefreshTokenHashPort refreshTokenHashPort;

    @Override
    @Transactional
    public AuthTokens refresh(String refreshToken) {
        if (!issueJwtPort.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid token type: not a refresh token");
        }

        Long userId = issueJwtPort.parseUserId(refreshToken);
        StoredRefreshToken storedToken = refreshTokenStorePort.findByTokenHash(refreshTokenHashPort.hash(refreshToken))
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        // 이미 revoked된 토큰으로 갱신 시도 → 탈취 의심 → 해당 사용자의 모든 세션 무효화
        if (storedToken.isRevoked()) {
            log.warn("[SECURITY] Refresh token reuse detected for userId={}. Revoking all sessions.", storedToken.userId());
            refreshTokenStorePort.revokeAllByUserId(storedToken.userId(), Instant.now());
            throw new SecurityException("Token reuse detected — all sessions have been revoked");
        }

        if (!userId.equals(storedToken.userId()) || storedToken.isExpired(Instant.now())) {
            throw new IllegalArgumentException("Refresh token is invalid or expired");
        }

        // Rotation: 기존 토큰 revoke 후 새 토큰 발급
        refreshTokenStorePort.revoke(storedToken.id(), Instant.now());

        AppUser user = userAccountPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newAccessToken = issueJwtPort.issueAccessToken(userId);
        String newRefreshToken = issueJwtPort.issueRefreshToken(userId);
        refreshTokenStorePort.save(
                userId,
                refreshTokenHashPort.hash(newRefreshToken),
                Instant.now().plusSeconds(issueJwtPort.getRefreshTokenValiditySeconds())
        );

        return new AuthTokens(
                newAccessToken,
                newRefreshToken,
                issueJwtPort.getAccessTokenValiditySeconds(),
                issueJwtPort.getRefreshTokenValiditySeconds(),
                user
        );
    }
}
