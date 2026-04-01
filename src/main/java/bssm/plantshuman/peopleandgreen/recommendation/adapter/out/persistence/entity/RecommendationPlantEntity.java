package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.AirPurificationLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.DifficultyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PetSafetyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SizeCategory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recommendation_plant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationPlantEntity {

    @Id
    @Column(name = "plant_id", nullable = false, length = 30)
    private String plantId;

    @Column(name = "name_ko", nullable = false)
    private String nameKo;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "pet_safety", nullable = false)
    private PetSafetyLevel petSafety;

    @Enumerated(EnumType.STRING)
    @Column(name = "air_purification_level", nullable = false)
    private AirPurificationLevel airPurificationLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "size_category", nullable = false)
    private SizeCategory sizeCategory;

    @Column(name = "display_water_cycle", nullable = false)
    private String displayWaterCycle;

    @Column(name = "display_temp_range", nullable = false)
    private String displayTempRange;

    @Column(name = "display_humidity_range", nullable = false)
    private String displayHumidityRange;

    @Column(name = "display_light_requirement", nullable = false)
    private String displayLightRequirement;

    @Column(name = "one_line_description", nullable = false, length = 500)
    private String oneLineDescription;

    @Column(name = "recommended_location_text", nullable = false, length = 500)
    private String recommendedLocationText;

    @OneToOne(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private RecommendationPlantConditionEntity condition;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RecommendationPlantEnvironmentFitEntity> environmentFits = new ArrayList<>();

    public RecommendationPlantEntity(
            String plantId,
            String nameKo,
            String nameEn,
            DifficultyLevel difficulty,
            PetSafetyLevel petSafety,
            AirPurificationLevel airPurificationLevel,
            SizeCategory sizeCategory,
            String displayWaterCycle,
            String displayTempRange,
            String displayHumidityRange,
            String displayLightRequirement,
            String oneLineDescription,
            String recommendedLocationText
    ) {
        this.plantId = plantId;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.difficulty = difficulty;
        this.petSafety = petSafety;
        this.airPurificationLevel = airPurificationLevel;
        this.sizeCategory = sizeCategory;
        this.displayWaterCycle = displayWaterCycle;
        this.displayTempRange = displayTempRange;
        this.displayHumidityRange = displayHumidityRange;
        this.displayLightRequirement = displayLightRequirement;
        this.oneLineDescription = oneLineDescription;
        this.recommendedLocationText = recommendedLocationText;
    }

    public void attachCondition(RecommendationPlantConditionEntity condition) {
        this.condition = condition;
    }

    public void addEnvironmentFit(RecommendationPlantEnvironmentFitEntity environmentFit) {
        this.environmentFits.add(environmentFit);
    }
}
