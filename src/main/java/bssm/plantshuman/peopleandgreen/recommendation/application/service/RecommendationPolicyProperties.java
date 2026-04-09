package bssm.plantshuman.peopleandgreen.recommendation.application.service;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "recommendation.policy")
public record RecommendationPolicyProperties(
        int sunlightExactScore,
        int sunlightAdjacentScore,
        int ventilationExactScore,
        int ventilationAdjacentScore,
        int temperatureExactScore,
        int temperatureSoftMargin,
        int temperatureHardMargin,
        int humidityExactScore,
        int humiditySoftMargin,
        int humidityHardMargin,
        int lowCareLongCycleDays,
        int lowCareMediumCycleDays,
        int lowCareLongCycleScore,
        int lowCareMediumCycleScore,
        int lowCareShortCycleScore,
        int mediumCareMinCycleDays,
        int mediumCareMaxCycleDays,
        int mediumCareIdealScore,
        int mediumCareLongCycleScore,
        int mediumCareShortCycleScore,
        int highCareShortCycleDays,
        int highCareMediumCycleDays,
        int highCareShortCycleScore,
        int highCareMediumCycleScore,
        int highCareLongCycleScore,
        int petCautionPenalty,
        int petToxicPenalty,
        int maxRecommendations
) {

    public RecommendationPolicy toPolicy() {
        return new RecommendationPolicy(
                sunlightExactScore,
                sunlightAdjacentScore,
                ventilationExactScore,
                ventilationAdjacentScore,
                temperatureExactScore,
                temperatureSoftMargin,
                temperatureHardMargin,
                humidityExactScore,
                humiditySoftMargin,
                humidityHardMargin,
                lowCareLongCycleDays,
                lowCareMediumCycleDays,
                lowCareLongCycleScore,
                lowCareMediumCycleScore,
                lowCareShortCycleScore,
                mediumCareMinCycleDays,
                mediumCareMaxCycleDays,
                mediumCareIdealScore,
                mediumCareLongCycleScore,
                mediumCareShortCycleScore,
                highCareShortCycleDays,
                highCareMediumCycleDays,
                highCareShortCycleScore,
                highCareMediumCycleScore,
                highCareLongCycleScore,
                petCautionPenalty,
                petToxicPenalty
        );
    }
}
