package bssm.plantshuman.peopleandgreen.recommendation.application.service;

import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadPlantCatalogPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.AirPurificationLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.CareLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.DifficultyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentFit;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.ExperienceLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.FitLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.HumidityBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlacementType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantCondition;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsCommand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendPlantsResult;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SizeCategory;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendPlantsServiceTest {

    @Test
    void limitsRecommendationsUsingConfiguredMax() {
        RecommendationPolicyProperties properties = new RecommendationPolicyProperties(
                30,
                15,
                10,
                5,
                20,
                3,
                7,
                15,
                10,
                20,
                14,
                7,
                5,
                3,
                1,
                7,
                14,
                5,
                4,
                3,
                7,
                14,
                5,
                4,
                3,
                18,
                100,
                1
        );

        RecommendPlantsService service = new RecommendPlantsService(
                () -> List.of(highScorePlant(), lowerScorePlant()),
                properties
        );

        RecommendPlantsResult result = service.recommend(new RecommendPlantsCommand(
                SunlightLevel.MEDIUM,
                VentilationLevel.NORMAL,
                TemperatureBand.NORMAL,
                HumidityBand.NORMAL,
                CareLevel.MEDIUM,
                ExperienceLevel.INTERMEDIATE,
                false,
                PlacementType.LIVING_ROOM
        ));

        assertEquals(EnvironmentType.ENV_03_BRIGHT_INDIRECT, result.representativeEnvironment());
        assertEquals(List.of(), result.secondaryEnvironmentTags());
        assertEquals(1, result.plants().size());
        assertEquals("PLT-HIGH", result.plants().getFirst().plantId());
    }

    private PlantCatalogItem highScorePlant() {
        return new PlantCatalogItem(
                "PLT-HIGH",
                "관음죽",
                "Lady Palm",
                DifficultyLevel.EASY,
                bssm.plantshuman.peopleandgreen.recommendation.domain.model.PetSafetyLevel.SAFE,
                AirPurificationLevel.HIGH,
                SizeCategory.LARGE,
                "1~2주 1회",
                "18~28°C",
                "40~70%",
                "간접광",
                "추천 1순위",
                "거실",
                new PlantCondition(
                        SunlightLevel.MEDIUM,
                        VentilationLevel.NORMAL,
                        18,
                        28,
                        40,
                        70,
                        7,
                        14,
                        Set.of(PlacementType.LIVING_ROOM)
                ),
                List.of(
                        new EnvironmentFit(EnvironmentType.ENV_03_BRIGHT_INDIRECT, FitLevel.OPTIMAL),
                        new EnvironmentFit(EnvironmentType.ENV_09_CLOSED, FitLevel.POSSIBLE)
                )
        );
    }

    private PlantCatalogItem lowerScorePlant() {
        return new PlantCatalogItem(
                "PLT-LOW",
                "아이비",
                "Ivy",
                DifficultyLevel.NORMAL,
                bssm.plantshuman.peopleandgreen.recommendation.domain.model.PetSafetyLevel.CAUTION,
                AirPurificationLevel.MEDIUM,
                SizeCategory.MEDIUM,
                "1주 1회",
                "10~22°C",
                "40~60%",
                "간접광",
                "추천 2순위",
                "창가",
                new PlantCondition(
                        SunlightLevel.MEDIUM,
                        VentilationLevel.HIGH,
                        10,
                        22,
                        40,
                        60,
                        7,
                        10,
                        Set.of(PlacementType.WINDOW)
                ),
                List.of(new EnvironmentFit(EnvironmentType.ENV_08_WELL_VENTILATED, FitLevel.POSSIBLE))
        );
    }
}
