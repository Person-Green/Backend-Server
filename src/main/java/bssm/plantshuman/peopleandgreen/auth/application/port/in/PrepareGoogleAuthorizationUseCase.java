package bssm.plantshuman.peopleandgreen.auth.application.port.in;

import bssm.plantshuman.peopleandgreen.auth.domain.model.PreparedGoogleAuthorization;

public interface PrepareGoogleAuthorizationUseCase {

    PreparedGoogleAuthorization prepare(String redirectUri);
}
