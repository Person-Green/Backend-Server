package bssm.plantshuman.peopleandgreen.auth.application.port.out;

import bssm.plantshuman.peopleandgreen.auth.domain.model.GoogleUserInfo;

public interface ExchangeGoogleAuthCodePort {

    GoogleUserInfo exchange(String authorizationCode, String redirectUri);
}
