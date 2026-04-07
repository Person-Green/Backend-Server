package bssm.plantshuman.peopleandgreen.catalog.adapter.out.persistence.entity;

import bssm.plantshuman.peopleandgreen.auth.adapter.out.persistence.entity.AppUserEntity;
import bssm.plantshuman.peopleandgreen.domain.plant.Plant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "favorite_plant",
        uniqueConstraints = @UniqueConstraint(name = "uk_favorite_user_plant", columnNames = {"user_id", "plant_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoritePlantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public FavoritePlantEntity(AppUserEntity user, Plant plant, Instant createdAt) {
        this.user = user;
        this.plant = plant;
        this.createdAt = createdAt;
    }
}
