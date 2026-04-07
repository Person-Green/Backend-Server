package bssm.plantshuman.peopleandgreen.auth.application.service;

import bssm.plantshuman.peopleandgreen.auth.application.config.GoogleOAuthProperties;
import bssm.plantshuman.peopleandgreen.auth.application.port.in.LoginWithGoogleUseCase;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.ExchangeGoogleAuthCodePort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.IssueJwtPort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.RefreshTokenStorePort;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.UserAccountPort;
import bssm.plantshuman.peopleandgreen.auth.adapter.out.security.RefreshTokenHasher;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AuthTokens;
import bssm.plantshuman.peopleandgreen.auth.domain.model.GoogleUserInfo;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class LoginWithGoogleService implements LoginWithGoogleUseCase {

    private final GoogleOAuthProperties properties;
    private final ExchangeGoogleAuthCodePort exchangeGoogleAuthCodePort;
    private final UserAccountPort userAccountPort;
    private final IssueJwtPort issueJwtPort;
    private final RefreshTokenStorePort refreshTokenStorePort;
    private final RefreshTokenHasher refreshTokenHasher;

    public LoginWithGoogleService(
            GoogleOAuthProperties properties,
            ExchangeGoogleAuthCodePort exchangeGoogleAuthCodePort,
            UserAccountPort userAccountPort,
            IssueJwtPort issueJwtPort,
            RefreshTokenStorePort refreshTokenStorePort,
            RefreshTokenHasher refreshTokenHasher
    ) {
        this.properties = properties;
        this.exchangeGoogleAuthCodePort = exchangeGoogleAuthCodePort;
        this.userAccountPort = userAccountPort;
        this.issueJwtPort = issueJwtPort;
        this.refreshTokenStorePort = refreshTokenStorePort;
        this.refreshTokenHasher = refreshTokenHasher;
    }

    @Override
    public AuthTokens login(String authorizationCode, String state, String stateNonce, String redirectUri) {
        PrepareGoogleAuthorizationService.validateRedirectUri(redirectUri, properties);
        issueJwtPort.validateGoogleState(state, redirectUri, stateNonce);

        GoogleUserInfo googleUserInfo = exchangeGoogleAuthCodePort.exchange(authorizationCode, redirectUri);
        AppUser user = userAccountPort.upsertGoogleUser(googleUserInfo);

        return issueTokens(user);
    }

    AuthTokens issueTokens(AppUser user) {
        String accessToken = issueJwtPort.issueAccessToken(user.id());
        String refreshToken = issueJwtPort.issueRefreshToken(user.id());
        refreshTokenStorePort.save(
                user.id(),
                refreshTokenHasher.hash(refreshToken),
                Instant.now().plusSeconds(issueJwtPort.getRefreshTokenValiditySeconds())
        );
        return new AuthTokens(accessToken, refreshToken, issueJwtPort.getAccessTokenValiditySeconds(), user);
    }
}
