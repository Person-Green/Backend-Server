package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import bssm.plantshuman.peopleandgreen.catalog.domain.model.FavoritePlantView;
import bssm.plantshuman.peopleandgreen.catalog.domain.model.PlantCatalogView;

public record FavoritePlantsResponse(
        String plantId,
        String plantKoreanName,
        String plantEnglishName,
        String size,
        String airPurification,
        String manageDifficulty,
        boolean isFavorite,
        Long favoriteCount
) {
    public static FavoritePlantsResponse from(PlantCatalogView view, Long favoriteCount) {
        return new FavoritePlantsResponse(
                view.plantId(),
                view.plantKoreanName(),
                view.plantEnglishName(),
                view.size(),
                view.airPurification().name(),
                view.manageDifficulty().name(),
                view.isFavorite(),
                favoriteCount
        );
    }

    public static FavoritePlantsResponse from(FavoritePlantView view) {
        return new FavoritePlantsResponse(
                view.plantId(),
                view.plantKoreanName(),
                view.plantEnglishName(),
                view.size(),
                view.airPurification().name(),
                view.manageDifficulty().name(),
                true,
                view.favoriteCount()
        );
    }
}
