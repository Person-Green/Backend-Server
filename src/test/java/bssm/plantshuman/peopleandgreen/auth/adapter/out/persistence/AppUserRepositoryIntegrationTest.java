package bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.entity.AppUserEntity;
import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.repository.AppUserRepository;
import bssm.plantshuman.peopleandgreen.auth.domain.model.OAuthProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:authdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class AppUserRepositoryIntegrationTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void upsertsExistingOAuthUserWithoutCreatingDuplicateUser() {
        appUserRepository.upsertOAuthUser(
                OAuthProvider.GOOGLE.name(),
                "google-user-id",
                "before@example.com",
                "before",
                "https://example.com/before.png"
        );
        appUserRepository.upsertOAuthUser(
                OAuthProvider.GOOGLE.name(),
                "google-user-id",
                "after@example.com",
                "after",
                "https://example.com/after.png"
        );

        AppUserEntity user = appUserRepository.findByOauthProviderAndOauthProviderUserId(
                OAuthProvider.GOOGLE,
                "google-user-id"
        ).orElseThrow();

        assertEquals(1L, appUserRepository.count());
        assertEquals("after@example.com", user.getEmail());
        assertEquals("before", user.getName());
        assertEquals("https://example.com/after.png", user.getProfileImageUrl());
    }
}
