package bssm.plantshuman.peopleandgreen.recommendation.domain.service;

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
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantRecommendation;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationPolicy;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.TemperatureBand;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.UserProfile;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

public class PlantRecommendationEngine {

    private final RepresentativeEnvironmentResolver representativeEnvironmentResolver;
    private final SecondaryEnvironmentTagResolver secondaryEnvironmentTagResolver;
    private final RecommendationPolicy recommendationPolicy;

    public PlantRecommendationEngine(
            RepresentativeEnvironmentResolver representativeEnvironmentResolver,
            SecondaryEnvironmentTagResolver secondaryEnvironmentTagResolver,
            RecommendationPolicy recommendationPolicy
    ) {
        this.representativeEnvironmentResolver = representativeEnvironmentResolver;
        this.secondaryEnvironmentTagResolver = secondaryEnvironmentTagResolver;
        this.recommendationPolicy = recommendationPolicy;
    }

    public List<PlantRecommendation> recommend(UserProfile userProfile, List<PlantCatalogItem> catalog) {
        EnvironmentType representativeEnvironment =
                representativeEnvironmentResolver.resolve(userProfile.environment());
        List<EnvironmentType> secondaryTags =
                secondaryEnvironmentTagResolver.resolve(userProfile.environment(), representativeEnvironment);

        return catalog.stream()
                .filter(plant -> passesHardFilter(userProfile, plant))
                .map(plant -> toRecommendation(plant, userProfile, representativeEnvironment, secondaryTags))
                .sorted(Comparator.comparingInt(PlantRecommendation::score).reversed())
                .toList();
    }

    private boolean passesHardFilter(UserProfile userProfile, PlantCatalogItem plant) {
        if (userProfile.hasPet() && plant.petSafety() == PetSafetyLevel.TOXIC) {
            return false;
        }
        if (userProfile.environment().sunlight() == SunlightLevel.LOW
                && plant.condition().sunlightLevel() == SunlightLevel.HIGH) {
            return false;
        }
        if (userProfile.environment().ventilation() == VentilationLevel.LOW
                && plant.condition().ventilationNeed() == VentilationLevel.HIGH) {
            return false;
        }
        IntRange temperatureBand = temperatureRange(userProfile.environment().temperature());
        if (distanceBetween(temperatureBand.min(), temperatureBand.max(),
                plant.condition().tempMin(), plant.condition().tempMax()) > recommendationPolicy.temperatureHardMargin()) {
            return false;
        }
        IntRange humidityBand = humidityRange(userProfile.environment().humidity());
        return distanceBetween(humidityBand.min(), humidityBand.max(),
                plant.condition().humidityMin(), plant.condition().humidityMax()) <= recommendationPolicy.humidityHardMargin();
    }

    private PlantRecommendation toRecommendation(
            PlantCatalogItem plant,
            UserProfile userProfile,
            EnvironmentType representativeEnvironment,
            List<EnvironmentType> secondaryTags
    ) {
        int sunlightScore = axisScore(userProfile.environment().sunlight().ordinal(),
                plant.condition().sunlightLevel().ordinal(),
                recommendationPolicy.sunlightExactScore(),
                recommendationPolicy.sunlightAdjacentScore());
        int ventilationScore = axisScore(userProfile.environment().ventilation().ordinal(),
                plant.condition().ventilationNeed().ordinal(),
                recommendationPolicy.ventilationExactScore(),
                recommendationPolicy.ventilationAdjacentScore());
        int temperatureScore = rangeBandScore(
                temperatureRange(userProfile.environment().temperature()),
                plant.condition().tempMin(),
                plant.condition().tempMax(),
                recommendationPolicy.temperatureExactScore(),
                recommendationPolicy.temperatureSoftMargin()
        );
        int humidityScore = rangeBandScore(
                humidityRange(userProfile.environment().humidity()),
                plant.condition().humidityMin(),
                plant.condition().humidityMax(),
                recommendationPolicy.humidityExactScore(),
                recommendationPolicy.humiditySoftMargin()
        );
        int difficultyScore = difficultyScore(userProfile.careLevel(), userProfile.experienceLevel(), plant.difficulty());
        int waterScore = waterScore(userProfile.careLevel(),
                (plant.condition().waterCycleMinDays() + plant.condition().waterCycleMaxDays()) / 2);
        int placementScore = plant.condition().supportedPlacements().contains(userProfile.placement()) ? 5 : 0;
        int envScore = environmentBonus(plant.environmentFits(), representativeEnvironment, secondaryTags);
        int petPenalty = petPenalty(userProfile, plant);
        int totalScore = sunlightScore + ventilationScore + temperatureScore + humidityScore
                + difficultyScore + waterScore + placementScore + envScore - petPenalty;

        return new PlantRecommendation(
                plant.plantId(),
                plant.nameKo(),
                plant.nameEn(),
                totalScore,
                buildReasons(plant, userProfile, representativeEnvironment, placementScore, difficultyScore),
                buildCautions(plant, userProfile),
                representativeEnvironment,
                secondaryTags,
                plant.airPurificationLevel(),
                plant.petSafety(),
                plant.difficulty(),
                plant.sizeCategory(),
                plant.condition().supportedPlacements().stream().toList(),
                plant.oneLineDescription()
        );
    }

