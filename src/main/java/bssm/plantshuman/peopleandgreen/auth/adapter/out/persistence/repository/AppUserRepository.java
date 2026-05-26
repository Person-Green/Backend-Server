package bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.repository;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.entity.AppUserEntity;
import bssm.plantshuman.peopleandgreen.auth.domain.model.OAuthProvider;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUserEntity, Long> {

    Optional<AppUserEntity> findByOauthProviderAndOauthProviderUserId(OAuthProvider oauthProvider, String oauthProviderUserId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(value = """
            INSERT INTO app_user (
                oauth_provider,
                oauth_provider_user_id,
                email,
                name,
                profile_image_url
            )
            VALUES (
                :oauthProvider,
                :oauthProviderUserId,
                :email,
                :name,
                :profileImageUrl
            )
            ON DUPLICATE KEY UPDATE
                email = VALUES(email),
                profile_image_url = VALUES(profile_image_url)
            """, nativeQuery = true)
    int upsertOAuthUser(
            @Param("oauthProvider") String oauthProvider,
            @Param("oauthProviderUserId") String oauthProviderUserId,
            @Param("email") String email,
            @Param("name") String name,
            @Param("profileImageUrl") String profileImageUrl
    );
}
