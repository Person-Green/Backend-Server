package bssm.plantshuman.peopleandgreen.recommendation.domain.model;

import java.util.List;

public record PlantCatalogItem(
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
        String recommendedLocationText,
        PlantCondition condition,
        List<EnvironmentFit> environmentFits
) {
}
