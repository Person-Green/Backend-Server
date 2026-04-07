package bssm.plantshuman.peopleandgreen.auth.domain.model;

public record PreparedGoogleAuthorization(
        String authorizationUrl,
        String state,
        String stateNonce
) {
}
