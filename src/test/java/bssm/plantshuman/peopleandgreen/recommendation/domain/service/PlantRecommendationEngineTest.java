package bssm.plantshuman.peopleandgreen.recommendation.domain.service;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.AirPurificationLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.CareLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.DifficultyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentFit;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.EnvironmentType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.ExperienceLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.FitLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.HumidityBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PetSafetyLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlacementType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantCatalogItem;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantCondition;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantRecommendation;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationPolicy;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SizeCategory;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.UserEnvironment;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.UserProfile;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantRecommendationEngineTest {

    private final PlantRecommendationEngine engine = new PlantRecommendationEngine(
            new RepresentativeEnvironmentResolver(),
            new SecondaryEnvironmentTagResolver(),
            new RecommendationPolicy(30, 15, 10, 5, 20, 3, 7, 15, 10, 20, 18, 100)
    );

    @Test
    void filtersOutPlantsThatNeedHighLightInDarkRoom() {
        UserProfile userProfile = new UserProfile(
                new UserEnvironment(
                        SunlightLevel.LOW,
                        VentilationLevel.NORMAL,
                        TemperatureBand.NORMAL,
                        HumidityBand.NORMAL
                ),
                CareLevel.MEDIUM,
                ExperienceLevel.BEGINNER,
                false,
                PlacementType.DESK
        );

        List<PlantRecommendation> recommendations = engine.recommend(
                userProfile,
                List.of(highLightPlant(), lowLightPlant())
        );

        assertEquals(1, recommendations.size());
        assertEquals("PLT-LOW", recommendations.getFirst().plantId());
    }

    @Test
    void prefersEasierPlantForBeginnerInSameEnvironment() {
        UserProfile userProfile = new UserProfile(
                new UserEnvironment(
                        SunlightLevel.MEDIUM,
                        VentilationLevel.NORMAL,
                        TemperatureBand.NORMAL,
                        HumidityBand.HIGH
                ),
                CareLevel.LOW,
                ExperienceLevel.BEGINNER,
                false,
                PlacementType.BATHROOM
        );

        List<PlantRecommendation> recommendations = engine.recommend(
                userProfile,
                List.of(easyBathroomPlant(), hardBathroomPlant())
        );

        assertEquals(2, recommendations.size());
        assertEquals("PLT-EASY", recommendations.get(0).plantId());
        assertTrue(recommendations.get(0).score() > recommendations.get(1).score());
    }

    @Test
    void includesReasonsAndCautionsBasedOnMatchedConditions() {
        UserProfile userProfile = new UserProfile(
                new UserEnvironment(
                        SunlightLevel.MEDIUM,
                        VentilationLevel.NORMAL,
                        TemperatureBand.NORMAL,
                        HumidityBand.HIGH
                ),
                CareLevel.LOW,
                ExperienceLevel.BEGINNER,
                true,
                PlacementType.BATHROOM
        );

        PlantRecommendation recommendation = engine.recommend(
                userProfile,
                List.of(easyBathroomPlant())
        ).getFirst();

        assertFalse(recommendation.reasons().isEmpty());
        assertTrue(recommendation.reasons().stream().anyMatch(reason -> reason.contains("습도")));
        assertTrue(recommendation.cautions().stream().anyMatch(caution -> caution.contains("반려동물")));
    }

    @Test
    void stronglyPrefersSafePlantWhenUserHasPet() {
        UserProfile userProfile = new UserProfile(
                new UserEnvironment(
                        SunlightLevel.MEDIUM,
                        VentilationLevel.NORMAL,
                        TemperatureBand.NORMAL,
                        HumidityBand.NORMAL
                ),
                CareLevel.MEDIUM,
                ExperienceLevel.INTERMEDIATE,
                true,
                PlacementType.LIVING_ROOM
        );

        List<PlantRecommendation> recommendations = engine.recommend(
                userProfile,
                List.of(cautionLivingRoomPlant(), safeLivingRoomPlant())
        );

        assertEquals("PLT-SAFE", recommendations.get(0).plantId());
        assertTrue(recommendations.get(0).score() > recommendations.get(1).score());
    }

    @Test
    void treatsEntireBandAsEligibleWhenTemperatureRangeOverlaps() {
        UserProfile userProfile = new UserProfile(
                new UserEnvironment(
                        SunlightLevel.MEDIUM,
                        VentilationLevel.NORMAL,
                        TemperatureBand.NORMAL,
                        HumidityBand.NORMAL
                ),
                CareLevel.MEDIUM,
                ExperienceLevel.INTERMEDIATE,
                false,
                PlacementType.LIVING_ROOM
        );

        List<PlantRecommendation> recommendations = engine.recommend(
                userProfile,
                List.of(coolNormalBandPlant(), warmNormalBandPlant())
        );

        int firstScore = recommendations.stream()
                .filter(recommendation -> recommendation.plantId().equals("PLT-COOL"))
                .findFirst()
                .orElseThrow()
                .score();
        int secondScore = recommendations.stream()
                .filter(recommendation -> recommendation.plantId().equals("PLT-WARM"))
                .findFirst()
                .orElseThrow()
                .score();

        assertEquals(firstScore, secondScore);
    }

    private PlantCatalogItem highLightPlant() {
        return new PlantCatalogItem(
                "PLT-HIGH",
                "선인장",
                "Cactus",
                DifficultyLevel.VERY_EASY,
                PetSafetyLevel.SAFE,
                AirPurificationLevel.MEDIUM,
                SizeCategory.SMALL,
                "3주 1회",
                "15~35°C",
                "30~50%",
                "직사광",
                "강한 햇빛이 필요한 식물",
                "창가",
                new PlantCondition(
                        SunlightLevel.HIGH,
                        VentilationLevel.NORMAL,
                        15,
                        35,
                        30,
                        50,
                        21,
                        28,
                        Set.of(PlacementType.WINDOW)
                ),
                List.of(new EnvironmentFit(EnvironmentType.ENV_01_SUNNY, FitLevel.OPTIMAL))
        );
    }

    private PlantCatalogItem lowLightPlant() {
        return new PlantCatalogItem(
                "PLT-LOW",
                "스킨답서스",
                "Pothos",
                DifficultyLevel.VERY_EASY,
                PetSafetyLevel.SAFE,
                AirPurificationLevel.HIGH,
                SizeCategory.MEDIUM,
                "1주 1회",
                "15~25°C",
                "40~60%",
                "낮은 빛",
                "어두운 공간에 적합한 식물",
                "책상",
                new PlantCondition(
                        SunlightLevel.LOW,
                        VentilationLevel.LOW,
                        15,
                        25,
                        40,
                        60,
                        7,
                        14,
                        Set.of(PlacementType.DESK, PlacementType.BEDROOM)
                ),
                List.of(new EnvironmentFit(EnvironmentType.ENV_02_DARK, FitLevel.OPTIMAL))
        );
    }

    private PlantCatalogItem easyBathroomPlant() {
        return new PlantCatalogItem(
                "PLT-EASY",
                "틸란드시아",
                "Tillandsia",
                DifficultyLevel.EASY,
                PetSafetyLevel.CAUTION,
                AirPurificationLevel.MEDIUM,
                SizeCategory.SMALL,
                "주 2회 분무",
                "15~30°C",
                "60~80%",
                "간접광",
                "습한 공간에 잘 적응하는 식물",
                "욕실",
                new PlantCondition(
                        SunlightLevel.MEDIUM,
                        VentilationLevel.NORMAL,
                        15,
                        30,
                        60,
                        80,
                        2,
                        3,
                        Set.of(PlacementType.BATHROOM, PlacementType.KITCHEN)
                ),
                List.of(new EnvironmentFit(EnvironmentType.ENV_07_HUMID, FitLevel.OPTIMAL))
        );
    }

    private PlantCatalogItem hardBathroomPlant() {
        return new PlantCatalogItem(
                "PLT-HARD",
                "칼라테아",
                "Calathea",
                DifficultyLevel.HARD,
                PetSafetyLevel.SAFE,
                AirPurificationLevel.MEDIUM,
                SizeCategory.MEDIUM,
                "주 1회",
                "18~24°C",
                "60~80%",
                "간접광",
                "관리 난도가 높은 습도 선호 식물",
                "거실",
                new PlantCondition(
                        SunlightLevel.MEDIUM,
                        VentilationLevel.NORMAL,
                        18,
                        24,
                        60,
                        80,
                        7,
                        7,
                        Set.of(PlacementType.LIVING_ROOM, PlacementType.BEDROOM)
                ),
                List.of(new EnvironmentFit(EnvironmentType.ENV_07_HUMID, FitLevel.OPTIMAL))
        );
    }

    private PlantCatalogItem cautionLivingRoomPlant() {
        return new PlantCatalogItem(
                "PLT-CAUTION",
                "고무나무",
                "Rubber Plant",
                DifficultyLevel.EASY,
                PetSafetyLevel.CAUTION,
                AirPurificationLevel.HIGH,
                SizeCategory.LARGE,
                "1~2주 1회",
                "18~28°C",
                "40~70%",
                "간접광",
                "반려동물 주의가 필요한 식물",
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
                List.of(new EnvironmentFit(EnvironmentType.ENV_03_BRIGHT_INDIRECT, FitLevel.OPTIMAL))
        );
    }

    private PlantCatalogItem safeLivingRoomPlant() {
        return new PlantCatalogItem(
                "PLT-SAFE",
                "관음죽",
                "Lady Palm",
                DifficultyLevel.EASY,
                PetSafetyLevel.SAFE,
                AirPurificationLevel.HIGH,
                SizeCategory.LARGE,
                "1~2주 1회",
                "18~28°C",
                "40~70%",
                "간접광",
                "반려동물과 함께 두기 쉬운 식물",
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
                List.of(new EnvironmentFit(EnvironmentType.ENV_03_BRIGHT_INDIRECT, FitLevel.OPTIMAL))
        );
    }

    private PlantCatalogItem coolNormalBandPlant() {
        return baseNormalBandPlant("PLT-COOL", 16, 16);
    }

    private PlantCatalogItem warmNormalBandPlant() {
        return baseNormalBandPlant("PLT-WARM", 24, 24);
    }

    private PlantCatalogItem baseNormalBandPlant(String plantId, int tempMin, int tempMax) {
        return new PlantCatalogItem(
                plantId,
                plantId,
                plantId,
                DifficultyLevel.EASY,
                PetSafetyLevel.SAFE,
                AirPurificationLevel.MEDIUM,
                SizeCategory.MEDIUM,
                "1주 1회",
                tempMin + "~" + tempMax + "°C",
                "40~60%",
                "간접광",
                "온도 밴드 테스트용 식물",
                "거실",
                new PlantCondition(
                        SunlightLevel.MEDIUM,
                        VentilationLevel.NORMAL,
                        tempMin,
                        tempMax,
                        40,
                        60,
                        7,
                        10,
                        Set.of(PlacementType.LIVING_ROOM)
                ),
                List.of(new EnvironmentFit(EnvironmentType.ENV_03_BRIGHT_INDIRECT, FitLevel.OPTIMAL))
        );
    }
}