    private int axisScore(int userLevel, int plantLevel, int exactScore, int adjacentScore) {
        int distance = Math.abs(userLevel - plantLevel);
        if (distance == 0) {
            return exactScore;
        }
        if (distance == 1) {
            return adjacentScore;
        }
        return 0;
    }

    private int rangeBandScore(IntRange band, int min, int max, int maxScore, int softMargin) {
        if (distanceBetween(band.min(), band.max(), min, max) == 0) {
            return maxScore;
        }
        if (distanceBetween(band.min(), band.max(), min, max) <= softMargin) {
            return maxScore / 2;
        }
        return 0;
    }

    private int difficultyScore(CareLevel careLevel, ExperienceLevel experienceLevel, DifficultyLevel difficultyLevel) {
        int userCapacity = careLevel.ordinal() + experienceLevel.ordinal();
        int requiredCapacity = difficultyLevel.ordinal();
        int gap = requiredCapacity - userCapacity;
        if (gap <= 0) {
            return 10;
        }
        if (gap == 1) {
            return 6;
        }
        if (gap == 2) {
            return 3;
        }
        return 0;
    }

    private int waterScore(CareLevel careLevel, int averageDays) {
        return switch (careLevel) {
            case LOW -> averageDays >= 14 ? 5 : averageDays >= 7 ? 3 : 1;
            case MEDIUM -> averageDays >= 7 && averageDays <= 14 ? 5 : averageDays > 14 ? 4 : 3;
            case HIGH -> averageDays <= 7 ? 5 : averageDays <= 14 ? 4 : 3;
        };
    }

    private int environmentBonus(
            List<EnvironmentFit> environmentFits,
            EnvironmentType representativeEnvironment,
            List<EnvironmentType> secondaryTags
    ) {
        LinkedHashSet<EnvironmentType> activeTags = new LinkedHashSet<>();
        activeTags.add(representativeEnvironment);
        activeTags.addAll(secondaryTags);

        int bonus = 0;
        for (EnvironmentFit fit : environmentFits) {
            if (activeTags.contains(fit.environmentType())) {
                bonus += fit.fitLevel() == FitLevel.OPTIMAL ? 3 : 1;
            }
        }
        return Math.min(bonus, 5);
    }

    private int petPenalty(UserProfile userProfile, PlantCatalogItem plant) {
        if (!userProfile.hasPet()) {
            return 0;
        }
        return switch (plant.petSafety()) {
            case SAFE -> 0;
            case CAUTION -> recommendationPolicy.petCautionPenalty();
            case TOXIC -> recommendationPolicy.petToxicPenalty();
        };
    }

