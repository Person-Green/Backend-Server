package bssm.plantshuman.peopleandgreen.auth.adapter.in.web.dto.response;

import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AuthTokens;
import bssm.plantshuman.peopleandgreen.auth.domain.model.PreparedGoogleAuthorization;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        UserResponse user
) {
    public static AuthTokenResponse from(AuthTokens tokens) {
        return new AuthTokenResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.expiresIn(),
                tokens.user() == null ? null : UserResponse.from(tokens.user())
        );
    }

    public record UserResponse(
            Long id,
            String email,
            String name,
            String profileImageUrl
    ) {
        static UserResponse from(AppUser user) {
            return new UserResponse(user.id(), user.email(), user.name(), user.profileImageUrl());
        }
    }

    public record GoogleAuthorizationResponse(
            String authorizationUrl,
            String state
    ) {
        public static GoogleAuthorizationResponse from(PreparedGoogleAuthorization authorization) {
            return new GoogleAuthorizationResponse(authorization.authorizationUrl(), authorization.state());
        }
    }
}
