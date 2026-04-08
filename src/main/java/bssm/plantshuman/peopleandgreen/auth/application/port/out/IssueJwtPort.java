package bssm.plantshuman.peopleandgreen.auth.application.port.out;

public interface IssueJwtPort {

    String issueAccessToken(Long userId);

    String issueRefreshToken(Long userId);

    long getAccessTokenValiditySeconds();

    long getRefreshTokenValiditySeconds();

    String issueGoogleState(String redirectUri);

    void validateGoogleState(String token, String redirectUri);

    Long parseUserId(String token);

    boolean isRefreshToken(String token);
}