    private List<String> buildReasons(
            PlantCatalogItem plant,
            UserProfile userProfile,
            EnvironmentType representativeEnvironment,
            int placementScore,
            int difficultyScore
    ) {
        List<String> reasons = new ArrayList<>();
        if (plant.condition().sunlightLevel() == userProfile.environment().sunlight()) {
            reasons.add("현재 햇빛 환경에 잘 맞아요.");
        }
        if (isHumidityInside(userProfile, plant)) {
            reasons.add("현재 습도 조건에 잘 적응할 가능성이 높아요.");
        }
        if (placementScore == 5) {
            reasons.add(locationReason(userProfile.placement()));
        }
        if (difficultyScore >= 6) {
            reasons.add("관리 난이도가 현재 관리 가능 수준에 맞아요.");
        }
        if (plant.environmentFits().stream()
                .anyMatch(fit -> fit.environmentType() == representativeEnvironment && fit.fitLevel() == FitLevel.OPTIMAL)) {
            reasons.add("현재 대표 환경 유형에 최적화된 식물이에요.");
        }
        return reasons.stream().distinct().limit(4).toList();
    }

    private boolean isHumidityInside(UserProfile userProfile, PlantCatalogItem plant) {
        IntRange humidityBand = humidityRange(userProfile.environment().humidity());
        return distanceBetween(humidityBand.min(), humidityBand.max(),
                plant.condition().humidityMin(), plant.condition().humidityMax()) == 0;
    }

    private List<String> buildCautions(PlantCatalogItem plant, UserProfile userProfile) {
        List<String> cautions = new ArrayList<>();
        if (userProfile.hasPet() && plant.petSafety() == PetSafetyLevel.CAUTION) {
            cautions.add("반려동물이 잎을 씹지 않도록 주의해 주세요.");
        }
        IntRange humidityBand = humidityRange(userProfile.environment().humidity());
        IntRange temperatureBand = temperatureRange(userProfile.environment().temperature());
        if (humidityBand.max() < plant.condition().humidityMin()) {
            cautions.add("건조해지면 잎 끝이 마를 수 있어요.");
        }
        if (humidityBand.min() > plant.condition().humidityMax()) {
            cautions.add("과습에 주의해 주세요.");
        }
        if (temperatureBand.max() < plant.condition().tempMin()) {
            cautions.add("저온에 오래 노출되지 않도록 관리해 주세요.");
        }
        if (temperatureBand.min() > plant.condition().tempMax()) {
            cautions.add("고온이 지속되면 잎 상태가 약해질 수 있어요.");
        }
        return cautions.stream().distinct().limit(2).toList();
    }

    private String locationReason(PlacementType placementType) {
        return switch (placementType) {
            case DESK -> "책상 배치와 잘 맞는 크기와 관리 난이도를 갖고 있어요.";
            case WINDOW -> "창가 배치에 적합한 광량 조건을 갖고 있어요.";
            case LIVING_ROOM -> "거실처럼 시야가 트인 공간에 두기 좋아요.";
            case BATHROOM -> "욕실처럼 습도가 높은 공간에 배치하기 좋아요.";
            case KITCHEN -> "주방처럼 생활 동선이 있는 공간에도 무리가 적어요.";
            case BEDROOM -> "침실 배치에 적합한 실내 적응력을 갖고 있어요.";
            case BALCONY -> "발코니처럼 빛과 통풍이 있는 공간에 적합해요.";
            case OFFICE -> "사무실처럼 실내 체류 시간이 긴 공간에 배치하기 좋아요.";
        };
    }

    private IntRange temperatureRange(TemperatureBand band) {
        return switch (band) {
            case LOW -> new IntRange(0, 15);
            case NORMAL -> new IntRange(16, 24);
            case HIGH -> new IntRange(25, 40);
        };
    }

    private IntRange humidityRange(HumidityBand band) {
        return switch (band) {
            case LOW -> new IntRange(0, 40);
            case NORMAL -> new IntRange(41, 59);
            case HIGH -> new IntRange(60, 100);
        };
    }

    private int distanceBetween(int leftMin, int leftMax, int rightMin, int rightMax) {
        if (leftMax < rightMin) {
            return rightMin - leftMax;
        }
        if (rightMax < leftMin) {
            return leftMin - rightMax;
        }
        return 0;
    }

    private record IntRange(int min, int max) {
    }
}
