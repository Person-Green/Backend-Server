package bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.entity;

import bssm.plantshuman.peopleandgreen.auth.domain.model.OAuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "app_user",
        uniqueConstraints = @UniqueConstraint(name = "uk_app_user_oauth_identity", columnNames = {"oauth_provider", "oauth_provider_user_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", nullable = false, length = 20)
    private OAuthProvider oauthProvider;

    @Column(name = "oauth_provider_user_id", nullable = false, length = 100)
    private String oauthProviderUserId;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    public AppUserEntity(
            OAuthProvider oauthProvider,
            String oauthProviderUserId,
            String email,
            String name,
            String profileImageUrl
    ) {
        this.oauthProvider = oauthProvider;
        this.oauthProviderUserId = oauthProviderUserId;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateProfile(String email, String name, String profileImageUrl) {
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }
}
