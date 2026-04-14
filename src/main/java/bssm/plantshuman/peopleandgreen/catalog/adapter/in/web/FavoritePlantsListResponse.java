package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import bssm.plantshuman.peopleandgreen.catalog.domain.model.FavoritePlantView;

import java.util.List;

public record FavoritePlantsListResponse(
        List<FavoritePlantsResponse> favoritePlants
) {
    public static FavoritePlantsListResponse from(List<FavoritePlantView> views) {
        return new FavoritePlantsListResponse(
                views.stream().map(FavoritePlantsResponse::from).toList()
        );
    }
}
