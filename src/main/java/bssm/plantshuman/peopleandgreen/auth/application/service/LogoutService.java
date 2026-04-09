package bssm.plantshuman.peopleandgreen.auth.application.service;

import bssm.plantshuman.peopleandgreen.auth.application.port.in.LogoutUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.IssueJwtPort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.RefreshTokenHashPort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.RefreshTokenStorePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {

    private final IssueJwtPort issueJwtPort;
    private final RefreshTokenStorePort refreshTokenStorePort;
    private final RefreshTokenHashPort refreshTokenHashPort;

    @Override
    public void logout(String refreshToken) {
        try {
            if (!issueJwtPort.isRefreshToken(refreshToken)) {
                return;
            }
            String hash = refreshTokenHashPort.hash(refreshToken);
            refreshTokenStorePort.findByTokenHash(hash)
                    .filter(t -> !t.isRevoked())
                    .ifPresent(t -> refreshTokenStorePort.revoke(t.id(), Instant.now()));
        } catch (Exception ignored) {
            // 이미 만료되거나 잘못된 토큰은 로그아웃 처리에서 무시한다
        }
    }
}
