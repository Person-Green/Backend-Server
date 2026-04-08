package bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.repository;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.entity.AppUserEntity;
import bssm.plantshuman.peopleandgreen.auth.domain.model.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUserEntity, Long> {

    Optional<AppUserEntity> findByOauthProviderAndOauthProviderUserId(OAuthProvider oauthProvider, String oauthProviderUserId);
}
