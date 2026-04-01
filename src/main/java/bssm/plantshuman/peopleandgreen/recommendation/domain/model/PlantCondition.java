package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

import java.util.Set;

public record PlantCondition(
        SunlightLevel sunlightLevel,
        VentilationLevel ventilationNeed,
        int tempMin,
        int tempMax,
        int humidityMin,
        int humidityMax,
        int waterCycleMinDays,
        int waterCycleMaxDays,
        Set<PlacementType> supportedPlacements
) {
}
