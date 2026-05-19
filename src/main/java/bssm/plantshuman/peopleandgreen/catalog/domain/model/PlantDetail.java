package bssm.plantshuman.peopleandgreen.catalog.domain.model;

import bssm.plantshuman.peopleandgreen.domain.plant.AirPurification;
import bssm.plantshuman.peopleandgreen.domain.plant.ManageDifficulty;

public record PlantDetail(
        String plantId,
        String plantKoreanName,
        String plantEnglishName,
        String imageUrl,
        ManageDifficulty manageDifficulty,
        String waterPeriod,
        String appropriateTemperature,
        String appropriateHumidity,
        String sunlightRequirements,
        String size,
        String recommendedIndoorLocation,
        AirPurification airPurification,
        String petSafety,
        String description,
        boolean isFavorite
) {
}
