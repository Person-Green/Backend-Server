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

    private static final int SUNLIGHT_EXACT_SCORE = 30;
    private static final int SUNLIGHT_ADJACENT_SCORE = 15;
    private static final int VENTILATION_EXACT_SCORE = 10;
    private static final int VENTILATION_ADJACENT_SCORE = 5;
    private static final int TEMPERATURE_EXACT_SCORE = 20;
    private static final int TEMPERATURE_SOFT_MARGIN = 3;
    private static final int TEMPERATURE_HARD_MARGIN = 7;
    private static final int HUMIDITY_EXACT_SCORE = 15;
    private static final int HUMIDITY_SOFT_MARGIN = 10;
    private static final int HUMIDITY_HARD_MARGIN = 20;
    private static final int LOW_CARE_LONG_CYCLE_DAYS = 14;
    private static final int LOW_CARE_MEDIUM_CYCLE_DAYS = 7;
    private static final int LOW_CARE_LONG_CYCLE_SCORE = 5;
    private static final int LOW_CARE_MEDIUM_CYCLE_SCORE = 3;
    private static final int LOW_CARE_SHORT_CYCLE_SCORE = 1;
    private static final int MEDIUM_CARE_MIN_CYCLE_DAYS = 7;
    private static final int MEDIUM_CARE_MAX_CYCLE_DAYS = 14;
    private static final int MEDIUM_CARE_IDEAL_SCORE = 5;
    private static final int MEDIUM_CARE_LONG_CYCLE_SCORE = 4;
    private static final int MEDIUM_CARE_SHORT_CYCLE_SCORE = 3;
    private static final int HIGH_CARE_SHORT_CYCLE_DAYS = 7;
    private static final int HIGH_CARE_MEDIUM_CYCLE_DAYS = 14;
    private static final int HIGH_CARE_SHORT_CYCLE_SCORE = 5;
    private static final int HIGH_CARE_MEDIUM_CYCLE_SCORE = 4;
    private static final int HIGH_CARE_LONG_CYCLE_SCORE = 3;
    private static final int PET_CAUTION_PENALTY = 18;
    private static final int PET_TOXIC_PENALTY = 100;

    private final PlantRecommendationEngine engine = new PlantRecommendationEngine(
            new RepresentativeEnvironmentResolver(),
            new SecondaryEnvironmentTagResolver(),
            new RecommendationPolicy(
                    SUNLIGHT_EXACT_SCORE,
                    SUNLIGHT_ADJACENT_SCORE,
                    VENTILATION_EXACT_SCORE,
                    VENTILATION_ADJACENT_SCORE,
                    TEMPERATURE_EXACT_SCORE,
                    TEMPERATURE_SOFT_MARGIN,
                    TEMPERATURE_HARD_MARGIN,
                    HUMIDITY_EXACT_SCORE,
                    HUMIDITY_SOFT_MARGIN,
                    HUMIDITY_HARD_MARGIN,
                    LOW_CARE_LONG_CYCLE_DAYS,
                    LOW_CARE_MEDIUM_CYCLE_DAYS,
                    LOW_CARE_LONG_CYCLE_SCORE,
                    LOW_CARE_MEDIUM_CYCLE_SCORE,
                    LOW_CARE_SHORT_CYCLE_SCORE,
                    MEDIUM_CARE_MIN_CYCLE_DAYS,
                    MEDIUM_CARE_MAX_CYCLE_DAYS,
                    MEDIUM_CARE_IDEAL_SCORE,
                    MEDIUM_CARE_LONG_CYCLE_SCORE,
                    MEDIUM_CARE_SHORT_CYCLE_SCORE,
                    HIGH_CARE_SHORT_CYCLE_DAYS,
                    HIGH_CARE_MEDIUM_CYCLE_DAYS,
                    HIGH_CARE_SHORT_CYCLE_SCORE,
                    HIGH_CARE_MEDIUM_CYCLE_SCORE,
                    HIGH_CARE_LONG_CYCLE_SCORE,
                    PET_CAUTION_PENALTY,
                    PET_TOXIC_PENALTY
            )
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

    @Test
    void ordersEqualScoresByPlantIdForStableResults() {
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
                List.of(stablePlant("PLT-B"), stablePlant("PLT-A"))
        );

        assertEquals(List.of("PLT-A", "PLT-B"), recommendations.stream()
                .map(PlantRecommendation::plantId)
                .toList());
    }

    @Test
    void returnsAllTriggeredCautionsInPriorityOrder() {
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

        PlantRecommendation recommendation = engine.recommend(
                userProfile,
                List.of(cautionPriorityPlant())
        ).getFirst();

        assertEquals(List.of(
                "반려동물이 잎을 씹지 않도록 주의해 주세요.",
                "저온에 오래 노출되지 않도록 관리해 주세요.",
                "건조해지면 잎 끝이 마를 수 있어요."
        ), recommendation.cautions());
    }

    @Test
    void ordersReasonsByContributionWeight() {
        UserProfile userProfile = new UserProfile(
                new UserEnvironment(
                        SunlightLevel.HIGH,
                        VentilationLevel.NORMAL,
                        TemperatureBand.NORMAL,
                        HumidityBand.NORMAL
                ),
                CareLevel.HIGH,
                ExperienceLevel.ADVANCED,
                false,
                PlacementType.DESK
        );

        PlantRecommendation recommendation = engine.recommend(
                userProfile,
                List.of(reasonPriorityPlant())
        ).getFirst();

        assertEquals(List.of(
                "현재 햇빛 환경에 잘 맞아요.",
                "현재 습도 조건에 잘 적응할 가능성이 높아요.",
                "관리 난이도가 현재 관리 가능 수준에 맞아요.",
                "책상 배치와 잘 맞는 크기와 관리 난이도를 갖고 있어요."
        ), recommendation.reasons());
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

    private PlantCatalogItem stablePlant(String plantId) {
        return new PlantCatalogItem(
                plantId,
                plantId,
                plantId,
                DifficultyLevel.EASY,
                PetSafetyLevel.SAFE,
                AirPurificationLevel.MEDIUM,
                SizeCategory.MEDIUM,
                "1주 1회",
                "18~28°C",
                "40~60%",
                "간접광",
                "동점 정렬 테스트용 식물",
                "거실",
                new PlantCondition(
                        SunlightLevel.MEDIUM,
                        VentilationLevel.NORMAL,
                        18,
                        28,
                        40,
                        60,
                        7,
                        14,
                        Set.of(PlacementType.LIVING_ROOM)
                ),
                List.of(new EnvironmentFit(EnvironmentType.ENV_03_BRIGHT_INDIRECT, FitLevel.OPTIMAL))
        );
    }

    private PlantCatalogItem cautionPriorityPlant() {
        return new PlantCatalogItem(
                "PLT-CAUTION-ORDER",
                "테스트 식물",
                "Priority Plant",
                DifficultyLevel.NORMAL,
                PetSafetyLevel.CAUTION,
                AirPurificationLevel.MEDIUM,
                SizeCategory.MEDIUM,
                "1주 1회",
                "26~30°C",
                "60~80%",
                "간접광",
                "경고 우선순위 테스트용 식물",
                "거실",
                new PlantCondition(
                        SunlightLevel.MEDIUM,
                        VentilationLevel.NORMAL,
                        26,
                        30,
                        60,
                        80,
                        7,
                        10,
                        Set.of(PlacementType.LIVING_ROOM)
                ),
                List.of(new EnvironmentFit(EnvironmentType.ENV_03_BRIGHT_INDIRECT, FitLevel.OPTIMAL))
        );
    }

    private PlantCatalogItem reasonPriorityPlant() {
        return new PlantCatalogItem(
                "PLT-REASON-ORDER",
                "이유 테스트 식물",
                "Reason Plant",
                DifficultyLevel.EASY,
                PetSafetyLevel.SAFE,
                AirPurificationLevel.MEDIUM,
                SizeCategory.SMALL,
                "1주 1회",
                "18~24°C",
                "41~59%",
                "직사광",
                "이유 우선순위 테스트용 식물",
                "책상",
                new PlantCondition(
                        SunlightLevel.HIGH,
                        VentilationLevel.NORMAL,
                        18,
                        24,
                        41,
                        59,
                        7,
                        10,
                        Set.of(PlacementType.DESK)
                ),
                List.of(new EnvironmentFit(EnvironmentType.ENV_01_SUNNY, FitLevel.OPTIMAL))
        );
    }
}
