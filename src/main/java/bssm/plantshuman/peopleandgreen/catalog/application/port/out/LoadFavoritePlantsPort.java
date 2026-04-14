package bssm.plantshuman.peopleandgreen.catalog.application.port.out;

import bssm.plantshuman.peopleandgreen.catalog.domain.model.FavoritePlantView;

import java.util.List;

public interface LoadFavoritePlantsPort {

    List<FavoritePlantView> loadFavoritePlants(Long userId);
}
