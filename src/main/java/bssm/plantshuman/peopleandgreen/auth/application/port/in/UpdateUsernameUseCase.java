package bssm.plantshuman.peopleandgreen.auth.application.port.in;

import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;

public interface UpdateUsernameUseCase {

    AppUser updateUsername(Long userId, String username);
}
