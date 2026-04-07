package bssm.plantshuman.peopleandgreen.auth.application.port.out;

public interface IssueJwtPort {

    String issueAccessToken(Long userId);

    String issueRefreshToken(Long userId);

    long getAccessTokenValiditySeconds();

    long getRefreshTokenValiditySeconds();

    String issueGoogleState(String redirectUri, String stateNonce);

    void validateGoogleState(String token, String redirectUri, String stateNonce);

    Long parseUserId(String token);

    boolean isRefreshToken(String token);
}
