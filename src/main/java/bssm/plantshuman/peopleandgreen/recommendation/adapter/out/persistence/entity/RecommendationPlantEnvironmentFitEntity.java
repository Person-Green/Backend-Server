package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.FitLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommendation_plant_env_fit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationPlantEnvironmentFitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private RecommendationPlantEntity plant;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment_type", nullable = false)
    private EnvironmentType environmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "fit_level", nullable = false)
    private FitLevel fitLevel;

    public RecommendationPlantEnvironmentFitEntity(
            RecommendationPlantEntity plant,
            EnvironmentType environmentType,
            FitLevel fitLevel
    ) {
        this.plant = plant;
        this.environmentType = environmentType;
        this.fitLevel = fitLevel;
    }
}
