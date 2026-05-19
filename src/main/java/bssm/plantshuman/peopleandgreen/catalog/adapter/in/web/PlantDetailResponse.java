package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantDetail;

public record PlantDetailResponse(
        String plantId,
        String plantKoreanName,
        String plantEnglishName,
        String imageUrl,
        String manageDifficulty,
        String waterPeriod,
        String appropriateTemperature,
        String appropriateHumidity,
        String sunlightRequirements,
        String size,
        String recommendedIndoorLocation,
        String airPurification,
        String petSafety,
        String description,
        boolean isFavorite
) {

    public static PlantDetailResponse from(PlantDetail detail) {
        return new PlantDetailResponse(
                detail.plantId(),
                detail.plantKoreanName(),
                detail.plantEnglishName(),
                detail.imageUrl(),
                detail.manageDifficulty().name(),
                detail.waterPeriod(),
                detail.appropriateTemperature(),
                detail.appropriateHumidity(),
                detail.sunlightRequirements(),
                detail.size(),
                detail.recommendedIndoorLocation(),
                detail.airPurification().name(),
                detail.petSafety(),
                detail.description(),
                detail.isFavorite()
        );
    }
}
