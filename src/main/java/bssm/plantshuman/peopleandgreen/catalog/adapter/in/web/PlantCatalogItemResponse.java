package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogView;

public record PlantCatalogItemResponse(
        String plantId,
        String plantKoreanName,
        String plantEnglishName,
        String size,
        String airPurification,
        String manageDifficulty,
        boolean isFavorite
) {
    public static PlantCatalogItemResponse from(PlantCatalogView view) {
        return new PlantCatalogItemResponse(
                view.plantId(),
                view.plantKoreanName(),
                view.plantEnglishName(),
                view.size(),
                view.airPurification().name(),
                view.manageDifficulty().name(),
                view.isFavorite()
        );
    }
}
