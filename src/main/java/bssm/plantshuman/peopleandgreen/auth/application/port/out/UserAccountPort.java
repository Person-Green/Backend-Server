package bssm.plantshuman.peopleandgreen.auth.application.port.out;

import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import bssm.plantshuman.peopleandgreen.auth.domain.model.GoogleUserInfo;

import java.util.Optional;

public interface UserAccountPort {

    AppUser upsertGoogleUser(GoogleUserInfo userInfo);

    Optional<AppUser> findById(Long userId);
}
