package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

public record RecommendationPolicy(
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
        int petCautionPenalty,
        int petToxicPenalty
) {
}
