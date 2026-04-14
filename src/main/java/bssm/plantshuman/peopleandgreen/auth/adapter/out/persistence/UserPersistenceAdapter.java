package bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.entity.AppUserEntity;
import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.repository.AppUserRepository;
import bssm.plantshuman.peopleandgreen.auth.application.port.out.UserAccountPort;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUser;
import bssm.plantshuman.peopleandgreen.auth.domain.model.GoogleUserInfo;
import bssm.plantshuman.peopleandgreen.auth.domain.model.OAuthProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserAccountPort {

    private final AppUserRepository appUserRepository;

    public UserPersistenceAdapter(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    @Transactional
    public AppUser upsertGoogleUser(GoogleUserInfo userInfo) {
        AppUserEntity entity = appUserRepository.findByOauthProviderAndOauthProviderUserId(OAuthProvider.GOOGLE, userInfo.providerUserId())
                .map(existing -> {
                    existing.updateGoogleProfile(userInfo.email(), userInfo.profileImageUrl());
                    return existing;
                })
                .orElseGet(() -> new AppUserEntity(
                        OAuthProvider.GOOGLE,
                        userInfo.providerUserId(),
                        userInfo.email(),
                        userInfo.name(),
                        userInfo.profileImageUrl()
                ));

        return toDomain(appUserRepository.save(entity));
    }

    @Override
    public Optional<AppUser> findById(Long userId) {
        return appUserRepository.findById(userId).map(this::toDomain);
    }

    @Override
    @Transactional
    public AppUser updateUsername(Long userId, String username) {
        AppUserEntity entity = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        entity.updateUsername(username);
        return toDomain(entity);
    }

    public AppUserEntity getReference(Long userId) {
        return appUserRepository.getReferenceById(userId);
    }

    private AppUser toDomain(AppUserEntity entity) {
        return new AppUser(
                entity.getId(),
                entity.getOauthProvider(),
                entity.getOauthProviderUserId(),
                entity.getEmail(),
                entity.getName(),
                entity.getProfileImageUrl()
        );
    }
}
