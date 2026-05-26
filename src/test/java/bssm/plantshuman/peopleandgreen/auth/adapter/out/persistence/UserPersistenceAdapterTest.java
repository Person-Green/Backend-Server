package bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.entity.AppUserEntity;
import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.repository.AppUserRepository;
import bssm.plantshuman.peopleandgreen.auth.domain.model.AppUserUpsertResult;
import bssm.plantshuman.peopleandgreen.auth.domain.model.GoogleUserInfo;
import bssm.plantshuman.peopleandgreen.auth.domain.model.OAuthProvider;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserPersistenceAdapterTest {

    @Test
    void reportsExistingUserEvenWhenUpsertAffectedRowsLooksLikeInsert() {
        AppUserRepository repository = mock(AppUserRepository.class);
        UserPersistenceAdapter adapter = new UserPersistenceAdapter(repository);
        GoogleUserInfo userInfo = new GoogleUserInfo(
                "google-user-id",
                "jjm@example.com",
                "jjm",
                "https://example.com/profile.png"
        );
        AppUserEntity existingUser = new AppUserEntity(
                OAuthProvider.GOOGLE,
                userInfo.providerUserId(),
                userInfo.email(),
                userInfo.name(),
                userInfo.profileImageUrl()
        );
        when(repository.findByOauthProviderAndOauthProviderUserId(OAuthProvider.GOOGLE, userInfo.providerUserId()))
                .thenReturn(Optional.of(existingUser));

        AppUserUpsertResult result = adapter.upsertGoogleUser(userInfo);

        assertEquals(false, result.created());
    }
}
