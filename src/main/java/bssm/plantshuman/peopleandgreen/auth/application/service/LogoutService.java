package bssm.plantshuman.peopleandgreen.auth.application.service;

import bssm.plantshuman.peopleandgreen.auth.application.port.in.LogoutUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.IssueJwtPort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.RefreshTokenHasherPort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.RefreshTokenStorePort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class LogoutService implements LogoutUseCase {

    private final IssueJwtPort issueJwtPort;
    private final RefreshTokenStorePort refreshTokenStorePort;
    private final RefreshTokenHasherPort refreshTokenHasher;

    public LogoutService(
            IssueJwtPort issueJwtPort,
            RefreshTokenStorePort refreshTokenStorePort,
            RefreshTokenHasherPort refreshTokenHasher
    ) {
        this.issueJwtPort = issueJwtPort;
        this.refreshTokenStorePort = refreshTokenStorePort;
        this.refreshTokenHasher = refreshTokenHasher;
    }

    @Override
    public void logout(String refreshToken) {
        try {
            if (!issueJwtPort.isRefreshToken(refreshToken)) {
                return;
            }
            String hash = refreshTokenHasher.hash(refreshToken);
            refreshTokenStorePort.findByTokenHash(hash)
                    .filter(t -> !t.isRevoked())
                    .ifPresent(t -> refreshTokenStorePort.revoke(t.id(), Instant.now()));
        } catch (Exception ignored) {
            // 이미 만료되거나 잘못된 토큰은 로그아웃 처리에서 무시한다
        }
    }
}
