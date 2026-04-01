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
        int petToxicPenalty
) {

    public int waterScore(CareLevel careLevel, int averageDays) {
        return switch (careLevel) {
            case LOW -> averageDays >= lowCareLongCycleDays
                    ? lowCareLongCycleScore
                    : averageDays >= lowCareMediumCycleDays ? lowCareMediumCycleScore : lowCareShortCycleScore;
            case MEDIUM -> averageDays >= mediumCareMinCycleDays && averageDays <= mediumCareMaxCycleDays
                    ? mediumCareIdealScore
                    : averageDays > mediumCareMaxCycleDays ? mediumCareLongCycleScore : mediumCareShortCycleScore;
            case HIGH -> averageDays <= highCareShortCycleDays
                    ? highCareShortCycleScore
                    : averageDays <= highCareMediumCycleDays ? highCareMediumCycleScore : highCareLongCycleScore;
        };
    }
}
