package bssm.plantshuman.peopleandgreen.auth.application.port.in;

import bssm.plantshuman.peopleandgreen.auth.domain.model.AuthTokens;

public interface RefreshAccessTokenUseCase {

    AuthTokens refresh(String refreshToken);
}
