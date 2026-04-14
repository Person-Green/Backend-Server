package bssm.plantshuman.peopleandgreen.catalog.application.port.in;

import bssm.plantshuman.peopleandgreen.catalog.domain.model.FavoritePlantView;

import java.util.List;

public interface GetFavoritePlantsUseCase {

    List<FavoritePlantView> getFavoritePlants(Long userId);
}
