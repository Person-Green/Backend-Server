package bssm.plantshuman.peopleandgreen.catalog.adapter.in.web;

import java.util.List;

public record FavoritePlantsListResponse(
        List<FavoritePlantsResponse> favoritePlants
) {
    public static FavoritePlantsListResponse from(List<FavoritePlantsResponse> favoritePlants) {
        return new FavoritePlantsListResponse(favoritePlants);
    }
}
